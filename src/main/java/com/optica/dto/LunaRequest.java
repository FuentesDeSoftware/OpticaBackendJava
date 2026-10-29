package com.optica.dto;

import com.optica.model.Luna;
import com.optica.model.TipoLuna;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record LunaRequest(
        @NotBlank(message = "El código es obligatorio") @Size(max = 30) String codigo,
        @NotNull(message = "El tipo es obligatorio") TipoLuna tipo,
        @NotBlank(message = "El material es obligatorio") @Size(max = 40) String material,
        @NotNull(message = "El índice es obligatorio") @DecimalMin("1.40") @DecimalMax("1.90") BigDecimal indice,
        @Size(max = 80) String tratamiento,
        @NotNull(message = "El precio es obligatorio") @DecimalMin(value = "0.00", message = "El precio no puede ser negativo") BigDecimal precio,
        @NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "El stock no puede ser negativo") Integer stock) {

    public Luna toModel() {
        Luna l = new Luna();
        l.setCodigo(codigo.trim());
        l.setTipo(tipo);
        l.setMaterial(material.trim());
        l.setIndice(indice);
        l.setTratamiento(tratamiento);
        l.setPrecio(precio);
        l.setStock(stock);
        return l;
    }
}
