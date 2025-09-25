package com.gestione.articoli.service;

import com.gestione.articoli.dto.ArticoloHierarchyDto;
import com.gestione.articoli.dto.OrdineDto;

import java.util.List;

public interface OrdineService {

    OrdineDto createOrdine(OrdineDto dto);

    List<OrdineDto> getAllOrdini();

    OrdineDto getOrdineById(Long id);
    
    OrdineDto updateOrdine(Long id, OrdineDto dto);

    void deleteOrdine(Long id);

	List<ArticoloHierarchyDto> getGerarchiaArticoliByOrdineId(Long ordineId);
}
