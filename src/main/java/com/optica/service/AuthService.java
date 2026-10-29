package com.optica.service;

import com.optica.dao.ClienteDAO;
import com.optica.dao.UsuarioDAO;
import com.optica.dao.VendedorDAO;
import com.optica.dto.AuthResponse;
import com.optica.dto.LoginRequest;
import com.optica.dto.RegistroRequest;
import com.optica.exception.ConflictoException;
import com.optica.exception.CredencialesInvalidasException;
import com.optica.model.Cliente;
import com.optica.model.Rol;
import com.optica.model.Usuario;
import com.optica.model.Vendedor;
import com.optica.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioDAO usuarioDAO;
    private final ClienteDAO clienteDAO;
    private final VendedorDAO vendedorDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioDAO usuarioDAO, ClienteDAO clienteDAO, VendedorDAO vendedorDAO,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioDAO = usuarioDAO;
        this.clienteDAO = clienteDAO;
        this.vendedorDAO = vendedorDAO;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest req) {
        Usuario usuario = usuarioDAO.findByEmail(req.email().trim())
                .filter(Usuario::isActivo)
                .filter(u -> passwordEncoder.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(CredencialesInvalidasException::new);

        // El perfil (cliente o vendedor) determina a qué interfaz se redirige
        if (usuario.getRol() == Rol.CLIENTE) {
            Cliente c = clienteDAO.findByUsuarioId(usuario.getId()).orElseThrow(CredencialesInvalidasException::new);
            return respuesta(usuario, c.getId(), c.getNombreCompleto());
        }
        Vendedor v = vendedorDAO.findByUsuarioId(usuario.getId()).orElseThrow(CredencialesInvalidasException::new);
        return respuesta(usuario, v.getId(), v.getNombreCompleto());
    }

    /** Crea el usuario y su perfil de cliente en una sola transacción. */
    @Transactional
    public AuthResponse registrarCliente(RegistroRequest req) {
        String email = req.email().trim().toLowerCase();
        if (usuarioDAO.existsByEmail(email)) {
            throw new ConflictoException("El correo ya está registrado");
        }
        if (clienteDAO.existsByDni(req.dni())) {
            throw new ConflictoException("El DNI ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(req.password()));
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);
        usuarioDAO.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setUsuarioId(usuario.getId());
        cliente.setNombres(req.nombres().trim());
        cliente.setApellidos(req.apellidos().trim());
        cliente.setDni(req.dni());
        cliente.setTelefono(req.telefono());
        clienteDAO.save(cliente);

        return respuesta(usuario, cliente.getId(), cliente.getNombreCompleto());
    }

    private AuthResponse respuesta(Usuario u, Long perfilId, String nombre) {
        String token = jwtService.generar(u.getId(), perfilId, u.getEmail(), u.getRol(), nombre);
        return new AuthResponse(token, u.getRol(), nombre, u.getEmail());
    }
}
