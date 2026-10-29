package com.optica.dto;

import com.optica.model.Accesorio;
import com.optica.model.Aro;
import com.optica.model.Luna;

import java.util.List;

public record AlertasStockResponse(int umbral, List<Aro> aros, List<Luna> lunas, List<Accesorio> accesorios) {

    public int total() {
        return aros.size() + lunas.size() + accesorios.size();
    }
}
