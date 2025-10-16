package com.gestione.articoli.repository;

import com.gestione.articoli.model.Lavorazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LavorazioneRepository extends JpaRepository<Lavorazione, Long> {

    Optional<Lavorazione> findByNome(String nome);

    boolean existsByNome(String nome);
}
