package com.optica.dao.jdbc;

import com.optica.dao.MedicionDAO;
import com.optica.model.Medicion;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class MedicionDAOJdbc extends JdbcSupport implements MedicionDAO {

    private static final String SELECT = """
            SELECT id, cliente_id, vendedor_id, fecha,
                   od_esfera, od_cilindro, od_eje, od_adicion,
                   oi_esfera, oi_cilindro, oi_eje, oi_adicion, dip, observaciones
            FROM mediciones
            """;

    public MedicionDAOJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Medicion> findById(Long id) {
        return queryOne(SELECT + " WHERE id = ?", this::mapRow, id);
    }

    @Override
    public List<Medicion> findByCliente(Long clienteId) {
        return queryList(SELECT + " WHERE cliente_id = ? ORDER BY fecha DESC, id DESC", this::mapRow, clienteId);
    }

    @Override
    public List<Medicion> findAll() {
        return queryList(SELECT + " ORDER BY fecha DESC, id DESC", this::mapRow);
    }

    @Override
    public Medicion save(Medicion m) {
        if (m.getFecha() == null) {
            m.setFecha(LocalDate.now());
        }
        long id = insert("""
                INSERT INTO mediciones (cliente_id, vendedor_id, fecha,
                    od_esfera, od_cilindro, od_eje, od_adicion,
                    oi_esfera, oi_cilindro, oi_eje, oi_adicion, dip, observaciones)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                m.getClienteId(), m.getVendedorId(), m.getFecha(),
                m.getOdEsfera(), m.getOdCilindro(), m.getOdEje(), m.getOdAdicion(),
                m.getOiEsfera(), m.getOiCilindro(), m.getOiEje(), m.getOiAdicion(),
                m.getDip(), m.getObservaciones());
        m.setId(id);
        return m;
    }

    @Override
    public boolean update(Medicion m) {
        return executeUpdate("""
                UPDATE mediciones SET fecha = ?,
                    od_esfera = ?, od_cilindro = ?, od_eje = ?, od_adicion = ?,
                    oi_esfera = ?, oi_cilindro = ?, oi_eje = ?, oi_adicion = ?,
                    dip = ?, observaciones = ?
                WHERE id = ?
                """,
                m.getFecha(),
                m.getOdEsfera(), m.getOdCilindro(), m.getOdEje(), m.getOdAdicion(),
                m.getOiEsfera(), m.getOiCilindro(), m.getOiEje(), m.getOiAdicion(),
                m.getDip(), m.getObservaciones(), m.getId()) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return executeUpdate("DELETE FROM mediciones WHERE id = ?", id) > 0;
    }

    private Medicion mapRow(ResultSet rs) throws SQLException {
        Medicion m = new Medicion();
        m.setId(rs.getLong("id"));
        m.setClienteId(rs.getLong("cliente_id"));
        m.setVendedorId(getLongOrNull(rs, "vendedor_id"));
        m.setFecha(rs.getObject("fecha", LocalDate.class));
        m.setOdEsfera(rs.getBigDecimal("od_esfera"));
        m.setOdCilindro(rs.getBigDecimal("od_cilindro"));
        m.setOdEje(getIntOrNull(rs, "od_eje"));
        m.setOdAdicion(rs.getBigDecimal("od_adicion"));
        m.setOiEsfera(rs.getBigDecimal("oi_esfera"));
        m.setOiCilindro(rs.getBigDecimal("oi_cilindro"));
        m.setOiEje(getIntOrNull(rs, "oi_eje"));
        m.setOiAdicion(rs.getBigDecimal("oi_adicion"));
        m.setDip(rs.getBigDecimal("dip"));
        m.setObservaciones(rs.getString("observaciones"));
        return m;
    }
}
