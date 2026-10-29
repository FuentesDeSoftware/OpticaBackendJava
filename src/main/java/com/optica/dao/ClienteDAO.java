package com.optica.dao;

import com.optica.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteDAO extends GenericDAO<Cliente, Long> {

    Optional<Cliente> findByUsuarioId(Long usuarioId);

    Optional<Cliente> findByDni(String dni);

    boolean existsByDni(String dni);

    /** Busca por coincidencia parcial de DNI, nombres o apellidos. */
    List<Cliente> buscar(String texto);
}
