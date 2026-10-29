package com.optica.exception;

/** HTTP 404. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super(recurso + " no encontrado (id " + id + ")");
    }
}
