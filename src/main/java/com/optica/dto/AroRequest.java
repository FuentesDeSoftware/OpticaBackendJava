package com.optica.dto;

import com.optica.model.Aro;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AroRequest(
        @NotBlank(message = "El código es obligatorio") @Size(max = 30) String codigo,
        @NotBlank(message = "La marca es obligatoria") @Size(max = 60) String marca,
        @NotBlank(message = "El modelo es obligatorio") @Size(max = 60) String modelo,
        @NotBlank(message = "El material es obligatorio") @Size(max = 40) String material,
        @NotBlank(message = "La forma es obligatoria") @Size(max = 40) String forma,
        @Size(max = 40) String color,
        @NotNull(message = "El precio es obligatorio") @DecimalMin(value = "0.00", message = "El precio no puede ser negativo") BigDecimal precio,
        @NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "El stock no puede ser negativo") Integer stock,
        @Size(max = 255) String imagenUrl) {

    public Aro toModel() {
        Aro a = new Aro();
        a.setCodigo(codigo.trim());
        a.setMarca(marca.trim());
        a.setModelo(modelo.trim());
        a.setMaterial(material.trim());
        a.setForma(forma.trim());
        a.setColor(color);
        a.setPrecio(precio);
        a.setStock(stock);
        a.setImagenUrl(imagenUrl == null || imagenUrl.isBlank() ? null : imagenUrl.trim());
        return a;
    }
}
