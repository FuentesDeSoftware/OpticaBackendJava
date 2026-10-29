package com.optica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "Los nombres son obligatorios") @Size(max = 80) String nombres,
        @NotBlank(message = "Los apellidos son obligatorios") @Size(max = 80) String apellidos,
        @NotBlank(message = "El DNI es obligatorio") @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos") String dni,
        @NotBlank(message = "El teléfono es obligatorio") @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos") String telefono,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Correo inválido") @Size(max = 120) String email,
        @NotBlank(message = "La contraseña es obligatoria") @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres") String password) {
}
