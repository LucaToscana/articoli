package com.gestione.articoli.repository;

import com.gestione.articoli.model.OrdineRisultato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdineRisultatoRepository extends JpaRepository<OrdineRisultato, Long> {
    List<OrdineRisultato> findByOrdineId(Long ordineId);
    List<OrdineRisultato> findByArticoloId(Long articoloId);
	void deleteByOrdineId(Long ordineId);

    @Query("SELECT r FROM OrdineRisultato r " +
           "JOIN FETCH r.ordine o " +
           "JOIN FETCH r.articolo a " +
           "WHERE o.id = :ordineId")
    List<OrdineRisultato> findByOrdineIdWithJoin(@Param("ordineId") Long ordineId);
    
    @Query("SELECT r FROM OrdineRisultato r " +
    	       "WHERE r.ordine.dataOrdine >= :start AND r.ordine.dataOrdine <= :end " +
    	       "AND (:aziendaId IS NULL OR r.ordine.azienda.id = :aziendaId) " +
    	       "AND r.ordine.workStatus = 'COMPLETED' " + 
    	       "ORDER BY r.ordine.dataOrdine ASC")
    List<OrdineRisultato> findOrdiniByDataRangeAndAzienda(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("aziendaId") Long aziendaId
    );


    
}


