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

import com.gestione.articoli.dto.ArticoloHierarchyDto;
import com.gestione.articoli.dto.OrdineDto;
import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.service.OrdineService;
import com.gestione.articoli.service.WorkService;

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

}
