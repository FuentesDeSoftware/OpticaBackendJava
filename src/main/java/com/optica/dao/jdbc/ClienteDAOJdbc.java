package com.optica.dao.jdbc;

import com.optica.dao.ClienteDAO;
import com.optica.model.Cliente;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ClienteDAOJdbc extends JdbcSupport implements ClienteDAO {

    private static final String SELECT = """
            SELECT c.id, c.usuario_id, c.nombres, c.apellidos, c.dni, c.telefono, u.email
            FROM clientes c
            JOIN usuarios u ON u.id = c.usuario_id
            """;

    public ClienteDAOJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return queryOne(SELECT + " WHERE c.id = ?", this::mapRow, id);
    }

    @Override
    public Optional<Cliente> findByUsuarioId(Long usuarioId) {
        return queryOne(SELECT + " WHERE c.usuario_id = ?", this::mapRow, usuarioId);
    }

    @Override
    public Optional<Cliente> findByDni(String dni) {
        return queryOne(SELECT + " WHERE c.dni = ?", this::mapRow, dni);
    }

    @Override
    public boolean existsByDni(String dni) {
        return findByDni(dni).isPresent();
    }

    @Override
    public List<Cliente> buscar(String texto) {
        String patron = "%" + texto.trim().toLowerCase() + "%";
        return queryList(SELECT + """
                 WHERE c.dni LIKE ? OR LOWER(c.nombres) LIKE ? OR LOWER(c.apellidos) LIKE ?
                 ORDER BY c.apellidos, c.nombres LIMIT 20
                """, this::mapRow, patron, patron, patron);
    }

    @Override
    public List<Cliente> findAll() {
        return queryList(SELECT + " ORDER BY c.apellidos, c.nombres", this::mapRow);
    }

    @Override
    public Cliente save(Cliente c) {
        long id = insert("INSERT INTO clientes (usuario_id, nombres, apellidos, dni, telefono) VALUES (?, ?, ?, ?, ?)",
                c.getUsuarioId(), c.getNombres(), c.getApellidos(), c.getDni(), c.getTelefono());
        c.setId(id);
        return c;
    }

    @Override
    public boolean update(Cliente c) {
        return executeUpdate("UPDATE clientes SET nombres = ?, apellidos = ?, dni = ?, telefono = ? WHERE id = ?",
                c.getNombres(), c.getApellidos(), c.getDni(), c.getTelefono(), c.getId()) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return executeUpdate("DELETE FROM clientes WHERE id = ?", id) > 0;
    }

    private Cliente mapRow(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getLong("id"));
        c.setUsuarioId(rs.getLong("usuario_id"));
        c.setNombres(rs.getString("nombres"));
        c.setApellidos(rs.getString("apellidos"));
        c.setDni(rs.getString("dni"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        return c;
    }
}
