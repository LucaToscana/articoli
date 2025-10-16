package com.gestione.articoli.service;

import com.gestione.articoli.dto.LavorazioneDto;
import java.util.List;

public interface LavorazioneService {

    List<LavorazioneDto> findAll();

    LavorazioneDto findById(Long id);

    LavorazioneDto save(LavorazioneDto lavorazioneDto);

    LavorazioneDto update(Long id, LavorazioneDto lavorazioneDto);

    void deleteById(Long id);
}
