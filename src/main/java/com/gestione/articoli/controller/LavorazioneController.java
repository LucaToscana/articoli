package com.gestione.articoli.controller;

import com.gestione.articoli.dto.LavorazioneDto;
import com.gestione.articoli.service.LavorazioneService;
import com.gestione.articoli.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/lavorazioni")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LavorazioneController {

    private final LavorazioneService lavorazioneService;
	private final UserService userService;

    @GetMapping
    public ResponseEntity<List<LavorazioneDto>> getAll() {
        return ResponseEntity.ok(lavorazioneService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LavorazioneDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lavorazioneService.findById(id));
    }

    @PostMapping
    public ResponseEntity<LavorazioneDto> create(@RequestBody LavorazioneDto dto) {
        return ResponseEntity.ok(lavorazioneService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LavorazioneDto> update(@PathVariable Long id, @RequestBody LavorazioneDto dto) {
        return ResponseEntity.ok(lavorazioneService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lavorazioneService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    // 1️⃣ Lista lavorazioni di un utente
    @GetMapping("/{id}/lavorazioni")
    public ResponseEntity<Set<LavorazioneDto>> getLavorazioni(@PathVariable Long id) {
        Set<LavorazioneDto> lavorazioni = userService.getLavorazioniByUserId(id);
        return ResponseEntity.ok(lavorazioni);
    }

    // 2️⃣ Assegna una lavorazione a un utente
    @PostMapping("/{id}/lavorazioni/{lavorazioneId}")
    public ResponseEntity<Void> addLavorazione(
            @PathVariable Long id,
            @PathVariable Long lavorazioneId) {
        userService.addLavorazioneToUser(id, lavorazioneId);
        return ResponseEntity.ok().build();
    }

    // 3️⃣ Rimuovi una lavorazione da un utente
    @DeleteMapping("/{id}/lavorazioni/{lavorazioneId}")
    public ResponseEntity<Void> removeLavorazione(
            @PathVariable Long id,
            @PathVariable Long lavorazioneId) {
        userService.removeLavorazioneFromUser(id, lavorazioneId);
        return ResponseEntity.noContent().build();
    }
}
