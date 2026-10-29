package com.optica.model;

import java.math.BigDecimal;

/**
 * Atributos comunes a todo producto con inventario (aros, lunas y accesorios).
 */
public abstract class Producto {

    private Long id;
    private String codigo;
    private BigDecimal precio;
    private int stock;
    private boolean activo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /** Texto corto usado en el detalle de una venta. */
    public abstract String getEtiqueta();
}
