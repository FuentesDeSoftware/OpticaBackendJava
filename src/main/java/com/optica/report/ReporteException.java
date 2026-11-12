package com.optica.report;

/** Error al compilar, llenar o exportar una plantilla de JasperReports (HTTP 500). */
public class ReporteException extends RuntimeException {

    public ReporteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
