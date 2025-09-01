package com.gestione.articoli.controller;

import com.gestione.articoli.dto.OrdineArticoloDto;
import com.gestione.articoli.service.OrdineArticoloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ordine-articoli")
@RequiredArgsConstructor
public class OrdineArticoloController {

    private final OrdineArticoloService service;

    @PostMapping
    public ResponseEntity<OrdineArticoloDto> create(@RequestBody OrdineArticoloDto dto){
        return ResponseEntity.ok(service.createOrdineArticolo(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdineArticoloDto> update(@PathVariable Long id, @RequestBody OrdineArticoloDto dto){
        return ResponseEntity.ok(service.updateOrdineArticolo(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.deleteOrdineArticolo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdineArticoloDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(service.getOrdineArticoloById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrdineArticoloDto>> getAll(){
        return ResponseEntity.ok(service.getAllOrdineArticoli());
    }
}
