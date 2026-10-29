package com.optica.dao;

import com.optica.model.Usuario;

import java.util.Optional;

public interface UsuarioDAO extends GenericDAO<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}
