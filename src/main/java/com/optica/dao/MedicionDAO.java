package com.optica.dao;

import com.optica.model.Medicion;

import java.util.List;

public interface MedicionDAO extends GenericDAO<Medicion, Long> {

    /** Historial de mediciones de un cliente, de la más reciente a la más antigua. */
    List<Medicion> findByCliente(Long clienteId);
}
