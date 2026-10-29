package com.optica.controller;

import com.optica.dto.AccesorioRequest;
import com.optica.dto.AlertasStockResponse;
import com.optica.dto.AroRequest;
import com.optica.dto.LunaRequest;
import com.optica.model.Accesorio;
import com.optica.model.Aro;
import com.optica.model.Luna;
import com.optica.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Gestión de stock (solo vendedores). */
@RestController
@RequestMapping("/api/vendedor")
public class InventarioController {

    private final InventarioService inventario;

    public InventarioController(InventarioService inventario) {
        this.inventario = inventario;
    }

    @GetMapping("/alertas-stock")
    public AlertasStockResponse alertas() {
        return inventario.alertasStock();
    }

    // ---------- Aros ----------
    @GetMapping("/aros")
    public List<Aro> listarAros() {
        return inventario.listarAros();
    }

    @GetMapping("/aros/{id}")
    public Aro obtenerAro(@PathVariable Long id) {
        return inventario.obtenerAro(id);
    }

    @PostMapping("/aros")
    @ResponseStatus(HttpStatus.CREATED)
    public Aro crearAro(@Valid @RequestBody AroRequest req) {
        return inventario.crearAro(req.toModel());
    }

    @PutMapping("/aros/{id}")
    public Aro actualizarAro(@PathVariable Long id, @Valid @RequestBody AroRequest req) {
        return inventario.actualizarAro(id, req.toModel());
    }

    @DeleteMapping("/aros/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarAro(@PathVariable Long id) {
        inventario.eliminarAro(id);
    }

    // ---------- Lunas ----------
    @GetMapping("/lunas")
    public List<Luna> listarLunas() {
        return inventario.listarLunas();
    }

    @GetMapping("/lunas/{id}")
    public Luna obtenerLuna(@PathVariable Long id) {
        return inventario.obtenerLuna(id);
    }

    @PostMapping("/lunas")
    @ResponseStatus(HttpStatus.CREATED)
    public Luna crearLuna(@Valid @RequestBody LunaRequest req) {
        return inventario.crearLuna(req.toModel());
    }

    @PutMapping("/lunas/{id}")
    public Luna actualizarLuna(@PathVariable Long id, @Valid @RequestBody LunaRequest req) {
        return inventario.actualizarLuna(id, req.toModel());
    }

    @DeleteMapping("/lunas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarLuna(@PathVariable Long id) {
        inventario.eliminarLuna(id);
    }

    // ---------- Accesorios ----------
    @GetMapping("/accesorios")
    public List<Accesorio> listarAccesorios() {
        return inventario.listarAccesorios();
    }

    @GetMapping("/accesorios/{id}")
    public Accesorio obtenerAccesorio(@PathVariable Long id) {
        return inventario.obtenerAccesorio(id);
    }

    @PostMapping("/accesorios")
    @ResponseStatus(HttpStatus.CREATED)
    public Accesorio crearAccesorio(@Valid @RequestBody AccesorioRequest req) {
        return inventario.crearAccesorio(req.toModel());
    }

    @PutMapping("/accesorios/{id}")
    public Accesorio actualizarAccesorio(@PathVariable Long id, @Valid @RequestBody AccesorioRequest req) {
        return inventario.actualizarAccesorio(id, req.toModel());
    }

    @DeleteMapping("/accesorios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarAccesorio(@PathVariable Long id) {
        inventario.eliminarAccesorio(id);
    }
}
