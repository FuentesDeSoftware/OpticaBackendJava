package com.optica.dao;

import com.optica.TestcontainersConfiguration;
import com.optica.dto.VentaRequest;
import com.optica.exception.StockInsuficienteException;
import com.optica.model.Aro;
import com.optica.model.Venta;
import com.optica.service.VentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Prueba los DAO JDBC y la transacción de venta contra un PostgreSQL real (Testcontainers). */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class DaoJdbcIntegrationTest {

    @Autowired AroDAO aroDAO;
    @Autowired LunaDAO lunaDAO;
    @Autowired ClienteDAO clienteDAO;
    @Autowired MedicionDAO medicionDAO;
    @Autowired VentaService ventaService;

    @Test
    void crudDeAroConBorradoLogico() {
        Aro aro = nuevoAro("IT-CRUD", 7);
        aroDAO.save(aro);
        assertThat(aro.getId()).isNotNull();

        aro.setPrecio(new BigDecimal("199.90"));
        assertThat(aroDAO.update(aro)).isTrue();
        assertThat(aroDAO.findById(aro.getId())).get()
                .extracting(Aro::getPrecio).isEqualTo(new BigDecimal("199.90"));

        assertThat(aroDAO.delete(aro.getId())).isTrue();
        assertThat(aroDAO.findAll()).extracting(Aro::getCodigo).doesNotContain("IT-CRUD");
        assertThat(aroDAO.findById(aro.getId())).get().extracting(Aro::isActivo).isEqualTo(false);

        // El código de un aro dado de baja puede reutilizarse
        assertThat(aroDAO.save(nuevoAro("IT-CRUD", 1)).getId()).isNotEqualTo(aro.getId());
    }

    @Test
    void filtrosDelCatalogoYAlertasDeStock() {
        List<Aro> rayBanAcetato = aroDAO.findByFiltros("Ray-Ban", "Acetato", null, null, new BigDecimal("450"));
        assertThat(rayBanAcetato).extracting(Aro::getCodigo).containsExactly("RB-7047");

        assertThat(aroDAO.findStockBajo(5)).allMatch(a -> a.getStock() < 5).isNotEmpty();
        assertThat(aroDAO.findMarcas()).contains("Oakley", "Vogue");
    }

    @Test
    void descontarStockNoPermiteNegativos() {
        Aro aro = aroDAO.save(nuevoAro("IT-STOCK", 2));
        assertThat(aroDAO.descontarStock(aro.getId(), 2)).isTrue();
        assertThat(aroDAO.descontarStock(aro.getId(), 1)).isFalse();
        assertThat(aroDAO.findById(aro.getId()).orElseThrow().getStock()).isZero();
    }

    @Test
    void registrarVentaDescuentaStockDeAroYLunas() {
        Long clienteId = clienteDAO.findByDni("71234567").orElseThrow().getId();
        Long medicionId = medicionDAO.findByCliente(clienteId).getFirst().getId();
        Aro aro = aroDAO.save(nuevoAro("IT-VENTA", 3));
        Long lunaId = lunaDAO.findAll().getFirst().getId();
        int stockLuna = lunaDAO.findById(lunaId).orElseThrow().getStock();

        Venta venta = ventaService.registrar(new VentaRequest(clienteId, medicionId, aro.getId(), lunaId, null), 1L);

        assertThat(venta.getId()).isNotNull();
        assertThat(venta.getDetalles()).hasSize(2);
        assertThat(venta.getTotal()).isEqualByComparingTo(venta.getSubtotal().add(venta.getIgv()));
        assertThat(aroDAO.findById(aro.getId()).orElseThrow().getStock()).isEqualTo(2);
        assertThat(lunaDAO.findById(lunaId).orElseThrow().getStock()).isEqualTo(stockLuna - 2);
    }

    @Test
    void ventaSinStockNoModificaNada() {
        Long clienteId = clienteDAO.findByDni("71234567").orElseThrow().getId();
        Long medicionId = medicionDAO.findByCliente(clienteId).getFirst().getId();
        Aro aro = aroDAO.save(nuevoAro("IT-SINSTOCK", 0));
        Long lunaId = lunaDAO.findAll().getFirst().getId();
        int stockLuna = lunaDAO.findById(lunaId).orElseThrow().getStock();

        assertThatThrownBy(() -> ventaService.registrar(
                new VentaRequest(clienteId, medicionId, aro.getId(), lunaId, null), 1L))
                .isInstanceOf(StockInsuficienteException.class);
        assertThat(lunaDAO.findById(lunaId).orElseThrow().getStock()).isEqualTo(stockLuna);
    }

    private static Aro nuevoAro(String codigo, int stock) {
        Aro a = new Aro();
        a.setCodigo(codigo);
        a.setMarca("Test");
        a.setModelo(codigo);
        a.setMaterial("Metal");
        a.setForma("Redondo");
        a.setColor("Negro");
        a.setPrecio(new BigDecimal("150.00"));
        a.setStock(stock);
        return a;
    }
}
