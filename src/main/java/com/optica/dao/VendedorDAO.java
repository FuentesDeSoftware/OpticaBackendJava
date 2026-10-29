package com.optica.dao;

import com.optica.model.Vendedor;

import java.util.Optional;

public interface VendedorDAO extends GenericDAO<Vendedor, Long> {

    Optional<Vendedor> findByUsuarioId(Long usuarioId);
}
