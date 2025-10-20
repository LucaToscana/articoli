package com.gestione.articoli.service;

import com.gestione.articoli.dto.StartWorkDto;
import com.gestione.articoli.dto.TotalWorkTimeDto;
import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.model.WorkStatus;

import java.util.List;

public interface WorkService {

    /* ========= CREATE ========= */
    WorkDto createWork(WorkDto dto);
    WorkDto startWork(StartWorkDto dto);

    /* ========= READ ========= */
    WorkDto getWorkById(Long id);
    List<WorkDto> getAllWorks();
    List<WorkDto> getWorksByOrderArticle(Long orderArticleId);
    List<WorkDto> getWorksByOrderArticleId(Long orderArticleId);
    List<WorkDto> getWorksByOrderId(Long orderId);
	List<WorkDto> getInProgressManualByOrder(Long id);
    List<WorkDto> getInProgressAvailabilityWorks();
	List<WorkDto> getAvailabilityWorksByOrder(Long orderId);
	List<WorkDto> getLottoWorksByOrder(Long id);
	List<WorkDto> getManualWorksWithTotalMinutesByOrderInProgress(Long orderId);
	List<WorkDto> getManualWorksWithTotalMinutesByOrder(Long orderId);
	List<WorkDto> getNotCompletedManualWorksExcludedActivitiesByOrderWithAllStatus(Long id);

    List<WorkDto> getInProgressManualWorks();
    List<WorkDto> getInProgressLottoWorks();
	List<WorkDto> getInProgressLottoWorksWithOrderInProgress();
	List<WorkDto> getLottoWorks();

	List<WorkDto> getStepsByWork(Long id);

	
    long calculateTotalWorkSeconds(Long orderArticleId);
    TotalWorkTimeDto getTotalWorkTimeDto(Long orderArticleId);

    /* ========= UPDATE ========= */
    WorkDto updateWork(Long id, WorkDto dto);
	WorkDto updateLottoWork(Long id, WorkDto dto);
	
    /* ========= TRANSITIONS / ACTIONS ========= */
    WorkDto closeWork(Long id, WorkStatus status);
    WorkDto transitionWork(Long workId, WorkStatus newStatus);
    
    /* ========= DELETE ========= */
    void deleteWork(Long id);
	void deleteLottoWork(Long id);
	void cleanCompletedOrderWorks(Long orderId);

}

