package com.optica.dao;

import com.optica.model.Aro;

import java.math.BigDecimal;
import java.util.List;

public interface AroDAO extends ProductoDAO<Aro> {

    /**
     * Catálogo de aros activos con stock. Los filtros nulos o vacíos se ignoran.
     */
    List<Aro> findByFiltros(String marca, String material, String forma, BigDecimal precioMin, BigDecimal precioMax);

    List<String> findMarcas();

    List<String> findMateriales();

    List<String> findFormas();
}
