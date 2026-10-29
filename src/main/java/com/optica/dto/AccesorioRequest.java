package com.optica.dto;

import com.optica.model.Accesorio;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AccesorioRequest(
        @NotBlank(message = "El código es obligatorio") @Size(max = 30) String codigo,
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 80) String nombre,
        @Size(max = 255) String descripcion,
        @NotNull(message = "El precio es obligatorio") @DecimalMin(value = "0.00", message = "El precio no puede ser negativo") BigDecimal precio,
        @NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "El stock no puede ser negativo") Integer stock) {

    public Accesorio toModel() {
        Accesorio a = new Accesorio();
        a.setCodigo(codigo.trim());
        a.setNombre(nombre.trim());
        a.setDescripcion(descripcion);
        a.setPrecio(precio);
        a.setStock(stock);
        return a;
    }
}
