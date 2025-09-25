package com.gestione.articoli.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticoloHierarchyDto {
    private Long id;
    private String codice;
    private String codiceComponente;

    private String descrizione;
    private String aziendaNome;
    private String immagineBase64;

    private int padriCount;
    private int figliCount;

    // Per evitare cicli infiniti:
    private List<ArticoloHierarchyDto> figli;
    private List<ArticoloMiniDto> padri;
    private Long articoloOrdine;
}
