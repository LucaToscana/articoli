package com.gestione.articoli.repository;

import com.gestione.articoli.model.Work;
import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.model.WorkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkRepository extends JpaRepository<Work, Long> {

    // Tutti i lavori di un OrdineArticolo
    List<Work> findByOrderArticle(OrdineArticolo ordineArticolo);

    // Tutti i lavori di un OrdineArticolo con uno specifico stato
    List<Work> findByOrderArticleAndStatus(OrdineArticolo ordineArticolo, WorkStatus status);

    // Tutti i lavori in corso
    List<Work> findByStatus(WorkStatus status);

    // Tutti i lavori tra due date
    List<Work> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // Tutti i lavori completati di un OrdineArticolo
    List<Work> findByOrderArticleAndStatusOrderByEndTimeAsc(OrdineArticolo ordineArticolo, WorkStatus status);

    // Conteggio dei lavori in corso per un OrdineArticolo
    long countByOrderArticleAndStatus(OrdineArticolo ordineArticolo, WorkStatus status);

    // Tutti i lavori di più stati specifici (IN_PROGRESS, ON_HOLD, etc.) per un OrdineArticolo
    List<Work> findByOrderArticleAndStatusIn(OrdineArticolo ordineArticolo, List<WorkStatus> statuses);

    // Tutti i lavori ordinati per data inizio decrescente
    List<Work> findByOrderArticleOrderByStartTimeDesc(OrdineArticolo ordineArticolo);

    // Query personalizzata: somma della durata dei lavori (endTime - startTime) per un OrdineArticolo, ignorando PAUSED e CANCELLED
    @Query("SELECT SUM(FUNCTION('TIMESTAMPDIFF', SECOND, w.startTime, w.endTime)) " +
           "FROM Work w WHERE w.orderArticle = :ordineArticolo AND w.status NOT IN :excludedStatuses")
    Long getTotalWorkDurationInSeconds(@Param("ordineArticolo") OrdineArticolo ordineArticolo,
                                       @Param("excludedStatuses") List<WorkStatus> excludedStatuses);
}
