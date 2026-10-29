package com.optica.dto;

import com.optica.model.Rol;

public record AuthResponse(String token, Rol rol, String nombre, String email) {
}
