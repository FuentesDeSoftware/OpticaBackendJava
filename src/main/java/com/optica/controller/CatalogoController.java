package com.optica.controller;

import com.optica.dto.FiltrosCatalogoResponse;
import com.optica.model.Aro;
import com.optica.service.CatalogoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/** Catálogo de aros disponible para cualquier usuario autenticado. */
@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/aros")
    public List<Aro> aros(@RequestParam(required = false) String marca,
                          @RequestParam(required = false) String material,
                          @RequestParam(required = false) String forma,
                          @RequestParam(required = false) BigDecimal precioMin,
                          @RequestParam(required = false) BigDecimal precioMax) {
        return catalogoService.buscarAros(marca, material, forma, precioMin, precioMax);
    }

    @GetMapping("/filtros")
    public FiltrosCatalogoResponse filtros() {
        return catalogoService.filtros();
    }
}
