package com.gestione.articoli.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestione.articoli.dto.ArticoloDto;
import com.gestione.articoli.dto.ArticoloHierarchyDto;
import com.gestione.articoli.dto.FastOrderDto;
import com.gestione.articoli.dto.OrdineArticoloPrezzoDto;
import com.gestione.articoli.dto.OrdineDto;
import com.gestione.articoli.service.OrdineService;

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
	@PostMapping("/fast-order")
	public ArticoloDto createFastOrder(@RequestBody FastOrderDto dto) {
		return ordineService.createFastOrder(dto);
	}

	@PutMapping("/{id}")
	public OrdineDto update(@PathVariable Long id, @RequestBody OrdineDto dto) {
		System.out.println("OrdineController OrdineDto update" + dto);

		return ordineService.updateOrdine(id, dto);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		ordineService.deleteOrdine(id);
	}

	@GetMapping("/{id}/articoli-gerarchia")
	public ResponseEntity<List<ArticoloHierarchyDto>> getArticoliGerarchia(@PathVariable Long id) {
		List<ArticoloHierarchyDto> articoli = ordineService.getGerarchiaArticoliByOrdineId(id);
		return ResponseEntity.ok(articoli);
	}

    /**
     * Recupera la lista di prezzi unitari per più articoli di un ordine
     */
    @PostMapping("/prezzi")
    public ResponseEntity<?> aggiornaPrezziUnitari(@RequestBody List<OrdineArticoloPrezzoDto> prezziDtoList) {
        try {
            ordineService.aggiornaPrezziCreaDatiFattura(prezziDtoList);
            return ResponseEntity.ok("Prezzi aggiornati correttamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Errore durante l'aggiornamento dei prezzi: " + e.getMessage());
        }
    }

    /**
     * Recupera i prezzi unitari di un ordine specifico
     */
    @GetMapping("/{ordineId}/prezzi")
    public ResponseEntity<List<OrdineArticoloPrezzoDto>> getPrezziByOrdine(@PathVariable Long ordineId) {
        List<OrdineArticoloPrezzoDto> prezziList = ordineService.getPrezziByOrdine(ordineId);
        return ResponseEntity.ok(prezziList);
    }
}
