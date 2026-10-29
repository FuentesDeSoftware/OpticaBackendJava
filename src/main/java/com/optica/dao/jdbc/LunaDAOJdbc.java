package com.optica.dao.jdbc;

import com.optica.dao.LunaDAO;
import com.optica.model.Luna;
import com.optica.model.TipoLuna;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class LunaDAOJdbc extends AbstractProductoDAOJdbc<Luna> implements LunaDAO {

    public LunaDAOJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String tabla() {
        return "lunas";
    }

    @Override
    protected String columnas() {
        return "id, codigo, tipo, material, indice, tratamiento, precio, stock, activo";
    }

    @Override
    protected String ordenPorDefecto() {
        return "tipo, precio";
    }

    @Override
    public Luna save(Luna l) {
        long id = insert("""
                INSERT INTO lunas (codigo, tipo, material, indice, tratamiento, precio, stock, activo)
                VALUES (?, ?, ?, ?, ?, ?, ?, TRUE)
                """,
                l.getCodigo(), l.getTipo(), l.getMaterial(), l.getIndice(), l.getTratamiento(),
                l.getPrecio(), l.getStock());
        l.setId(id);
        l.setActivo(true);
        return l;
    }

    @Override
    public boolean update(Luna l) {
        return executeUpdate("""
                UPDATE lunas SET codigo = ?, tipo = ?, material = ?, indice = ?, tratamiento = ?,
                       precio = ?, stock = ?
                WHERE id = ? AND activo
                """,
                l.getCodigo(), l.getTipo(), l.getMaterial(), l.getIndice(), l.getTratamiento(),
                l.getPrecio(), l.getStock(), l.getId()) > 0;
    }

    @Override
    protected Luna mapRow(ResultSet rs) throws SQLException {
        Luna l = new Luna();
        mapProducto(rs, l);
        l.setTipo(TipoLuna.valueOf(rs.getString("tipo")));
        l.setMaterial(rs.getString("material"));
        l.setIndice(rs.getBigDecimal("indice"));
        l.setTratamiento(rs.getString("tratamiento"));
        return l;
    }
}
