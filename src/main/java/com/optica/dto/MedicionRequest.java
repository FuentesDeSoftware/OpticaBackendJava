package com.optica.dto;

import com.optica.model.Medicion;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Valores en dioptrías; eje en grados (0-180); DIP en milímetros. */
public record MedicionRequest(
        @PastOrPresent(message = "La fecha no puede ser futura") LocalDate fecha,
        @NotNull(message = "Esfera OD obligatoria") @DecimalMin("-20.00") @DecimalMax("20.00") BigDecimal odEsfera,
        @NotNull @DecimalMin("-10.00") @DecimalMax("10.00") BigDecimal odCilindro,
        @NotNull @Min(0) @Max(180) Integer odEje,
        @NotNull @DecimalMin("0.00") @DecimalMax("4.00") BigDecimal odAdicion,
        @NotNull(message = "Esfera OI obligatoria") @DecimalMin("-20.00") @DecimalMax("20.00") BigDecimal oiEsfera,
        @NotNull @DecimalMin("-10.00") @DecimalMax("10.00") BigDecimal oiCilindro,
        @NotNull @Min(0) @Max(180) Integer oiEje,
        @NotNull @DecimalMin("0.00") @DecimalMax("4.00") BigDecimal oiAdicion,
        @DecimalMin("40.0") @DecimalMax("80.0") BigDecimal dip,
        @Size(max = 255) String observaciones) {

    public Medicion toModel() {
        Medicion m = new Medicion();
        m.setFecha(fecha);
        m.setOdEsfera(odEsfera);
        m.setOdCilindro(odCilindro);
        m.setOdEje(odEje);
        m.setOdAdicion(odAdicion);
        m.setOiEsfera(oiEsfera);
        m.setOiCilindro(oiCilindro);
        m.setOiEje(oiEje);
        m.setOiAdicion(oiAdicion);
        m.setDip(dip);
        m.setObservaciones(observaciones);
        return m;
    }
}
