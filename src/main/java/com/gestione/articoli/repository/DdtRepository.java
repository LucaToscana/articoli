package com.gestione.articoli.repository;


import com.gestione.articoli.model.Ddt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DdtRepository extends JpaRepository<Ddt, Long> {
    Optional<Ddt> findTopByOrderByProgressivoDesc();
}
