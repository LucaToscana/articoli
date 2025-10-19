package com.gestione.articoli.service;


import com.gestione.articoli.dto.OrdineRisultatoDto;
import com.gestione.articoli.model.OrdineRisultato;

import java.math.BigDecimal;
import java.util.List;

public interface OrdineRisultatoService {
    OrdineRisultatoDto save(OrdineRisultatoDto dto);
    OrdineRisultatoDto getById(Long id);
    List<OrdineRisultatoDto> getAll();
    List<OrdineRisultatoDto> getByOrdineId(Long ordineId);
    void delete(Long id);
    List<OrdineRisultato> generaRisultatiDaWorks(Long ordineId, OrdineRisultatoDto parametriCalcoloDto);
	void deleteByOrdineId(Long ordineId);
}
