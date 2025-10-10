package com.gestione.articoli.service;

import com.gestione.articoli.dto.OrdineArticoloDto;
import java.util.List;

public interface OrdineArticoloService {
    OrdineArticoloDto createOrdineArticolo(OrdineArticoloDto dto);
    OrdineArticoloDto updateOrdineArticolo(Long id, OrdineArticoloDto dto);
    void deleteOrdineArticolo(Long id);
    OrdineArticoloDto getOrdineArticoloById(Long id);
    List<OrdineArticoloDto> getAllOrdineArticoli();
    List<OrdineArticoloDto> getAllOrdineArticoliByOrdineId(Long ordineId);

}
