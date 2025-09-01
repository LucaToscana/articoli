package com.gestione.articoli.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdineDto {

    private Long id;
    private LocalDateTime dataOrdine;

    private Set<OrdineArticoloDto> articoli;
}
