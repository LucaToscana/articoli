package com.gestione.articoli.repository;

import com.gestione.articoli.model.OrdineArticolo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrdineArticoloRepository extends JpaRepository<OrdineArticolo, Long> {

    // Trova tutti gli articoli di un ordine specifico
    List<OrdineArticolo> findByOrdineId(Long ordineId);
}
