package com.optica.security;

import com.optica.model.Rol;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Datos del usuario autenticado extraídos del JWT.
 *
 * @param usuarioId id en la tabla usuarios
 * @param perfilId  id en la tabla clientes o vendedores, según el rol
 */
public record UsuarioActual(Long usuarioId, Long perfilId, Rol rol, String email) {

    public static UsuarioActual de(Jwt jwt) {
        return new UsuarioActual(
                ((Number) jwt.getClaim(JwtService.CLAIM_USUARIO)).longValue(),
                ((Number) jwt.getClaim(JwtService.CLAIM_PERFIL)).longValue(),
                Rol.valueOf(jwt.getClaimAsString(JwtService.CLAIM_ROL)),
                jwt.getSubject());
    }
}
