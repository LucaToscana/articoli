package com.gestione.articoli.service;

import com.gestione.articoli.dto.ArticoloDto;
import com.gestione.articoli.dto.ArticoloHierarchyDto;
import com.gestione.articoli.dto.ArticoloOrdersDto;
import com.gestione.articoli.model.Articolo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

public interface ArticoloService {

    ArticoloDto save(ArticoloDto articoloDto);

    ArticoloDto findById(Long id);
    List<ArticoloDto> findByAziendaId(Long aziendaId);

    List<ArticoloDto> findAllNoPagination();

    void deleteById(Long id);

	Optional<Articolo> findEntityById(Long id) ;

	List<ArticoloDto> findAllParents();

	Page<ArticoloDto> findAll(int page, int size);

	ArticoloHierarchyDto getGerarchia(Long id);

	Articolo saveAndGetEntity(ArticoloDto articoloDto);

	ArticoloOrdersDto getOrdiniPerArticolo(Long id);

}
