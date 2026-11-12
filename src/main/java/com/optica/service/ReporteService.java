package com.optica.service;

import com.optica.dao.MedicionDAO;
import com.optica.dao.VentaDAO;
import com.optica.exception.NegocioException;
import com.optica.exception.RecursoNoEncontradoException;
import com.optica.model.Cliente;
import com.optica.model.Medicion;
import com.optica.model.Producto;
import com.optica.model.Venta;
import com.optica.report.FilaInventario;
import com.optica.report.ReporteGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reportes PDF: obtiene los datos con los services y DAO existentes y los entrega a
 * {@link ReporteGenerator} como colecciones de beans.
 */
@Service
public class ReporteService {

    private final VentaService ventaService;
    private final InventarioService inventarioService;
    private final ClienteService clienteService;
    private final VentaDAO ventaDAO;
    private final MedicionDAO medicionDAO;
    private final ReporteGenerator generator;
    private final int umbralStock;

    public ReporteService(VentaService ventaService, InventarioService inventarioService,
                          ClienteService clienteService, VentaDAO ventaDAO, MedicionDAO medicionDAO,
                          ReporteGenerator generator, @Value("${optica.stock.umbral}") int umbralStock) {
        this.ventaService = ventaService;
        this.inventarioService = inventarioService;
        this.clienteService = clienteService;
        this.ventaDAO = ventaDAO;
        this.medicionDAO = medicionDAO;
        this.generator = generator;
        this.umbralStock = umbralStock;
    }

    public byte[] comprobanteVenta(Long ventaId) {
        Venta venta = ventaService.obtener(ventaId);
        Cliente cliente = clienteService.obtener(venta.getClienteId());
        // Map.of no admite valores null; HashMap sí (p. ej. un DNI vacío)
        Map<String, Object> params = new HashMap<>();
        params.put("NUMERO", venta.getId());
        params.put("FECHA", venta.getFecha());
        params.put("CLIENTE", venta.getClienteNombre());
        params.put("CLIENTE_DNI", cliente.getDni());
        params.put("VENDEDOR", venta.getVendedorNombre());
        params.put("SUBTOTAL", venta.getSubtotal());
        params.put("ISV", venta.getIgv());
        params.put("TOTAL", venta.getTotal());
        params.put("TASA_ISV", ventaService.getTasaIgv());
        return generator.pdf("venta", params, venta.getDetalles());
    }

    /** Ventas entre dos fechas, ambas incluidas. */
    public byte[] ventasPorFechas(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new NegocioException("La fecha inicial no puede ser posterior a la final");
        }
        List<Venta> ventas = ventaDAO.findByRango(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay());
        return generator.pdf("ventas", Map.of("DESDE", desde, "HASTA", hasta), ventas);
    }

    public byte[] inventario() {
        List<FilaInventario> filas = new ArrayList<>();
        agregar(filas, "Aros", inventarioService.listarAros());
        agregar(filas, "Lunas", inventarioService.listarLunas());
        agregar(filas, "Accesorios", inventarioService.listarAccesorios());
        return generator.pdf("inventario", Map.of("UMBRAL", umbralStock), filas);
    }

    /**
     * @param clienteId si no es null, la medición debe pertenecer a ese cliente
     *                  (un cliente solo puede descargar sus propias recetas)
     */
    public byte[] receta(Long medicionId, Long clienteId) {
        Medicion medicion = medicionDAO.findById(medicionId)
                .filter(m -> clienteId == null || m.getClienteId().equals(clienteId))
                .orElseThrow(() -> new RecursoNoEncontradoException("Medición", medicionId));
        Cliente cliente = clienteService.obtener(medicion.getClienteId());
        Map<String, Object> params = new HashMap<>();
        params.put("CLIENTE", cliente.getNombreCompleto());
        params.put("CLIENTE_DNI", cliente.getDni());
        return generator.pdf("receta", params, List.of(medicion));
    }

    private void agregar(List<FilaInventario> filas, String tipo, List<? extends Producto> productos) {
        for (Producto p : productos) {
            filas.add(new FilaInventario(tipo, p.getCodigo(), p.getEtiqueta(), p.getPrecio(), p.getStock(),
                    p.getStock() < umbralStock));
        }
    }
}
