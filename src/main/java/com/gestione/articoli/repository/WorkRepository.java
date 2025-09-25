package com.gestione.articoli.repository;

import com.gestione.articoli.model.Work;
import com.gestione.articoli.model.WorkActivityType;
import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.model.WorkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkRepository extends JpaRepository<Work, Long> {

    /* ========= STANDARD FINDERS ========= */

    List<Work> findByOrderArticleId(Long orderArticleId);

    List<Work> findByOrderArticle(OrdineArticolo ordineArticolo);

    List<Work> findByOrderArticleAndStatus(OrdineArticolo ordineArticolo, WorkStatus status);

    List<Work> findByStatus(WorkStatus status);

    List<Work> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Work> findByOrderArticleAndStatusOrderByEndTimeAsc(OrdineArticolo ordineArticolo, WorkStatus status);

    long countByOrderArticleAndStatus(OrdineArticolo ordineArticolo, WorkStatus status);

    List<Work> findByOrderArticleAndStatusIn(OrdineArticolo ordineArticolo, List<WorkStatus> statuses);

    List<Work> findByOrderArticleOrderByStartTimeDesc(OrdineArticolo ordineArticolo);

    /* ========= CUSTOM QUERIES ========= */

    // Somma della durata dei lavori per un OrdineArticolo, ignorando PAUSED e CANCELLED
    @Query("SELECT SUM(FUNCTION('TIMESTAMPDIFF', SECOND, w.startTime, w.endTime)) "
         + "FROM Work w WHERE w.orderArticle = :ordineArticolo AND w.status NOT IN :excludedStatuses")
    Long getTotalWorkDurationInSeconds(@Param("ordineArticolo") OrdineArticolo ordineArticolo,
                                       @Param("excludedStatuses") List<WorkStatus> excludedStatuses);

    /* ========= RICERCHE AVANZATE ========= */

    @Query("""
        SELECT w FROM Work w
        WHERE w.orderArticle.id = :orderArticleId
          AND w.articolo.id = :articoloId
          AND w.manager.id = :managerId
          AND w.operator.id = :operatorId
          AND w.activity = :activity
    """)
    List<Work> searchWorks(@Param("orderArticleId") Long orderArticleId,
                           @Param("articoloId") Long articoloId,
                           @Param("managerId") Long managerId,
                           @Param("operatorId") Long operatorId,
                           @Param("activity") WorkActivityType activity);

    @Query("""
        SELECT w FROM Work w
        WHERE w.orderArticle.id = :orderArticleId
          AND w.articolo.id = :articoloId
          AND w.operator.id = :operatorId
          AND w.activity = :activity
    """)
    List<Work> searchWorksAllManagers(@Param("orderArticleId") Long orderArticleId,
                                      @Param("articoloId") Long articoloId,
                                      @Param("operatorId") Long operatorId,
                                      @Param("activity") WorkActivityType activity);

    /* ========= WORKS IN PROGRESS ========= */

    @Query("""
        SELECT w FROM Work w
        WHERE w.status = com.gestione.articoli.model.WorkStatus.IN_PROGRESS
          AND w.activity = com.gestione.articoli.model.WorkActivityType.DISPONIBILITA_LAVORAZIONE
          AND w.endTime IS NULL
    """)
    List<Work> findActiveManualWorks();

    @Query("""
        SELECT w FROM Work w
        WHERE w.status = com.gestione.articoli.model.WorkStatus.IN_PROGRESS
          AND w.activity = com.gestione.articoli.model.WorkActivityType.DISPONIBILITA_LAVORAZIONE
          AND w.endTime IS NULL
    """)
    List<Work> findInProgressAvailabilityWorks();

    @Query("""
        SELECT w FROM Work w
        WHERE w.status = com.gestione.articoli.model.WorkStatus.IN_PROGRESS
          AND w.activity = com.gestione.articoli.model.WorkActivityType.DISPONIBILITA_LOTTO
          AND w.endTime IS NULL
    """)
    List<Work> findInProgressLottoWorks();

    /* ========= LOTTO WORKS ========= */

    @Query("""
        SELECT w FROM Work w
        WHERE w.activity = com.gestione.articoli.model.WorkActivityType.DISPONIBILITA_LOTTO
          AND w.endTime IS NULL
        ORDER BY w.startTime DESC
    """)
    List<Work> findLottoWorks();
    
    @Query("""
            SELECT w FROM Work w
            JOIN w.orderArticle oa
            JOIN oa.ordine o 
            WHERE w.activity = com.gestione.articoli.model.WorkActivityType.DISPONIBILITA_LOTTO
              AND w.endTime IS NULL
              AND o.id = :orderId
            ORDER BY w.startTime DESC
        """)
    List<Work> findLottoWorksByOrder(Long orderId);
    /* ========= WORKS WITH ORDERS IN PROGRESS ========= */

    @Query("""
        SELECT w FROM Work w
        JOIN w.orderArticle oa
        JOIN oa.ordine o
        WHERE w.status = 'IN_PROGRESS'
          AND w.activity = 'DISPONIBILITA_LAVORAZIONE'
          AND w.endTime IS NULL
          AND o.workStatus = 'IN_PROGRESS'
    """)
    List<Work> findAvailabilityWorksInProgressWithOrderInProgress();

    @Query("""
        SELECT w FROM Work w
        JOIN w.orderArticle oa
        JOIN oa.ordine o
        WHERE w.status = 'IN_PROGRESS'
          AND w.activity = 'DISPONIBILITA_LOTTO'
          AND w.endTime IS NULL
          AND o.workStatus = 'IN_PROGRESS'
    """)
    List<Work> findLottoInProgressWorksWithOrderInProgress();

    /* ========= MANUAL WORKS EXCLUDED ACTIVITIES (NATIVE QUERY) ========= */

    @Query(value = """
        SELECT * FROM (
            SELECT w.*,
                   ROW_NUMBER() OVER (
                       PARTITION BY w.order_article_id,
                                    w.articolo_id,
                                    w.activity,
                                    w.specifiche,
                                    w.grana,
                                    w.pasta_colore,
                                    w.operator_id,
                                    w.operator2_id,
                                    w.operator3_id
                       ORDER BY w.start_time DESC
                   ) AS rn
            FROM works w
            JOIN ordine_articoli oa ON w.order_article_id = oa.id
            JOIN ordini o ON oa.ordine_id = o.id
            WHERE w.activity NOT IN (:excludedActivities)
              AND o.work_status = 'IN_PROGRESS'
        ) sub
        WHERE rn = 1
    """, nativeQuery = true)
    List<Work> findInProgressManualWorksExcludedActivities(@Param("excludedActivities") List<String> excludedActivities);
    /* ========= MANUAL WORKS EXCLUDED ACTIVITIES (NATIVE QUERY) ========= */

    @Query(value = """
        SELECT * FROM (
            SELECT w.*,
                   ROW_NUMBER() OVER (
                       PARTITION BY w.order_article_id,
                                    w.articolo_id,
                                    w.activity,
                                    w.specifiche,
                                    w.grana,
                                    w.pasta_colore,
                                    w.operator_id,
                                    w.operator2_id,
                                    w.operator3_id
                       ORDER BY w.start_time DESC
                   ) AS rn
            FROM works w
            JOIN ordine_articoli oa ON w.order_article_id = oa.id
            JOIN ordini o ON oa.ordine_id = o.id
            WHERE w.activity NOT IN (:excludedActivities)
              AND o.work_status = 'IN_PROGRESS'
              AND o.id = :orderId
        ) sub
        WHERE rn = 1
    """, nativeQuery = true)
    List<Work> findInProgressManualWorksExcludedActivitiesByOrder(@Param("excludedActivities") List<String> excludedActivities,Long orderId);

    /* ========= WORKS BY ORDER ========= */

    @Query("""
        SELECT w FROM Work w
        JOIN w.orderArticle oa
        JOIN oa.ordine o
        WHERE w.activity = 'DISPONIBILITA_LAVORAZIONE'
          AND w.endTime IS NULL
          AND o.id = :orderId
    """)
    List<Work> findAvailabilityWorksByOrder(Long orderId);

    @Query("""
        SELECT w FROM Work w
        JOIN w.orderArticle oa
        JOIN oa.ordine o
        WHERE w.activity = com.gestione.articoli.model.WorkActivityType.DISPONIBILITA_LOTTO
          AND w.endTime IS NULL
          AND o.id = :orderId
        ORDER BY w.startTime DESC
    """)
    List<Work> findLottoWorksByOrder();
}
