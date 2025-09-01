package com.gestione.articoli.service;

import com.gestione.articoli.dto.ArticoloDto;
import com.gestione.articoli.model.Articolo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

public interface ArticoloService {

    ArticoloDto save(ArticoloDto articoloDto);

    ArticoloDto findById(Long id);

    List<ArticoloDto> findAllNoPagination();

    void deleteById(Long id);

	Optional<Articolo> findEntityById(Long id) ;

	List<ArticoloDto> findAllParents();

	Page<ArticoloDto> findAll(int page, int size);

}
