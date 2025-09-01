package com.gestione.articoli.repository;

import com.gestione.articoli.model.Azienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AziendaRepository extends JpaRepository<Azienda, Long> {

    // Trova un'azienda per nome
    Optional<Azienda> findByNome(String nome);

    // Trova un'azienda per partita IVA
    Optional<Azienda> findByPartitaIva(String partitaIva);

    // Eventuali query custom possono essere aggiunte qui
}
