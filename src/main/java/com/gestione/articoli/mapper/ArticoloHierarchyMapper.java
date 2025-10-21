package com.gestione.articoli.mapper;

import com.gestione.articoli.dto.ArticoloHierarchyDto;
import com.gestione.articoli.dto.ArticoloMiniDto;
import com.gestione.articoli.model.Articolo;

import java.util.*;
import java.util.stream.Collectors;

public class ArticoloHierarchyMapper {
	
	public static ArticoloHierarchyDto toHierarchyDto(Articolo articolo) {
		return toHierarchyDto(articolo, new HashSet<>());
	}

	public static ArticoloHierarchyDto toHierarchyDto(Articolo articolo, Set<Long> visited) {
		if (articolo == null || visited.contains(articolo.getId()))
			return null;
		visited.add(articolo.getId());

		ArticoloHierarchyDto dto = new ArticoloHierarchyDto();
		dto.setId(articolo.getId());
		dto.setCodice(articolo.getCodice());
		dto.setCodiceComponente(articolo.getCodiceComponente());
		dto.setDescrizione(articolo.getDescrizione());
		dto.setAziendaNome(articolo.getAzienda() != null ? articolo.getAzienda().getNome() : null);

		if (articolo.getImmagine() != null) {
			dto.setImmagineBase64(Base64.getEncoder().encodeToString(articolo.getImmagine()));
		}

		// padri
		if (articolo.getArticoliPadri() != null) {
			dto.setPadriCount(articolo.getArticoliPadri().size());
			dto.setPadri(articolo.getArticoliPadri().stream().map(p -> {
				ArticoloMiniDto mini = new ArticoloMiniDto();
				mini.setId(p.getId());
				mini.setCodice(p.getCodice());
				mini.setDescrizione(p.getDescrizione());
				if (p.getImmagine() != null) {
					mini.setImmagineBase64(Base64.getEncoder().encodeToString(p.getImmagine()));
				}
				return mini;
			}).collect(Collectors.toList()));
		}

		// figli
		if (articolo.getArticoliFigli() != null) {
			dto.setFigliCount(articolo.getArticoliFigli().size());
			dto.setFigli(articolo.getArticoliFigli().stream().map(f -> toHierarchyDto(f, visited))
					.filter(Objects::nonNull).collect(Collectors.toList()));
		}

		return dto;
	}
}
