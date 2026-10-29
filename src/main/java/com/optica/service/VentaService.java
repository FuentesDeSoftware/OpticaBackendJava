package com.optica.service;

import com.optica.dao.AccesorioDAO;
import com.optica.dao.ApartadoDAO;
import com.optica.dao.AroDAO;
import com.optica.dao.ClienteDAO;
import com.optica.dao.LunaDAO;
import com.optica.dao.MedicionDAO;
import com.optica.dao.ProductoDAO;
import com.optica.dao.VentaDAO;
import com.optica.dto.VentaRequest;
import com.optica.exception.NegocioException;
import com.optica.exception.RecursoNoEncontradoException;
import com.optica.exception.StockInsuficienteException;
import com.optica.model.Accesorio;
import com.optica.model.Aro;
import com.optica.model.DetalleVenta;
import com.optica.model.EstadoApartado;
import com.optica.model.Luna;
import com.optica.model.Medicion;
import com.optica.model.Producto;
import com.optica.model.TipoProducto;
import com.optica.model.Venta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registro de ventas: valida las asociaciones obligatorias (cliente, medición, aro y luna),
 * calcula subtotal, IGV y total, guarda la venta y descuenta el stock en una única transacción.
 */
@Service
public class VentaService {

    /** Un par de lentes lleva dos lunas (ojo derecho e izquierdo). */
    static final int LUNAS_POR_PAR = 2;

    private final ClienteDAO clienteDAO;
    private final MedicionDAO medicionDAO;
    private final AroDAO aroDAO;
    private final LunaDAO lunaDAO;
    private final AccesorioDAO accesorioDAO;
    private final VentaDAO ventaDAO;
    private final ApartadoDAO apartadoDAO;
    private final BigDecimal tasaIgv;

    public VentaService(ClienteDAO clienteDAO, MedicionDAO medicionDAO, AroDAO aroDAO, LunaDAO lunaDAO,
                        AccesorioDAO accesorioDAO, VentaDAO ventaDAO, ApartadoDAO apartadoDAO,
                        @Value("${optica.igv}") BigDecimal tasaIgv) {
        this.clienteDAO = clienteDAO;
        this.medicionDAO = medicionDAO;
        this.aroDAO = aroDAO;
        this.lunaDAO = lunaDAO;
        this.accesorioDAO = accesorioDAO;
        this.ventaDAO = ventaDAO;
        this.apartadoDAO = apartadoDAO;
        this.tasaIgv = tasaIgv;
    }

    public BigDecimal getTasaIgv() {
        return tasaIgv;
    }

    /** Calcula la venta sin guardarla (vista previa en el formulario). */
    public Venta cotizar(VentaRequest req) {
        return construir(req);
    }

    @Transactional
    public Venta registrar(VentaRequest req, Long vendedorId) {
        Venta venta = construir(req);
        venta.setVendedorId(vendedorId);
        ventaDAO.save(venta);

        // Actualización automática del inventario. Si algún descuento falla se lanza una
        // excepción y la transacción revierte la venta y los descuentos ya hechos.
        for (DetalleVenta d : venta.getDetalles()) {
            boolean ok = switch (d.getTipoProducto()) {
                case ARO -> aroDAO.descontarStock(d.getProductoId(), d.getCantidad());
                case LUNA -> lunaDAO.descontarStock(d.getProductoId(), d.getCantidad());
                case ACCESORIO -> accesorioDAO.descontarStock(d.getProductoId(), d.getCantidad());
            };
            if (!ok) {
                throw new StockInsuficienteException(d.getDescripcion());
            }
        }

        apartadoDAO.findPendiente(venta.getClienteId(), venta.getAroId())
                .ifPresent(ap -> apartadoDAO.actualizarEstado(ap.getId(), EstadoApartado.VENDIDO));

        return ventaDAO.findById(venta.getId()).orElse(venta);
    }

    public List<Venta> listar() {
        return ventaDAO.findAll();
    }

    public Venta obtener(Long id) {
        return ventaDAO.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Venta", id));
    }

    private Venta construir(VentaRequest req) {
        clienteDAO.findById(req.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", req.clienteId()));
        Medicion medicion = medicionDAO.findById(req.medicionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Medición", req.medicionId()));
        if (!medicion.getClienteId().equals(req.clienteId())) {
            throw new NegocioException("La medición seleccionada no pertenece al cliente");
        }

        List<DetalleVenta> detalles = new ArrayList<>();
        Aro aro = productoDisponible(aroDAO, req.aroId(), 1, "Aro");
        detalles.add(linea(TipoProducto.ARO, aro, 1));
        Luna luna = productoDisponible(lunaDAO, req.lunaId(), LUNAS_POR_PAR, "Luna");
        detalles.add(linea(TipoProducto.LUNA, luna, LUNAS_POR_PAR));

        // Agrupa accesorios repetidos en una sola línea
        Map<Long, Integer> accesorios = new LinkedHashMap<>();
        for (VentaRequest.ItemAccesorio item : req.accesoriosOVacio()) {
            accesorios.merge(item.accesorioId(), item.cantidad(), Integer::sum);
        }
        accesorios.forEach((id, cantidad) -> {
            Accesorio acc = productoDisponible(accesorioDAO, id, cantidad, "Accesorio");
            detalles.add(linea(TipoProducto.ACCESORIO, acc, cantidad));
        });

        Venta venta = new Venta();
        venta.setClienteId(req.clienteId());
        venta.setMedicionId(req.medicionId());
        venta.setAroId(aro.getId());
        venta.setLunaId(luna.getId());
        venta.setDetalles(detalles);
        calcularTotales(venta);
        return venta;
    }

    /** subtotal = suma de líneas; IGV = subtotal × tasa; total = subtotal + IGV (redondeo a 2 decimales). */
    void calcularTotales(Venta venta) {
        BigDecimal subtotal = venta.getDetalles().stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal igv = subtotal.multiply(tasaIgv).setScale(2, RoundingMode.HALF_UP);
        venta.setSubtotal(subtotal);
        venta.setIgv(igv);
        venta.setTotal(subtotal.add(igv));
    }

    private <T extends Producto> T productoDisponible(ProductoDAO<T> dao, Long id, int cantidad, String nombre) {
        T producto = dao.findById(id)
                .filter(Producto::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException(nombre, id));
        if (producto.getStock() < cantidad) {
            throw new StockInsuficienteException(producto.getEtiqueta()
                    + " (disponible: " + producto.getStock() + ", solicitado: " + cantidad + ")");
        }
        return producto;
    }

    private static DetalleVenta linea(TipoProducto tipo, Producto producto, int cantidad) {
        DetalleVenta d = new DetalleVenta();
        d.setTipoProducto(tipo);
        d.setProductoId(producto.getId());
        d.setDescripcion(producto.getEtiqueta());
        d.setCantidad(cantidad);
        d.setPrecioUnitario(producto.getPrecio());
        d.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
        return d;
    }
}
