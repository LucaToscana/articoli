package com.gestione.articoli.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LavorazioneDto {
    private Long id;
    private String nome;
    private String descrizione;
}
