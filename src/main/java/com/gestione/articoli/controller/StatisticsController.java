package com.gestione.articoli.controller;


import com.gestione.articoli.dto.OrdineRisultatoDto;
import com.gestione.articoli.dto.StatisticsDto;
import com.gestione.articoli.model.OrdineRisultato;
import com.gestione.articoli.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    // Statistiche generali
    @GetMapping
    public StatisticsDto getStatistics(
            @RequestParam(required = false) Long aziendaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return statisticsService.calculateStatistics(start, end, aziendaId);
    }
    @GetMapping("/orders")
    public Map<Long, List<OrdineRisultatoDto>> getOrdini(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) Long aziendaId
    ) {
        return statisticsService.getOrdiniDettaglio(start, end, aziendaId);
    }
}

