package com.gestione.articoli.mapper;

import com.gestione.articoli.model.Work;
import com.gestione.articoli.model.WorkStatus;
import com.gestione.articoli.dto.WorkDto;

public class WorkMapper {

    // Converte Work (entity) → WorkDto
    public static WorkDto toDto(Work entity) {
        if (entity == null) return null;

        return WorkDto.builder()
                .id(entity.getId())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null) // enum → String
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .orderArticleId(entity.getOrderArticle() != null ? entity.getOrderArticle().getId() : null)
                .build();
    }

    // Converte WorkDto → Work (entity)
    public static Work toEntity(WorkDto dto) {
        if (dto == null) return null;

        return Work.builder()
                .id(dto.getId())
                .status(dto.getStatus() != null ? WorkStatus.valueOf(dto.getStatus()) : null) // String → enum
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                // orderArticle va settato nel service per evitare problemi con lazy loading
                .build();
    }
}
