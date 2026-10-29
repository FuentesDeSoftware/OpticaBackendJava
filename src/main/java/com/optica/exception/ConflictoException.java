package com.optica.exception;

/** El recurso ya existe o su estado no permite la operación (HTTP 409). */
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
