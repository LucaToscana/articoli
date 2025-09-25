package com.gestione.articoli.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticoloMiniDto {
    private Long id;
    private String codice;
    private String codiceComponente;
    private String descrizione;
    private String immagineBase64;
}
