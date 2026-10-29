package com.optica.exception;

import java.sql.SQLException;

/** Envuelve las SQLException de la capa DAO en una excepción no comprobada. */
public class DAOException extends RuntimeException {

    private static final String UNIQUE_VIOLATION = "23505";
    private static final String FOREIGN_KEY_VIOLATION = "23503";

    private final String sqlState;

    public DAOException(String mensaje, SQLException causa) {
        super(mensaje + ": " + causa.getMessage(), causa);
        this.sqlState = causa.getSQLState();
    }

    public boolean esDuplicado() {
        return UNIQUE_VIOLATION.equals(sqlState);
    }

    public boolean esViolacionFk() {
        return FOREIGN_KEY_VIOLATION.equals(sqlState);
    }
}
