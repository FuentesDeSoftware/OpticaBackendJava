package com.optica.controller;

import com.optica.dto.MedicionRequest;
import com.optica.model.Apartado;
import com.optica.model.Cliente;
import com.optica.model.Medicion;
import com.optica.security.UsuarioActual;
import com.optica.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Consulta de clientes y registro de mediciones por parte del vendedor. */
@RestController
@RequestMapping("/api/vendedor/clientes")
public class VendedorClienteController {

    private final ClienteService clienteService;

    public VendedorClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<Cliente> buscar(@RequestParam(required = false) String q) {
        return clienteService.buscar(q);
    }

    @GetMapping("/{id}")
    public Cliente obtener(@PathVariable Long id) {
        return clienteService.obtener(id);
    }

    @GetMapping("/{id}/mediciones")
    public List<Medicion> mediciones(@PathVariable Long id) {
        return clienteService.mediciones(id);
    }

    @PostMapping("/{id}/mediciones")
    @ResponseStatus(HttpStatus.CREATED)
    public Medicion registrarMedicion(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                      @Valid @RequestBody MedicionRequest req) {
        return clienteService.registrarMedicion(id, UsuarioActual.de(jwt).perfilId(), req);
    }

    @GetMapping("/{id}/apartados")
    public List<Apartado> apartados(@PathVariable Long id) {
        return clienteService.apartados(id);
    }
}
