package com.optica.dao;

import com.optica.model.Venta;

import java.util.List;

/**
 * {@link #save(Object)} inserta la cabecera y todas sus líneas de detalle;
 * {@link #findById(Object)} devuelve la venta con su detalle.
 */
public interface VentaDAO extends GenericDAO<Venta, Long> {

    List<Venta> findByCliente(Long clienteId);
}
