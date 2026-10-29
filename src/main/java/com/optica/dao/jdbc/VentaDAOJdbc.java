package com.optica.dao.jdbc;

import com.optica.dao.VentaDAO;
import com.optica.exception.DAOException;
import com.optica.model.DetalleVenta;
import com.optica.model.TipoProducto;
import com.optica.model.Venta;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class VentaDAOJdbc extends JdbcSupport implements VentaDAO {

    private static final String SELECT = """
            SELECT v.id, v.cliente_id, v.vendedor_id, v.medicion_id, v.aro_id, v.luna_id,
                   v.fecha, v.subtotal, v.igv, v.total,
                   c.nombres || ' ' || c.apellidos AS cliente_nombre,
                   ve.nombres || ' ' || ve.apellidos AS vendedor_nombre
            FROM ventas v
            JOIN clientes c ON c.id = v.cliente_id
            JOIN vendedores ve ON ve.id = v.vendedor_id
            """;

    private static final String INSERT_VENTA = """
            INSERT INTO ventas (cliente_id, vendedor_id, medicion_id, aro_id, luna_id, fecha, subtotal, igv, total)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String INSERT_DETALLE = """
            INSERT INTO detalle_venta (venta_id, tipo_producto, producto_id, descripcion, cantidad, precio_unitario, subtotal)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    public VentaDAOJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Venta> findById(Long id) {
        Optional<Venta> venta = queryOne(SELECT + " WHERE v.id = ?", this::mapRow, id);
        venta.ifPresent(v -> v.setDetalles(findDetalles(v.getId())));
        return venta;
    }

    @Override
    public List<Venta> findAll() {
        return queryList(SELECT + " ORDER BY v.fecha DESC, v.id DESC", this::mapRow);
    }

    @Override
    public List<Venta> findByCliente(Long clienteId) {
        return queryList(SELECT + " WHERE v.cliente_id = ? ORDER BY v.fecha DESC", this::mapRow, clienteId);
    }

    /**
     * Inserta la cabecera y sus detalles (en lote) usando la misma conexión.
     * Debe invocarse dentro de una transacción del servicio para que sea atómico.
     */
    @Override
    public Venta save(Venta v) {
        if (v.getFecha() == null) {
            v.setFecha(LocalDateTime.now());
        }
        Connection con = getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement(INSERT_VENTA, new String[]{"id"})) {
                setParams(ps, v.getClienteId(), v.getVendedorId(), v.getMedicionId(), v.getAroId(), v.getLunaId(),
                        v.getFecha(), v.getSubtotal(), v.getIgv(), v.getTotal());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    v.setId(keys.getLong(1));
                }
            }
            try (PreparedStatement ps = con.prepareStatement(INSERT_DETALLE)) {
                for (DetalleVenta d : v.getDetalles()) {
                    d.setVentaId(v.getId());
                    setParams(ps, d.getVentaId(), d.getTipoProducto(), d.getProductoId(), d.getDescripcion(),
                            d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            return v;
        } catch (SQLException e) {
            throw new DAOException("Error al registrar la venta", e);
        } finally {
            release(con);
        }
    }

    /** Las ventas son documentos contables: solo se permite corregir los montos. */
    @Override
    public boolean update(Venta v) {
        return executeUpdate("UPDATE ventas SET subtotal = ?, igv = ?, total = ? WHERE id = ?",
                v.getSubtotal(), v.getIgv(), v.getTotal(), v.getId()) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return executeUpdate("DELETE FROM ventas WHERE id = ?", id) > 0;
    }

    private List<DetalleVenta> findDetalles(Long ventaId) {
        return queryList("""
                SELECT id, venta_id, tipo_producto, producto_id, descripcion, cantidad, precio_unitario, subtotal
                FROM detalle_venta WHERE venta_id = ? ORDER BY id
                """, this::mapDetalle, ventaId);
    }

    private Venta mapRow(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setId(rs.getLong("id"));
        v.setClienteId(rs.getLong("cliente_id"));
        v.setVendedorId(rs.getLong("vendedor_id"));
        v.setMedicionId(rs.getLong("medicion_id"));
        v.setAroId(rs.getLong("aro_id"));
        v.setLunaId(rs.getLong("luna_id"));
        v.setFecha(rs.getObject("fecha", LocalDateTime.class));
        v.setSubtotal(rs.getBigDecimal("subtotal"));
        v.setIgv(rs.getBigDecimal("igv"));
        v.setTotal(rs.getBigDecimal("total"));
        v.setClienteNombre(rs.getString("cliente_nombre"));
        v.setVendedorNombre(rs.getString("vendedor_nombre"));
        return v;
    }

    private DetalleVenta mapDetalle(ResultSet rs) throws SQLException {
        DetalleVenta d = new DetalleVenta();
        d.setId(rs.getLong("id"));
        d.setVentaId(rs.getLong("venta_id"));
        d.setTipoProducto(TipoProducto.valueOf(rs.getString("tipo_producto")));
        d.setProductoId(rs.getLong("producto_id"));
        d.setDescripcion(rs.getString("descripcion"));
        d.setCantidad(rs.getInt("cantidad"));
        d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        d.setSubtotal(rs.getBigDecimal("subtotal"));
        return d;
    }
}
