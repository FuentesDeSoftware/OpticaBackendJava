package com.optica.report;

import com.optica.model.DetalleVenta;
import com.optica.model.Medicion;
import com.optica.model.TipoProducto;
import com.optica.model.Venta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** Compila y llena cada plantilla con datos de ejemplo (sin base de datos). */
class ReporteGeneratorTest {

    private final ReporteGenerator generator = new ReporteGenerator();

    @Test
    void comprobanteDeVenta() {
        DetalleVenta d = new DetalleVenta();
        d.setTipoProducto(TipoProducto.ARO);
        d.setDescripcion("Ray-Ban RB-7047 Acetato");
        d.setCantidad(1);
        d.setPrecioUnitario(new BigDecimal("1500.00"));
        d.setSubtotal(new BigDecimal("1500.00"));

        byte[] pdf = generator.pdf("venta", Map.of(
                "NUMERO", 12L, "FECHA", LocalDateTime.now(), "CLIENTE", "Ana López", "CLIENTE_DNI", "0801199912345",
                "VENDEDOR", "Juan Pérez", "SUBTOTAL", new BigDecimal("1500.00"), "ISV", new BigDecimal("225.00"),
                "TOTAL", new BigDecimal("1725.00"), "TASA_ISV", new BigDecimal("0.15")), List.of(d));

        assertEsPdf(pdf);
    }

    @Test
    void ventasPorFechasSinResultados() {
        assertEsPdf(generator.pdf("ventas",
                Map.of("DESDE", LocalDate.of(2026, 1, 1), "HASTA", LocalDate.of(2026, 1, 31)), List.of()));
    }

    @Test
    void ventasPorFechas() {
        Venta v = new Venta();
        v.setId(3L);
        v.setFecha(LocalDateTime.now());
        v.setClienteNombre("Ana López");
        v.setVendedorNombre("Juan Pérez");
        v.setSubtotal(new BigDecimal("100.00"));
        v.setIgv(new BigDecimal("15.00"));
        v.setTotal(new BigDecimal("115.00"));

        assertEsPdf(generator.pdf("ventas",
                Map.of("DESDE", LocalDate.of(2026, 1, 1), "HASTA", LocalDate.of(2026, 1, 31)), List.of(v)));
    }

    @Test
    void inventario() {
        List<FilaInventario> filas = List.of(
                new FilaInventario("Aros", "RB-7047", "Ray-Ban RB-7047", new BigDecimal("1500.00"), 2, true),
                new FilaInventario("Lunas", "LU-01", "Antirreflejo", new BigDecimal("400.00"), 20, false));

        assertEsPdf(generator.pdf("inventario", Map.of("UMBRAL", 5), filas));
    }

    @Test
    void receta() {
        Medicion m = new Medicion();
        m.setId(8L);
        m.setFecha(LocalDate.now());
        m.setOdEsfera(new BigDecimal("-1.25"));
        m.setOdCilindro(new BigDecimal("-0.50"));
        m.setOdEje(90);
        m.setOiEsfera(new BigDecimal("1.00"));
        m.setDip(new BigDecimal("62.0"));

        assertEsPdf(generator.pdf("receta", Map.of("CLIENTE", "Ana López", "CLIENTE_DNI", "0801199912345"), List.of(m)));
    }

    static void assertEsPdf(byte[] pdf) {
        assertThat(new String(pdf, 0, 5, StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
    }
}
