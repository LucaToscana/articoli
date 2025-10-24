package com.gestione.articoli.repository;

import com.gestione.articoli.model.Ordine;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrdineRepository extends JpaRepository<Ordine, Long> { 
	

    
	@Query("SELECT DISTINCT o " +
		       "FROM Ordine o " +
		       "JOIN FETCH o.azienda a " +
		       "LEFT JOIN FETCH o.articoli oa " +
		       "LEFT JOIN FETCH oa.articolo ar " +
		       "ORDER BY o.dataOrdine DESC")
		List<Ordine> findAllWithAziendaAndArticoli();
	
	
    @Query("SELECT o FROM Ordine o " +
            "WHERE o.dataOrdine >= :start AND o.dataOrdine <= :end " +
            "AND (:aziendaId IS NULL OR o.azienda.id = :aziendaId) " +
            "AND o.workStatus = 'COMPLETED' " +
            "ORDER BY o.dataOrdine ASC")
     List<Ordine> findOrdiniByDataRangeAndAzienda(
         @Param("start") LocalDateTime start,
         @Param("end") LocalDateTime end,
         @Param("aziendaId") Long aziendaId
     );
}
