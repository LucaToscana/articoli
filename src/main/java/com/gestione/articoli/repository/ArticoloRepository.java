package com.gestione.articoli.repository;

import com.gestione.articoli.model.Articolo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticoloRepository extends JpaRepository<Articolo, Long> {

    boolean existsByCodice(String codice);
    List<Articolo> findAllByOrderByDataCreazioneDesc();
    Page<Articolo> findAllByOrderByDataCreazioneDesc(Pageable pageable);

    @Query("SELECT a FROM Articolo a WHERE a.articoliPadri IS EMPTY ORDER BY a.dataCreazione DESC")
    List<Articolo> findAllParents();
    @Query("SELECT a FROM Articolo a LEFT JOIN FETCH a.articoliFigli WHERE a.id = :id")
    Optional<Articolo> findByIdWithFigli(@Param("id") Long id);
    @Query("SELECT a FROM Articolo a LEFT JOIN FETCH a.articoliFigli LEFT JOIN FETCH a.articoliPadri WHERE a.id = :id")
    Optional<Articolo> findByIdWithFigliAndPadri(@Param("id") Long id);
    // Recupera tutti gli articoli per una specifica azienda
    List<Articolo> findByAziendaId(Long aziendaId);
}
