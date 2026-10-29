package com.optica.dao.jdbc;

import com.optica.dao.AccesorioDAO;
import com.optica.model.Accesorio;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class AccesorioDAOJdbc extends AbstractProductoDAOJdbc<Accesorio> implements AccesorioDAO {

    public AccesorioDAOJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String tabla() {
        return "accesorios";
    }

    @Override
    protected String columnas() {
        return "id, codigo, nombre, descripcion, precio, stock, activo";
    }

    @Override
    protected String ordenPorDefecto() {
        return "nombre";
    }

    @Override
    public Accesorio save(Accesorio a) {
        long id = insert("""
                INSERT INTO accesorios (codigo, nombre, descripcion, precio, stock, activo)
                VALUES (?, ?, ?, ?, ?, TRUE)
                """,
                a.getCodigo(), a.getNombre(), a.getDescripcion(), a.getPrecio(), a.getStock());
        a.setId(id);
        a.setActivo(true);
        return a;
    }

    @Override
    public boolean update(Accesorio a) {
        return executeUpdate("""
                UPDATE accesorios SET codigo = ?, nombre = ?, descripcion = ?, precio = ?, stock = ?
                WHERE id = ? AND activo
                """,
                a.getCodigo(), a.getNombre(), a.getDescripcion(), a.getPrecio(), a.getStock(), a.getId()) > 0;
    }

    @Override
    protected Accesorio mapRow(ResultSet rs) throws SQLException {
        Accesorio a = new Accesorio();
        mapProducto(rs, a);
        a.setNombre(rs.getString("nombre"));
        a.setDescripcion(rs.getString("descripcion"));
        return a;
    }
}
