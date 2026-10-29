package com.optica.dao.jdbc;

import com.optica.dao.ApartadoDAO;
import com.optica.model.Apartado;
import com.optica.model.Aro;
import com.optica.model.EstadoApartado;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ApartadoDAOJdbc extends JdbcSupport implements ApartadoDAO {

    private static final String SELECT = """
            SELECT ap.id, ap.cliente_id, ap.aro_id, ap.fecha, ap.estado,
                   a.codigo, a.marca, a.modelo, a.material, a.forma, a.color, a.precio, a.stock, a.imagen_url, a.activo
            FROM apartados ap
            JOIN aros a ON a.id = ap.aro_id
            """;

    public ApartadoDAOJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Apartado> findById(Long id) {
        return queryOne(SELECT + " WHERE ap.id = ?", this::mapRow, id);
    }

    @Override
    public List<Apartado> findByCliente(Long clienteId) {
        return queryList(SELECT + " WHERE ap.cliente_id = ? ORDER BY ap.fecha DESC", this::mapRow, clienteId);
    }

    @Override
    public Optional<Apartado> findPendiente(Long clienteId, Long aroId) {
        return queryOne(SELECT + " WHERE ap.cliente_id = ? AND ap.aro_id = ? AND ap.estado = 'PENDIENTE'",
                this::mapRow, clienteId, aroId);
    }

    @Override
    public List<Apartado> findAll() {
        return queryList(SELECT + " ORDER BY ap.fecha DESC", this::mapRow);
    }

    @Override
    public Apartado save(Apartado ap) {
        if (ap.getFecha() == null) {
            ap.setFecha(LocalDateTime.now());
        }
        if (ap.getEstado() == null) {
            ap.setEstado(EstadoApartado.PENDIENTE);
        }
        long id = insert("INSERT INTO apartados (cliente_id, aro_id, fecha, estado) VALUES (?, ?, ?, ?)",
                ap.getClienteId(), ap.getAroId(), ap.getFecha(), ap.getEstado());
        ap.setId(id);
        return ap;
    }

    @Override
    public boolean update(Apartado ap) {
        return actualizarEstado(ap.getId(), ap.getEstado());
    }

    @Override
    public boolean actualizarEstado(Long id, EstadoApartado estado) {
        return executeUpdate("UPDATE apartados SET estado = ? WHERE id = ?", estado, id) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return executeUpdate("DELETE FROM apartados WHERE id = ?", id) > 0;
    }

    private Apartado mapRow(ResultSet rs) throws SQLException {
        Apartado ap = new Apartado();
        ap.setId(rs.getLong("id"));
        ap.setClienteId(rs.getLong("cliente_id"));
        ap.setAroId(rs.getLong("aro_id"));
        ap.setFecha(rs.getObject("fecha", LocalDateTime.class));
        ap.setEstado(EstadoApartado.valueOf(rs.getString("estado")));

        Aro a = new Aro();
        a.setId(ap.getAroId());
        a.setCodigo(rs.getString("codigo"));
        a.setMarca(rs.getString("marca"));
        a.setModelo(rs.getString("modelo"));
        a.setMaterial(rs.getString("material"));
        a.setForma(rs.getString("forma"));
        a.setColor(rs.getString("color"));
        a.setPrecio(rs.getBigDecimal("precio"));
        a.setStock(rs.getInt("stock"));
        a.setImagenUrl(rs.getString("imagen_url"));
        a.setActivo(rs.getBoolean("activo"));
        ap.setAro(a);
        return ap;
    }
}
