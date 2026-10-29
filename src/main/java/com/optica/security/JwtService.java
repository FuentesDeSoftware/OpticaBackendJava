package com.optica.security;

import com.optica.model.Rol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/** Emite los JWT (HS256) que el frontend envía en la cabecera Authorization. */
@Service
public class JwtService {

    public static final String CLAIM_ROL = "rol";
    public static final String CLAIM_USUARIO = "uid";
    public static final String CLAIM_PERFIL = "pid";
    public static final String CLAIM_NOMBRE = "nombre";

    private final JwtEncoder encoder;
    private final Duration expiracion;

    public JwtService(JwtEncoder encoder, @Value("${optica.jwt.expiracion-horas}") long horas) {
        this.encoder = encoder;
        this.expiracion = Duration.ofHours(horas);
    }

    public String generar(Long usuarioId, Long perfilId, String email, Rol rol, String nombre) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("optica-backend")
                .issuedAt(ahora)
                .expiresAt(ahora.plus(expiracion))
                .subject(email)
                .claim(CLAIM_ROL, rol.name())
                .claim(CLAIM_USUARIO, usuarioId)
                .claim(CLAIM_PERFIL, perfilId)
                .claim(CLAIM_NOMBRE, nombre)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
