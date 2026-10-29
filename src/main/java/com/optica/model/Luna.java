package com.optica.model;

import java.math.BigDecimal;

/**
 * Luna (cristal) oftálmica.
 */
public class Luna extends Producto {

    private TipoLuna tipo;
    private String material;
    private BigDecimal indice;
    private String tratamiento;

    public Luna() {
    }

    public TipoLuna getTipo() {
        return tipo;
    }

    public void setTipo(TipoLuna tipo) {
        this.tipo = tipo;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public BigDecimal getIndice() {
        return indice;
    }

    public void setIndice(BigDecimal indice) {
        this.indice = indice;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    @Override
    public String getEtiqueta() {
        return "Luna " + tipo + " " + material + " " + indice;
    }
}
