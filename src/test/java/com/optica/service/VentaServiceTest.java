package com.optica.service;

import com.optica.dao.AccesorioDAO;
import com.optica.dao.ApartadoDAO;
import com.optica.dao.AroDAO;
import com.optica.dao.ClienteDAO;
import com.optica.dao.LunaDAO;
import com.optica.dao.MedicionDAO;
import com.optica.dao.VentaDAO;
import com.optica.dto.VentaRequest;
import com.optica.exception.NegocioException;
import com.optica.exception.StockInsuficienteException;
import com.optica.model.Accesorio;
import com.optica.model.Aro;
import com.optica.model.Cliente;
import com.optica.model.Luna;
import com.optica.model.Medicion;
import com.optica.model.TipoLuna;
import com.optica.model.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock ClienteDAO clienteDAO;
    @Mock MedicionDAO medicionDAO;
    @Mock AroDAO aroDAO;
    @Mock LunaDAO lunaDAO;
    @Mock AccesorioDAO accesorioDAO;
    @Mock VentaDAO ventaDAO;
    @Mock ApartadoDAO apartadoDAO;

    VentaService service;

    @BeforeEach
    void setUp() {
        service = new VentaService(clienteDAO, medicionDAO, aroDAO, lunaDAO, accesorioDAO, ventaDAO, apartadoDAO,
                new BigDecimal("0.18"));
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        when(clienteDAO.findById(1L)).thenReturn(Optional.of(cliente));
    }

    @Test
    void cotizarCalculaSubtotalIgvYTotal() {
        medicionDelCliente(1L);
        when(aroDAO.findById(10L)).thenReturn(Optional.of(aro(10L, "520.00", 3)));
        when(lunaDAO.findById(20L)).thenReturn(Optional.of(luna(20L, "120.00", 10)));
        when(accesorioDAO.findById(30L)).thenReturn(Optional.of(accesorio(30L, "8.00", 50)));

        // Accesorio repetido: se agrupa en una sola línea de 3 unidades
        Venta v = service.cotizar(new VentaRequest(1L, 5L, 10L, 20L, List.of(
                new VentaRequest.ItemAccesorio(30L, 1), new VentaRequest.ItemAccesorio(30L, 2))));

        // 520 + 2 × 120 + 3 × 8 = 784.00 ; IGV 18 % = 141.12 ; total 925.12
        assertThat(v.getDetalles()).hasSize(3);
        assertThat(v.getDetalles().get(1).getCantidad()).isEqualTo(VentaService.LUNAS_POR_PAR);
        assertThat(v.getDetalles().get(2).getCantidad()).isEqualTo(3);
        assertThat(v.getSubtotal()).isEqualByComparingTo("784.00");
        assertThat(v.getIgv()).isEqualByComparingTo("141.12");
        assertThat(v.getTotal()).isEqualByComparingTo("925.12");
        verify(ventaDAO, never()).save(any());
    }

    @Test
    void rechazaMedicionDeOtroCliente() {
        Medicion ajena = new Medicion();
        ajena.setId(5L);
        ajena.setClienteId(99L);
        when(medicionDAO.findById(5L)).thenReturn(Optional.of(ajena));

        assertThatThrownBy(() -> service.cotizar(new VentaRequest(1L, 5L, 10L, 20L, null)))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("no pertenece");
    }

    @Test
    void rechazaLunasSinStockParaElPar() {
        medicionDelCliente(1L);
        when(aroDAO.findById(10L)).thenReturn(Optional.of(aro(10L, "300.00", 5)));
        when(lunaDAO.findById(20L)).thenReturn(Optional.of(luna(20L, "120.00", 1)));

        assertThatThrownBy(() -> service.cotizar(new VentaRequest(1L, 5L, 10L, 20L, null)))
                .isInstanceOf(StockInsuficienteException.class);
    }

    @Test
    void registrarFallaSiElDescuentoDeStockNoAfectaFilas() {
        medicionDelCliente(1L);
        when(aroDAO.findById(10L)).thenReturn(Optional.of(aro(10L, "300.00", 5)));
        when(lunaDAO.findById(20L)).thenReturn(Optional.of(luna(20L, "120.00", 10)));
        when(aroDAO.descontarStock(10L, 1)).thenReturn(false); // otra venta se llevó la última unidad

        assertThatThrownBy(() -> service.registrar(new VentaRequest(1L, 5L, 10L, 20L, null), 7L))
                .isInstanceOf(StockInsuficienteException.class);
    }

    private void medicionDelCliente(Long clienteId) {
        Medicion m = new Medicion();
        m.setId(5L);
        m.setClienteId(clienteId);
        when(medicionDAO.findById(5L)).thenReturn(Optional.of(m));
    }

    private static Aro aro(Long id, String precio, int stock) {
        Aro a = new Aro();
        a.setId(id);
        a.setMarca("Ray-Ban");
        a.setModelo("Test");
        a.setPrecio(new BigDecimal(precio));
        a.setStock(stock);
        a.setActivo(true);
        return a;
    }

    private static Luna luna(Long id, String precio, int stock) {
        Luna l = new Luna();
        l.setId(id);
        l.setTipo(TipoLuna.MONOFOCAL);
        l.setMaterial("Resina");
        l.setIndice(new BigDecimal("1.56"));
        l.setPrecio(new BigDecimal(precio));
        l.setStock(stock);
        l.setActivo(true);
        return l;
    }

    private static Accesorio accesorio(Long id, String precio, int stock) {
        Accesorio a = new Accesorio();
        a.setId(id);
        a.setNombre("Paño");
        a.setPrecio(new BigDecimal(precio));
        a.setStock(stock);
        a.setActivo(true);
        return a;
    }
}
