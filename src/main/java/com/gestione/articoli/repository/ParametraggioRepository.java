package com.gestione.articoli.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestione.articoli.model.CategoriaParametraggio;
import com.gestione.articoli.model.Parametraggio;

public interface ParametraggioRepository extends JpaRepository<Parametraggio, Long> {

    Optional<Parametraggio> findByNomeIgnoreCase(String nome);

    List<Parametraggio> findByCategoria(CategoriaParametraggio categoria);

    List<Parametraggio> findByAttivoTrue();


    List<Parametraggio> findAllByOrderByNomeAsc();
}
