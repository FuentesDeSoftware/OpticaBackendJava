package com.optica.dao;

import com.optica.model.Producto;

import java.util.List;

/**
 * Operaciones comunes a los productos con inventario.
 * {@link #delete(Object)} realiza un borrado lógico (activo = false)
 * para no romper el historial de ventas.
 */
public interface ProductoDAO<T extends Producto> extends GenericDAO<T, Long> {

    /** Productos activos con stock menor al umbral indicado. */
    List<T> findStockBajo(int umbral);

    /**
     * Resta unidades del stock solo si hay suficientes.
     *
     * @return false si el stock disponible es menor a la cantidad solicitada
     */
    boolean descontarStock(Long id, int cantidad);
}
