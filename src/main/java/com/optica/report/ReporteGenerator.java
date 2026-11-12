package com.optica.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Genera PDF a partir de las plantillas {@code classpath:reportes/<nombre>.jrxml}.
 * Los datos llegan como colección de beans (obtenidos con los DAO), no como conexión JDBC.
 */
@Component
public class ReporteGenerator {

    static final Locale LOCALE_HN = Locale.of("es", "HN");

    /** Cada plantilla se compila una sola vez. */
    private final Map<String, JasperReport> compilados = new ConcurrentHashMap<>();

    public byte[] pdf(String plantilla, Map<String, Object> params, Collection<?> filas) {
        Map<String, Object> parametros = new HashMap<>(params);
        parametros.put(JRParameter.REPORT_LOCALE, LOCALE_HN);
        try {
            JasperPrint print = JasperFillManager.fillReport(compilado(plantilla), parametros,
                    new JRBeanCollectionDataSource(filas));
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException e) {
            throw new ReporteException("No se pudo generar el reporte " + plantilla, e);
        }
    }

    private JasperReport compilado(String plantilla) {
        return compilados.computeIfAbsent(plantilla, nombre -> {
            String ruta = "reportes/" + nombre + ".jrxml";
            try (InputStream jrxml = getClass().getClassLoader().getResourceAsStream(ruta)) {
                if (jrxml == null) {
                    throw new IllegalArgumentException("No existe la plantilla " + ruta);
                }
                return JasperCompileManager.compileReport(jrxml);
            } catch (JRException | IOException e) {
                throw new ReporteException("No se pudo compilar la plantilla " + ruta, e);
            }
        });
    }
}
