package com.gestione.articoli.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdineArticoloDto {

    private Long id;
    private Long ordineId;
    private Long articoloId;
    private int quantita;

    // dati articolo/azienda per frontend
    private String articoloCodice;
    private String articoloDescrizione;
    private String articoloImmagineBase64;
    private String aziendaNome;
}
