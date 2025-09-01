package com.gestione.articoli.controller;

import com.gestione.articoli.dto.TotalWorkTimeDto;
import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @PostMapping
    public ResponseEntity<WorkDto> createWork(@RequestBody WorkDto dto) {
        return ResponseEntity.ok(workService.createWork(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkDto> updateWork(@PathVariable Long id, @RequestBody WorkDto dto) {
        return ResponseEntity.ok(workService.updateWork(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWork(@PathVariable Long id) {
        workService.deleteWork(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkDto> getWorkById(@PathVariable Long id) {
        return ResponseEntity.ok(workService.getWorkById(id));
    }

    @GetMapping
    public ResponseEntity<List<WorkDto>> getAllWorks() {
        return ResponseEntity.ok(workService.getAllWorks());
    }

    @GetMapping("/order-article/{orderArticleId}")
    public ResponseEntity<List<WorkDto>> getWorksByOrderArticle(@PathVariable Long orderArticleId) {
        return ResponseEntity.ok(workService.getWorksByOrderArticle(orderArticleId));
    }
    @GetMapping("/order-article/{orderArticleId}/total-time-dto")
    public ResponseEntity<?> getTotalWorkTimeDto(@PathVariable Long orderArticleId){
        try {
            TotalWorkTimeDto dto = workService.getTotalWorkTimeDto(orderArticleId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            // Errore noto, ad esempio OrdineArticolo non trovato
            return ResponseEntity.status(404).body("Errore: " + e.getMessage());
        } catch (Exception e) {
            // Errore generico
            return ResponseEntity.status(500).body("Errore interno del server: " + e.getMessage());
        }
    }

}
