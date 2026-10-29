package com.optica.dao.jdbc;

import com.optica.dao.UsuarioDAO;
import com.optica.model.Rol;
import com.optica.model.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioDAOJdbc extends JdbcSupport implements UsuarioDAO {

    private static final String SELECT = "SELECT id, email, password_hash, rol, activo, creado_en FROM usuarios";

    public UsuarioDAOJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return queryOne(SELECT + " WHERE id = ?", this::mapRow, id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return queryOne(SELECT + " WHERE LOWER(email) = LOWER(?)", this::mapRow, email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public List<Usuario> findAll() {
        return queryList(SELECT + " ORDER BY id", this::mapRow);
    }

    @Override
    public Usuario save(Usuario u) {
        long id = insert("INSERT INTO usuarios (email, password_hash, rol, activo) VALUES (?, ?, ?, ?)",
                u.getEmail().toLowerCase(), u.getPasswordHash(), u.getRol(), u.isActivo());
        u.setId(id);
        return u;
    }

    @Override
    public boolean update(Usuario u) {
        return executeUpdate("UPDATE usuarios SET email = ?, password_hash = ?, rol = ?, activo = ? WHERE id = ?",
                u.getEmail().toLowerCase(), u.getPasswordHash(), u.getRol(), u.isActivo(), u.getId()) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return executeUpdate("UPDATE usuarios SET activo = FALSE WHERE id = ?", id) > 0;
    }

    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRol(Rol.valueOf(rs.getString("rol")));
        u.setActivo(rs.getBoolean("activo"));
        u.setCreadoEn(rs.getObject("creado_en", LocalDateTime.class));
        return u;
    }
}
