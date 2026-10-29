package com.optica.exception;

/** No hay unidades suficientes para completar la venta (HTTP 409). */
public class StockInsuficienteException extends ConflictoException {

    public StockInsuficienteException(String producto) {
        super("Stock insuficiente para: " + producto);
    }
}
