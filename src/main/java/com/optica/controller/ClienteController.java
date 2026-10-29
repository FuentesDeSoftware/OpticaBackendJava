package com.optica.controller;

import com.optica.dto.ApartadoRequest;
import com.optica.model.Apartado;
import com.optica.model.Cliente;
import com.optica.model.Medicion;
import com.optica.security.UsuarioActual;
import com.optica.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Autogestión del cliente: siempre opera sobre el cliente del token, nunca sobre un id recibido. */
@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/perfil")
    public Cliente perfil(@AuthenticationPrincipal Jwt jwt) {
        return clienteService.obtener(UsuarioActual.de(jwt).perfilId());
    }

    @GetMapping("/mediciones")
    public List<Medicion> mediciones(@AuthenticationPrincipal Jwt jwt) {
        return clienteService.mediciones(UsuarioActual.de(jwt).perfilId());
    }

    @GetMapping("/apartados")
    public List<Apartado> apartados(@AuthenticationPrincipal Jwt jwt) {
        return clienteService.apartados(UsuarioActual.de(jwt).perfilId());
    }

    @PostMapping("/apartados")
    @ResponseStatus(HttpStatus.CREATED)
    public Apartado apartar(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ApartadoRequest req) {
        return clienteService.apartarAro(UsuarioActual.de(jwt).perfilId(), req.aroId());
    }

    @DeleteMapping("/apartados/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        clienteService.cancelarApartado(UsuarioActual.de(jwt).perfilId(), id);
    }
}
