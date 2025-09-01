package com.gestione.articoli.service;

import com.gestione.articoli.dto.AziendaDto;

import java.util.List;

public interface AziendaService {

    AziendaDto createAzienda(AziendaDto dto);

    AziendaDto updateAzienda(Long id, AziendaDto dto);

    void deleteAzienda(Long id);

    AziendaDto getAziendaById(Long id);

    List<AziendaDto> getAllAziende();
}
