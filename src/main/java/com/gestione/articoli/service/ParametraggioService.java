package com.gestione.articoli.service;

import com.gestione.articoli.dto.ParametraggioDTO;
import com.gestione.articoli.model.CategoriaParametraggio;

import java.util.List;

public interface ParametraggioService {

    List<ParametraggioDTO> getAll();

    List<ParametraggioDTO> getByCategoria(CategoriaParametraggio categoria);

    ParametraggioDTO getByNome(String nome);

    ParametraggioDTO getById(Long id);

    ParametraggioDTO save(ParametraggioDTO dto);

    void delete(Long id);
}
