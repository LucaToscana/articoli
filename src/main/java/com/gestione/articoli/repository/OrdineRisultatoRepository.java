package com.gestione.articoli.repository;

import com.gestione.articoli.model.OrdineRisultato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdineRisultatoRepository extends JpaRepository<OrdineRisultato, Long> {
    List<OrdineRisultato> findByOrdineId(Long ordineId);
    List<OrdineRisultato> findByArticoloId(Long articoloId);
}
