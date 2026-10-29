package com.optica.service;

import com.optica.dao.AroDAO;
import com.optica.dto.FiltrosCatalogoResponse;
import com.optica.exception.NegocioException;
import com.optica.model.Aro;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CatalogoService {

    private final AroDAO aroDAO;

    public CatalogoService(AroDAO aroDAO) {
        this.aroDAO = aroDAO;
    }

    public List<Aro> buscarAros(String marca, String material, String forma, BigDecimal precioMin, BigDecimal precioMax) {
        if (precioMin != null && precioMax != null && precioMin.compareTo(precioMax) > 0) {
            throw new NegocioException("El precio mínimo no puede ser mayor que el máximo");
        }
        return aroDAO.findByFiltros(marca, material, forma, precioMin, precioMax);
    }

    public FiltrosCatalogoResponse filtros() {
        return new FiltrosCatalogoResponse(aroDAO.findMarcas(), aroDAO.findMateriales(), aroDAO.findFormas());
    }
}
