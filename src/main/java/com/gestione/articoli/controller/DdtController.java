package com.gestione.articoli.controller;

import com.gestione.articoli.dto.DdtDto;
import com.gestione.articoli.service.DdtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ddt")
@RequiredArgsConstructor
public class DdtController {

    private final DdtService ddtService;

    @PostMapping("/crea/{ordineId}")
    public ResponseEntity<DdtDto> creaDdt(
            @PathVariable Long ordineId,
            @RequestParam String causale,
            @RequestParam String partenza,
            @RequestParam String arrivo) {
        return ResponseEntity.ok(ddtService.creaDdt(ordineId, causale, partenza, arrivo));
    }

    @GetMapping
    public ResponseEntity<List<DdtDto>> getAll() {
        return ResponseEntity.ok(ddtService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DdtDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ddtService.getById(id));
    }
}
