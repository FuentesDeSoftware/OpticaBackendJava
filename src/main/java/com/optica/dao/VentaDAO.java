package com.optica.dao;

import com.optica.model.Venta;

import java.time.LocalDateTime;
import java.util.List;

/**
 * {@link #save(Object)} inserta la cabecera y todas sus líneas de detalle;
 * {@link #findById(Object)} devuelve la venta con su detalle.
 */
public interface VentaDAO extends GenericDAO<Venta, Long> {

    List<Venta> findByCliente(Long clienteId);

    /** Ventas con {@code desde <= fecha < hasta}, de la más antigua a la más reciente (sin detalle). */
    List<Venta> findByRango(LocalDateTime desde, LocalDateTime hasta);
}
