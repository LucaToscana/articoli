package com.gestione.articoli.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DdtDto {
    private Long id;
    private Long progressivo;
    private String numeroDocumento;
    private LocalDateTime dataDocumento;
    private String causaleTrasporto;
    private String luogoPartenza;
    private String luogoArrivo;
    private Long ordineId;
}
