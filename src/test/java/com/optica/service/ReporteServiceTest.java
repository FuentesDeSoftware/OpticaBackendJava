package com.optica.service;

import com.optica.dao.MedicionDAO;
import com.optica.dao.VentaDAO;
import com.optica.exception.NegocioException;
import com.optica.exception.RecursoNoEncontradoException;
import com.optica.model.Cliente;
import com.optica.model.Medicion;
import com.optica.report.ReporteGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock VentaService ventaService;
    @Mock InventarioService inventarioService;
    @Mock ClienteService clienteService;
    @Mock VentaDAO ventaDAO;
    @Mock MedicionDAO medicionDAO;
    @Mock ReporteGenerator generator;

    ReporteService service;

    @BeforeEach
    void setUp() {
        service = new ReporteService(ventaService, inventarioService, clienteService, ventaDAO, medicionDAO,
                generator, 5);
    }

    @Test
    void clienteNoPuedeDescargarRecetaAjena() {
        Medicion m = new Medicion();
        m.setId(8L);
        m.setClienteId(2L);
        when(medicionDAO.findById(8L)).thenReturn(Optional.of(m));

        assertThatThrownBy(() -> service.receta(8L, 1L)).isInstanceOf(RecursoNoEncontradoException.class);
        verifyNoInteractions(generator);
    }

    @Test
    void vendedorDescargaCualquierReceta() {
        Medicion m = new Medicion();
        m.setId(8L);
        m.setClienteId(2L);
        Cliente c = new Cliente();
        c.setNombres("Ana");
        c.setApellidos("López");
        when(medicionDAO.findById(8L)).thenReturn(Optional.of(m));
        when(clienteService.obtener(2L)).thenReturn(c);
        when(generator.pdf(eq("receta"), anyMap(), eq(List.of(m)))).thenReturn(new byte[]{1});

        assertThat(service.receta(8L, null)).containsExactly(1);
    }

    @Test
    void rangoDeFechasInvertidoEsInvalido() {
        assertThatThrownBy(() -> service.ventasPorFechas(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)))
                .isInstanceOf(NegocioException.class);
        verifyNoInteractions(ventaDAO);
    }

    @Test
    void rangoDeFechasIncluyeElDiaFinal() {
        when(generator.pdf(eq("ventas"), anyMap(), any())).thenReturn(new byte[]{1});

        service.ventasPorFechas(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        verify(ventaDAO).findByRango(
                LocalDate.of(2026, 1, 1).atStartOfDay(), LocalDate.of(2026, 2, 1).atStartOfDay());
    }
}
