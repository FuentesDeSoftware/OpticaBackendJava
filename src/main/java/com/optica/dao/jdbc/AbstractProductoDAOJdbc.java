package com.optica.dao.jdbc;

import com.optica.dao.ProductoDAO;
import com.optica.model.Producto;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Lógica JDBC compartida por las tablas de productos (aros, lunas, accesorios):
 * consultas, borrado lógico, alertas de stock y descuento de stock.
 * Cada subclase define su tabla, sus columnas y el mapeo de filas.
 */
abstract class AbstractProductoDAOJdbc<T extends Producto> extends JdbcSupport implements ProductoDAO<T> {

    protected AbstractProductoDAOJdbc(DataSource dataSource) {
        super(dataSource);
    }

    /** Nombre de la tabla (constante interna, nunca proviene del usuario). */
    protected abstract String tabla();

    protected abstract String columnas();

    protected abstract String ordenPorDefecto();

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected String select() {
        return "SELECT " + columnas() + " FROM " + tabla();
    }

    /** Carga las columnas comunes de {@link Producto}. */
    protected void mapProducto(ResultSet rs, T p) throws SQLException {
        p.setId(rs.getLong("id"));
        p.setCodigo(rs.getString("codigo"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setStock(rs.getInt("stock"));
        p.setActivo(rs.getBoolean("activo"));
    }

    @Override
    public Optional<T> findById(Long id) {
        return queryOne(select() + " WHERE id = ?", this::mapRow, id);
    }

    /** Solo productos activos. */
    @Override
    public List<T> findAll() {
        return queryList(select() + " WHERE activo ORDER BY " + ordenPorDefecto(), this::mapRow);
    }

    @Override
    public List<T> findStockBajo(int umbral) {
        return queryList(select() + " WHERE activo AND stock < ? ORDER BY stock, " + ordenPorDefecto(),
                this::mapRow, umbral);
    }

    @Override
    public boolean descontarStock(Long id, int cantidad) {
        // La condición stock >= ? evita stock negativo incluso con ventas concurrentes
        return executeUpdate("UPDATE " + tabla() + " SET stock = stock - ? WHERE id = ? AND activo AND stock >= ?",
                cantidad, id, cantidad) > 0;
    }

    /** Borrado lógico: el producto deja de mostrarse pero se conserva en ventas anteriores. */
    @Override
    public boolean delete(Long id) {
        return executeUpdate("UPDATE " + tabla() + " SET activo = FALSE WHERE id = ? AND activo", id) > 0;
    }
}
