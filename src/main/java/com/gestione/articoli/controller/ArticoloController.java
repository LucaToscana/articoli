package com.gestione.articoli.controller;

import com.gestione.articoli.dto.ArticoloDto;
import com.gestione.articoli.service.ArticoloService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articoli")
@RequiredArgsConstructor
public class ArticoloController {

    private final ArticoloService articoloService;

    @GetMapping
    public List<ArticoloDto> getAll() {
        return articoloService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticoloDto> getById(@PathVariable Long id) {
        ArticoloDto dto = articoloService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ArticoloDto create(@RequestBody ArticoloDto articoloDto) {
        return articoloService.save(articoloDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ArticoloDto> update(@PathVariable Long id, @RequestBody ArticoloDto articoloDto) {
        ArticoloDto existing = articoloService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        articoloDto.setId(id);
        ArticoloDto updated = articoloService.save(articoloDto);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        articoloService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
