package com.gestione.articoli.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TotalWorkTimeDto {
    private long hours;
    private long minutes;
    private long seconds;
}