package com.optica.dto;

import java.util.List;

public record FiltrosCatalogoResponse(List<String> marcas, List<String> materiales, List<String> formas) {
}
