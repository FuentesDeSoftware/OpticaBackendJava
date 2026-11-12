package com.optica.controller;

import com.optica.security.UsuarioActual;
import com.optica.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/** Reportes PDF generados con JasperReports. Se muestran en el navegador (inline). */
@RestController
public class ReporteController {

    private final ReporteService reportes;

    public ReporteController(ReporteService reportes) {
        this.reportes = reportes;
    }

    @GetMapping("/api/vendedor/reportes/ventas/{id}")
    public ResponseEntity<byte[]> comprobanteVenta(@PathVariable Long id) {
        return pdf(reportes.comprobanteVenta(id), "venta-" + id + ".pdf");
    }

    @GetMapping("/api/vendedor/reportes/ventas")
    public ResponseEntity<byte[]> ventasPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return pdf(reportes.ventasPorFechas(desde, hasta), "ventas-" + desde + "-a-" + hasta + ".pdf");
    }

    @GetMapping("/api/vendedor/reportes/inventario")
    public ResponseEntity<byte[]> inventario() {
        return pdf(reportes.inventario(), "inventario-" + LocalDate.now() + ".pdf");
    }

    @GetMapping("/api/vendedor/reportes/mediciones/{id}")
    public ResponseEntity<byte[]> recetaVendedor(@PathVariable Long id) {
        return pdf(reportes.receta(id, null), "receta-" + id + ".pdf");
    }

    /** El cliente solo puede descargar sus propias recetas. */
    @GetMapping("/api/cliente/reportes/mediciones/{id}")
    public ResponseEntity<byte[]> recetaCliente(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return pdf(reportes.receta(id, UsuarioActual.de(jwt).perfilId()), "receta-" + id + ".pdf");
    }

    private static ResponseEntity<byte[]> pdf(byte[] contenido, String nombreArchivo) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(nombreArchivo).build().toString())
                .body(contenido);
    }
}
