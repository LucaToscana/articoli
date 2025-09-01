package com.gestione.articoli.controller;

import com.gestione.articoli.dto.OrdineDto;
import com.gestione.articoli.service.OrdineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordini")
public class OrdineController {

    private final OrdineService ordineService;

    public OrdineController(OrdineService ordineService) {
        this.ordineService = ordineService;
    }

    @GetMapping
    public List<OrdineDto> getAll() {
        return ordineService.getAllOrdini();
    }

    @GetMapping("/{id}")
    public OrdineDto getById(@PathVariable Long id) {
        return ordineService.getOrdineById(id);
    }

    @PostMapping
    public OrdineDto create(@RequestBody OrdineDto dto) {
        return ordineService.createOrdine(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ordineService.deleteOrdine(id);
    }
}
