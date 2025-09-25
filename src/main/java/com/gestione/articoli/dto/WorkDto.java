package com.gestione.articoli.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkDto {
	private Long id;
	private String status;
	private String activity;

	// lavoro manuale
	private List<String> posizioni;
	private String specifiche;
	private String grana;
	private String pastaColore;

	private int quantita;
	private LocalDateTime startTime;
	private LocalDateTime endTime;

	private Long orderArticleId;
	private ArticoloDto articolo;
	private OrdineDto ordine;
	private OrdineArticoloDto ordineArticolo;

	private UserDto manager;
	private UserDto operator;
	private UserDto operator2;
	private UserDto operator3;
}
