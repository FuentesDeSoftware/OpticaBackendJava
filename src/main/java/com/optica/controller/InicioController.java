package com.optica.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Página pública en la raíz para confirmar que la API está en línea. */
@RestController
public class InicioController {

    private static final String PAGINA = """
            <!doctype html>
            <html lang="es">
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, initial-scale=1">
              <title>Óptica Visión Clara · API</title>
            </head>
            <body style="margin:0;min-height:100vh;display:grid;place-items:center;font-family:system-ui,sans-serif;background:#f5f7f8;color:#14232b;text-align:center;padding:16px">
              <main>
                <h1 style="margin:0 0 8px">Backend de software para óptica de Julio Fuentes está funcionando :)</h1>
                <p style="margin:0;color:#5a6b74">API REST de Óptica Visión Clara · Spring Boot</p>
              </main>
            </body>
            </html>
            """;

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String inicio() {
        return PAGINA;
    }
}
