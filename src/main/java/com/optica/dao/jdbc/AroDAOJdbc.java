package com.optica.dao.jdbc;

import com.optica.dao.AroDAO;
import com.optica.model.Aro;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AroDAOJdbc extends AbstractProductoDAOJdbc<Aro> implements AroDAO {

    public AroDAOJdbc(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String tabla() {
        return "aros";
    }

    @Override
    protected String columnas() {
        return "id, codigo, marca, modelo, material, forma, color, precio, stock, imagen_url, activo";
    }

    @Override
    protected String ordenPorDefecto() {
        return "marca, modelo";
    }

    @Override
    public List<Aro> findByFiltros(String marca, String material, String forma,
                                   BigDecimal precioMin, BigDecimal precioMax) {
        // Consulta dinámica: solo se agregan las condiciones de los filtros presentes,
        // siempre como parámetros (?) para evitar inyección SQL.
        StringBuilder sql = new StringBuilder(select()).append(" WHERE activo AND stock > 0");
        List<Object> params = new ArrayList<>();
        if (tieneTexto(marca)) {
            sql.append(" AND marca = ?");
            params.add(marca);
        }
        if (tieneTexto(material)) {
            sql.append(" AND material = ?");
            params.add(material);
        }
        if (tieneTexto(forma)) {
            sql.append(" AND forma = ?");
            params.add(forma);
        }
        if (precioMin != null) {
            sql.append(" AND precio >= ?");
            params.add(precioMin);
        }
        if (precioMax != null) {
            sql.append(" AND precio <= ?");
            params.add(precioMax);
        }
        sql.append(" ORDER BY ").append(ordenPorDefecto());
        return queryList(sql.toString(), this::mapRow, params.toArray());
    }

    @Override
    public List<String> findMarcas() {
        return valoresDistintos("marca");
    }

    @Override
    public List<String> findMateriales() {
        return valoresDistintos("material");
    }

    @Override
    public List<String> findFormas() {
        return valoresDistintos("forma");
    }

    /** {@code columna} es siempre una constante de esta clase. */
    private List<String> valoresDistintos(String columna) {
        return queryList("SELECT DISTINCT " + columna + " FROM aros WHERE activo AND stock > 0 ORDER BY 1",
                rs -> rs.getString(1));
    }

    @Override
    public Aro save(Aro a) {
        long id = insert("""
                INSERT INTO aros (codigo, marca, modelo, material, forma, color, precio, stock, imagen_url, activo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE)
                """,
                a.getCodigo(), a.getMarca(), a.getModelo(), a.getMaterial(), a.getForma(), a.getColor(),
                a.getPrecio(), a.getStock(), a.getImagenUrl());
        a.setId(id);
        a.setActivo(true);
        return a;
    }

    @Override
    public boolean update(Aro a) {
        return executeUpdate("""
                UPDATE aros SET codigo = ?, marca = ?, modelo = ?, material = ?, forma = ?, color = ?,
                       precio = ?, stock = ?, imagen_url = ?
                WHERE id = ? AND activo
                """,
                a.getCodigo(), a.getMarca(), a.getModelo(), a.getMaterial(), a.getForma(), a.getColor(),
                a.getPrecio(), a.getStock(), a.getImagenUrl(), a.getId()) > 0;
    }

    @Override
    protected Aro mapRow(ResultSet rs) throws SQLException {
        Aro a = new Aro();
        mapProducto(rs, a);
        a.setMarca(rs.getString("marca"));
        a.setModelo(rs.getString("modelo"));
        a.setMaterial(rs.getString("material"));
        a.setForma(rs.getString("forma"));
        a.setColor(rs.getString("color"));
        a.setImagenUrl(rs.getString("imagen_url"));
        return a;
    }

    private static boolean tieneTexto(String s) {
        return s != null && !s.isBlank();
    }
}
