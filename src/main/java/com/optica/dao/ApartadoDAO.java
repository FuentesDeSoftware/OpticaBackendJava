package com.optica.dao;

import com.optica.model.Apartado;
import com.optica.model.EstadoApartado;

import java.util.List;
import java.util.Optional;

public interface ApartadoDAO extends GenericDAO<Apartado, Long> {

    /** Apartados de un cliente con los datos del aro cargados. */
    List<Apartado> findByCliente(Long clienteId);

    Optional<Apartado> findPendiente(Long clienteId, Long aroId);

    boolean actualizarEstado(Long id, EstadoApartado estado);
}
