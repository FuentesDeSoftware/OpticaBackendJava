package com.optica.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VentaRequest(
        @NotNull(message = "El cliente es obligatorio") Long clienteId,
        @NotNull(message = "La medición es obligatoria") Long medicionId,
        @NotNull(message = "El aro es obligatorio") Long aroId,
        @NotNull(message = "La luna es obligatoria") Long lunaId,
        @Valid List<ItemAccesorio> accesorios) {

    public record ItemAccesorio(
            @NotNull Long accesorioId,
            @NotNull @Min(value = 1, message = "La cantidad mínima es 1") Integer cantidad) {
    }

    public List<ItemAccesorio> accesoriosOVacio() {
        return accesorios == null ? List.of() : accesorios;
    }
}
