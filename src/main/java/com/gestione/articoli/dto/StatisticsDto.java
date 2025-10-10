package com.gestione.articoli.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
@AllArgsConstructor
public class StatisticsDto {
    private long totaleOrdini;
    private BigDecimal totaleOre;
    private BigDecimal totaleEuro;
    private BigDecimal mediaArticoliOra;
}
