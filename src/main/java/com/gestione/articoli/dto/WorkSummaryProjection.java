package com.gestione.articoli.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface WorkSummaryProjection {

    Long getId();
    Long getOrderArticleId();
    int getQuantita();
    String getStatus();
    LocalDateTime getOriginalStartTime();
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    Long getManagerId();
    Long getOperatorId();
    Long getOperator2Id();
    Long getOperator3Id();
    Long getArticoloId();
    String getActivity();
    String getSpecifiche();
    String getGrana();
    String getPastaColore();
    BigDecimal getTotalMinutes();
}
