package com.gestione.articoli.controller;

import com.gestione.articoli.dto.ArticoloDto;
import com.gestione.articoli.mapper.ArticoloMapper;
import com.gestione.articoli.model.Articolo;
import com.gestione.articoli.repository.ArticoloRepository;
import com.gestione.articoli.service.ArticoloService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articoli")
@RequiredArgsConstructor
public class ArticoloController {

    private final ArticoloService articoloService;
    private final ArticoloRepository articoloRepository;

 // Metodo esistente
    @GetMapping
    public List<ArticoloDto> getAll() {
        return articoloService.findAllNoPagination();
    }

    // Metodo paginato
    @GetMapping("/page")
    public Page<ArticoloDto> getArticoli(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return articoloService.findAll(page, size);
    }
    
    @GetMapping("/parents")
    public List<ArticoloDto> getAllParents() {
        return articoloService.findAllParents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticoloDto> getById(@PathVariable Long id) {
        ArticoloDto dto = articoloService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ArticoloDto> create(@RequestBody ArticoloDto articoloDto) {
        ArticoloDto saved = articoloService.save(articoloDto);
        return ResponseEntity.ok(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ArticoloDto> update(@PathVariable Long id,
                                              @RequestBody ArticoloDto articoloDto) {
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

    @Transactional
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadImage(@PathVariable Long id,
                                         @RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Nessun file");

        Optional<Articolo> optionalArticolo = articoloService.findEntityById(id);
        if (optionalArticolo.isEmpty()) return ResponseEntity.notFound().build();

        Articolo articolo = optionalArticolo.get();
        try {
            articolo.setImmagine(file.getBytes());
            articoloRepository.save(articolo);
            return ResponseEntity.ok(ArticoloMapper.toDto(articolo));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore IO: " + e.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleError(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500)
                .body("Errore: " + e.getMessage());
    }
}
