package com.optica.dao.jdbc;

import com.optica.exception.DAOException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base de las implementaciones JDBC.
 * <p>
 * Las conexiones se obtienen con {@link DataSourceUtils}: si el servicio abrió una
 * transacción ({@code @Transactional}) todos los DAO comparten la misma conexión,
 * de modo que un error en cualquiera de ellos revierte toda la operación.
 * Fuera de una transacción, cada llamada usa (y devuelve al pool) su propia conexión.
 */
public abstract class JdbcSupport {

    /** Convierte la fila actual del ResultSet en un objeto del modelo. */
    @FunctionalInterface
    protected interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    protected final DataSource dataSource;

    protected JdbcSupport(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    protected void release(Connection con) {
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    protected <T> List<T> queryList(String sql, RowMapper<T> mapper, Object... params) {
        Connection con = getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapper.map(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al consultar", e);
        } finally {
            release(con);
        }
    }

    protected <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
        List<T> lista = queryList(sql, mapper, params);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    /** Ejecuta INSERT/UPDATE/DELETE y devuelve las filas afectadas. */
    protected int executeUpdate(String sql, Object... params) {
        Connection con = getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            setParams(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar", e);
        } finally {
            release(con);
        }
    }

    /** Ejecuta un INSERT y devuelve la clave primaria generada (columna "id"). */
    protected long insert(String sql, Object... params) {
        Connection con = getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"})) {
            setParams(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("No se generó la clave primaria");
                }
                return keys.getLong(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al insertar", e);
        } finally {
            release(con);
        }
    }

    protected void setParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            if (p == null) {
                ps.setNull(i + 1, Types.NULL);
            } else if (p instanceof Enum<?> e) {
                ps.setString(i + 1, e.name());
            } else {
                ps.setObject(i + 1, p);
            }
        }
    }

    /** getLong que respeta los NULL de la base de datos. */
    protected static Long getLongOrNull(ResultSet rs, String columna) throws SQLException {
        long valor = rs.getLong(columna);
        return rs.wasNull() ? null : valor;
    }

    protected static Integer getIntOrNull(ResultSet rs, String columna) throws SQLException {
        int valor = rs.getInt(columna);
        return rs.wasNull() ? null : valor;
    }
}
