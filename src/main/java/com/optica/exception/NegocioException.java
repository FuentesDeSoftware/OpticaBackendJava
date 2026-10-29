package com.optica.exception;

/** Regla de negocio incumplida (HTTP 400). */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
