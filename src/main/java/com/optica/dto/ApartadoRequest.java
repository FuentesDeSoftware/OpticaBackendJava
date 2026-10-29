package com.optica.dto;

import jakarta.validation.constraints.NotNull;

public record ApartadoRequest(@NotNull(message = "Seleccione un aro") Long aroId) {
}
