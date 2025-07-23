package com.gestione.articoli.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter 
@Setter
public class ArticoloDto {
    private Long id;
    private String codice;
    private String descrizione;
	public ArticoloDto(Long id, String codice) {
		super();
		this.id = id;
		this.codice = codice;
	}
    
    
}
