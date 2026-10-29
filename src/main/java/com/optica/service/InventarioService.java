package com.optica.service;

import com.optica.dao.AccesorioDAO;
import com.optica.dao.AroDAO;
import com.optica.dao.LunaDAO;
import com.optica.dao.ProductoDAO;
import com.optica.dto.AlertasStockResponse;
import com.optica.exception.RecursoNoEncontradoException;
import com.optica.model.Accesorio;
import com.optica.model.Aro;
import com.optica.model.Luna;
import com.optica.model.Producto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/** CRUD de aros, lunas y accesorios, y alertas de stock bajo. */
@Service
public class InventarioService {

    private final AroDAO aroDAO;
    private final LunaDAO lunaDAO;
    private final AccesorioDAO accesorioDAO;
    private final int umbralStock;

    public InventarioService(AroDAO aroDAO, LunaDAO lunaDAO, AccesorioDAO accesorioDAO,
                             @Value("${optica.stock.umbral}") int umbralStock) {
        this.aroDAO = aroDAO;
        this.lunaDAO = lunaDAO;
        this.accesorioDAO = accesorioDAO;
        this.umbralStock = umbralStock;
    }

    // ---------- Aros ----------
    public List<Aro> listarAros() {
        return aroDAO.findAll();
    }

    public Aro obtenerAro(Long id) {
        return obtener(aroDAO, id, "Aro");
    }

    public Aro crearAro(Aro aro) {
        return aroDAO.save(aro);
    }

    public Aro actualizarAro(Long id, Aro aro) {
        return actualizar(aroDAO, id, aro, "Aro");
    }

    public void eliminarAro(Long id) {
        eliminar(aroDAO, id, "Aro");
    }

    // ---------- Lunas ----------
    public List<Luna> listarLunas() {
        return lunaDAO.findAll();
    }

    public Luna obtenerLuna(Long id) {
        return obtener(lunaDAO, id, "Luna");
    }

    public Luna crearLuna(Luna luna) {
        return lunaDAO.save(luna);
    }

    public Luna actualizarLuna(Long id, Luna luna) {
        return actualizar(lunaDAO, id, luna, "Luna");
    }

    public void eliminarLuna(Long id) {
        eliminar(lunaDAO, id, "Luna");
    }

    // ---------- Accesorios ----------
    public List<Accesorio> listarAccesorios() {
        return accesorioDAO.findAll();
    }

    public Accesorio obtenerAccesorio(Long id) {
        return obtener(accesorioDAO, id, "Accesorio");
    }

    public Accesorio crearAccesorio(Accesorio accesorio) {
        return accesorioDAO.save(accesorio);
    }

    public Accesorio actualizarAccesorio(Long id, Accesorio accesorio) {
        return actualizar(accesorioDAO, id, accesorio, "Accesorio");
    }

    public void eliminarAccesorio(Long id) {
        eliminar(accesorioDAO, id, "Accesorio");
    }

    // ---------- Alertas ----------
    public AlertasStockResponse alertasStock() {
        return new AlertasStockResponse(umbralStock,
                aroDAO.findStockBajo(umbralStock),
                lunaDAO.findStockBajo(umbralStock),
                accesorioDAO.findStockBajo(umbralStock));
    }

    // ---------- Comunes ----------
    private <T extends Producto> T obtener(ProductoDAO<T> dao, Long id, String nombre) {
        return dao.findById(id)
                .filter(Producto::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException(nombre, id));
    }

    private <T extends Producto> T actualizar(ProductoDAO<T> dao, Long id, T producto, String nombre) {
        producto.setId(id);
        if (!dao.update(producto)) {
            throw new RecursoNoEncontradoException(nombre, id);
        }
        return obtener(dao, id, nombre);
    }

    private <T extends Producto> void eliminar(ProductoDAO<T> dao, Long id, String nombre) {
        if (!dao.delete(id)) {
            throw new RecursoNoEncontradoException(nombre, id);
        }
    }
}
