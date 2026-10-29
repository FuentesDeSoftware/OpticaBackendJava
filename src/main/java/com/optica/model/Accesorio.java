package com.optica.model;

/**
 * Accesorio de venta libre (estuches, paños, líquidos, etc.).
 */
public class Accesorio extends Producto {

    private String nombre;
    private String descripcion;

    public Accesorio() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String getEtiqueta() {
        return nombre;
    }
}
