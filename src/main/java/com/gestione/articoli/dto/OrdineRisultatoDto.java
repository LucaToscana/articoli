package com.gestione.articoli.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdineRisultatoDto {
    private Long id;
    private Long ordineId;
    private Long articoloId;

    // MOLATURA
    private BigDecimal molaturaReale;
    private BigDecimal molaturaFatturabile;

    // LUCIDATURA
    private BigDecimal lucidaturaReale;
    private BigDecimal lucidaturaFatturabile;

    // SALDATURA
    private BigDecimal saldaturaReale;
    private BigDecimal saldaturaFatturabile;

    // FORATURA
    private BigDecimal foraturaReale;
    private BigDecimal foraturaFatturabile;

    // FILETTATURA
    private BigDecimal filettaturaReale;
    private BigDecimal filettaturaFatturabile;

    // MONTAGGIO
    private BigDecimal montaggioReale;
    private BigDecimal montaggioFatturabile;

    // SCATOLATURA
    private BigDecimal scatolaturaReale;
    private BigDecimal scatolaturaFatturabile;

    private LocalDateTime dataRisultato;
    private BigDecimal prezzo;
    private BigDecimal quantita;
}
