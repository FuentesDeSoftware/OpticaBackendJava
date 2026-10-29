package com.optica.controller;

import com.optica.dto.VentaRequest;
import com.optica.model.Venta;
import com.optica.security.UsuarioActual;
import com.optica.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vendedor/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    /** Vista previa de subtotal, IGV y total sin registrar la venta. */
    @PostMapping("/cotizar")
    public Venta cotizar(@Valid @RequestBody VentaRequest req) {
        return ventaService.cotizar(req);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Venta registrar(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody VentaRequest req) {
        return ventaService.registrar(req, UsuarioActual.de(jwt).perfilId());
    }

    @GetMapping
    public List<Venta> listar() {
        return ventaService.listar();
    }

    @GetMapping("/{id}")
    public Venta obtener(@PathVariable Long id) {
        return ventaService.obtener(id);
    }
}
