package com.gestione.articoli.controller;

import com.gestione.articoli.dto.ParametraggioDTO;
import com.gestione.articoli.model.CategoriaParametraggio;
import com.gestione.articoli.service.ParametraggioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parametraggi")
@RequiredArgsConstructor
public class ParametraggioController {

    private final ParametraggioService service;

    @GetMapping
    public ResponseEntity<List<ParametraggioDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ParametraggioDTO>> getByCategoria(@PathVariable CategoriaParametraggio categoria) {
        return ResponseEntity.ok(service.getByCategoria(categoria));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<ParametraggioDTO> getByNome(@PathVariable String nome) {
        return ResponseEntity.ok(service.getByNome(nome));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParametraggioDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ParametraggioDTO> create(@RequestBody ParametraggioDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParametraggioDTO> update(@PathVariable Long id, @RequestBody ParametraggioDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(service.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
