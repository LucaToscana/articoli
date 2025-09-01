package com.gestione.articoli.controller;

import com.gestione.articoli.dto.AziendaDto;
import com.gestione.articoli.service.AziendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aziende")
@RequiredArgsConstructor
public class AziendaController {

    private final AziendaService aziendaService;

    @GetMapping
    public ResponseEntity<List<AziendaDto>> getAll() {
        return ResponseEntity.ok(aziendaService.getAllAziende());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AziendaDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(aziendaService.getAziendaById(id));
    }

    @PostMapping
    public ResponseEntity<AziendaDto> create(@RequestBody AziendaDto dto) {
        return ResponseEntity.ok(aziendaService.createAzienda(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AziendaDto> update(@PathVariable Long id, @RequestBody AziendaDto dto) {
        return ResponseEntity.ok(aziendaService.updateAzienda(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        aziendaService.deleteAzienda(id);
        return ResponseEntity.noContent().build();
    }
}
