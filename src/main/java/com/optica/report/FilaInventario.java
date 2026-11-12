package com.optica.report;

import java.math.BigDecimal;

/**
 * Una fila del reporte de inventario (aro, luna o accesorio).
 * Es una clase con getters y no un record porque JRBeanCollectionDataSource lee propiedades JavaBean.
 */
public class FilaInventario {

    private final String tipo;
    private final String codigo;
    private final String descripcion;
    private final BigDecimal precio;
    private final int stock;
    private final boolean stockBajo;

    public FilaInventario(String tipo, String codigo, String descripcion, BigDecimal precio, int stock,
                          boolean stockBajo) {
        this.tipo = tipo;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.stockBajo = stockBajo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public boolean isStockBajo() {
        return stockBajo;
    }
}
