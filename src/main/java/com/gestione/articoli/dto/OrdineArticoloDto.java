package com.gestione.articoli.dto;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "articoloImmagineBase64")
public class OrdineArticoloDto {

    private Long id;
    private Long ordineId;
    private Long articoloId;
    private int quantita;

    // dati articolo/azienda per frontend
    private String articoloCodice;
    private String articoloCodiceComponente;
    private String articoloDescrizione;
    private String articoloImmagineBase64;
    
    private Long aziendaId;
    private String aziendaNome;
    BigDecimal prezzo;

    private BigDecimal iva = BigDecimal.ZERO;
    private BigDecimal prezzoLordo = BigDecimal.ZERO;
    private BigDecimal totaleMinutiLavorazioni = BigDecimal.ZERO;
    
}
