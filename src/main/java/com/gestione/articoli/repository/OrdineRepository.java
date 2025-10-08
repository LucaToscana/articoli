package com.gestione.articoli.repository;

import com.gestione.articoli.model.Ordine;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrdineRepository extends JpaRepository<Ordine, Long> { 
	

    
	@Query("SELECT DISTINCT o " +
		       "FROM Ordine o " +
		       "JOIN FETCH o.azienda a " +
		       "LEFT JOIN FETCH o.articoli oa " +
		       "LEFT JOIN FETCH oa.articolo ar " +
		       "ORDER BY o.dataOrdine DESC")
		List<Ordine> findAllWithAziendaAndArticoli();
}
