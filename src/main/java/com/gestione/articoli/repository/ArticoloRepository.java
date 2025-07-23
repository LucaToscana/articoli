package com.gestione.articoli.repository;

import com.gestione.articoli.model.Articolo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticoloRepository extends JpaRepository<Articolo, Long> {
    boolean existsByCodice(String codice);
}
