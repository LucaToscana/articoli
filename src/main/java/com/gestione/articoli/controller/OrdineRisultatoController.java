package com.gestione.articoli.controller;


import com.gestione.articoli.dto.OrdineRisultatoDto;
import com.gestione.articoli.mapper.OrdineRisultatoMapper;
import com.gestione.articoli.model.Ordine;
import com.gestione.articoli.model.OrdineRisultato;
import com.gestione.articoli.service.OrdineRisultatoService;
import com.gestione.articoli.service.OrdineService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ordine-risultati")
@RequiredArgsConstructor
public class OrdineRisultatoController {

    private final OrdineRisultatoService service;
    private final OrdineService ordineService;

    @PostMapping
    public ResponseEntity<OrdineRisultatoDto> create(@RequestBody OrdineRisultatoDto dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdineRisultatoDto> getById(@PathVariable Long id) {
        OrdineRisultatoDto result = service.getById(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<OrdineRisultatoDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/ordine/{ordineId}")
    public ResponseEntity<List<OrdineRisultatoDto>> getByOrdine(@PathVariable Long ordineId) {
        return ResponseEntity.ok(service.getByOrdineId(ordineId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
 // Elimina tutti i risultati di un ordine
    @DeleteMapping("/ordine/{ordineId}")
    public ResponseEntity<Void> deleteByOrdine(@PathVariable Long ordineId) {
        service.deleteByOrdineId(ordineId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/generate/{ordineId}")
    public ResponseEntity<List<OrdineRisultatoDto>> generaRisultati(
            @PathVariable Long ordineId,
            @RequestParam("prezzo") BigDecimal prezzo // ✅ viene passato come query param
    ) {

        List<OrdineRisultato> risultati = service.generaRisultatiDaWorks(ordineId, prezzo);

        List<OrdineRisultatoDto> dtoList = risultati.stream()
                .map(OrdineRisultatoMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

}
