package com.gestione.articoli.service;

import com.gestione.articoli.dto.TotalWorkTimeDto;
import com.gestione.articoli.dto.WorkDto;
import java.util.List;

public interface WorkService {
    WorkDto createWork(WorkDto dto);
    WorkDto updateWork(Long id, WorkDto dto);
    void deleteWork(Long id);
    WorkDto getWorkById(Long id);
    List<WorkDto> getAllWorks();
    List<WorkDto> getWorksByOrderArticle(Long orderArticleId);
    long calculateTotalWorkSeconds(Long orderArticleId);
	TotalWorkTimeDto getTotalWorkTimeDto(Long orderArticleId);

}
