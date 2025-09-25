package com.gestione.articoli.service;

import com.gestione.articoli.dto.DdtDto;

import java.util.List;

public interface DdtService {
    DdtDto creaDdt(Long ordineId, String causale, String partenza, String arrivo);
    List<DdtDto> getAll();
    DdtDto getById(Long id);
}
