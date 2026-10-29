package com.optica.dao;

import java.util.List;
import java.util.Optional;

/**
 * Contrato CRUD genérico que comparten todos los DAO.
 *
 * @param <T>  entidad del modelo
 * @param <ID> tipo de la clave primaria
 */
public interface GenericDAO<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();

    /** Inserta la entidad y devuelve la misma instancia con su id generado. */
    T save(T entidad);

    /** @return true si se actualizó algún registro */
    boolean update(T entidad);

    /** @return true si se eliminó algún registro */
    boolean delete(ID id);
}
