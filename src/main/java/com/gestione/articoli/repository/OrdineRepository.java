package com.gestione.articoli.repository;

import com.gestione.articoli.model.Ordine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdineRepository extends JpaRepository<Ordine, Long> { }
