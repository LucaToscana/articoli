package com.gestione.articoli.service;

import com.gestione.articoli.dto.ArticoloDto;

import java.util.List;

public interface ArticoloService {

    ArticoloDto save(ArticoloDto articoloDto);

    ArticoloDto findById(Long id);

    List<ArticoloDto> findAll();

    void deleteById(Long id);
}
