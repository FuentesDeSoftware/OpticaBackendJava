package com.optica.dao.jdbc;

import com.optica.dao.VendedorDAO;
import com.optica.model.Vendedor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class VendedorDAOJdbc extends JdbcSupport implements VendedorDAO {

    private static final String SELECT = "SELECT id, usuario_id, nombres, apellidos FROM vendedores";

    public VendedorDAOJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Vendedor> findById(Long id) {
        return queryOne(SELECT + " WHERE id = ?", this::mapRow, id);
    }

    @Override
    public Optional<Vendedor> findByUsuarioId(Long usuarioId) {
        return queryOne(SELECT + " WHERE usuario_id = ?", this::mapRow, usuarioId);
    }

    @Override
    public List<Vendedor> findAll() {
        return queryList(SELECT + " ORDER BY apellidos, nombres", this::mapRow);
    }

    @Override
    public Vendedor save(Vendedor v) {
        long id = insert("INSERT INTO vendedores (usuario_id, nombres, apellidos) VALUES (?, ?, ?)",
                v.getUsuarioId(), v.getNombres(), v.getApellidos());
        v.setId(id);
        return v;
    }

    @Override
    public boolean update(Vendedor v) {
        return executeUpdate("UPDATE vendedores SET nombres = ?, apellidos = ? WHERE id = ?",
                v.getNombres(), v.getApellidos(), v.getId()) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return executeUpdate("DELETE FROM vendedores WHERE id = ?", id) > 0;
    }

    private Vendedor mapRow(ResultSet rs) throws SQLException {
        Vendedor v = new Vendedor();
        v.setId(rs.getLong("id"));
        v.setUsuarioId(rs.getLong("usuario_id"));
        v.setNombres(rs.getString("nombres"));
        v.setApellidos(rs.getString("apellidos"));
        return v;
    }
}
