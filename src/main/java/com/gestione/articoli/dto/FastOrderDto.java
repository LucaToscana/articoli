package com.gestione.articoli.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FastOrderDto {

	private ArticoloDto articolo; // DTO articolo
	private OrdineDto ordine; // DTO ordine
	private Integer quantita;
	private boolean immediatelyVisible;
}