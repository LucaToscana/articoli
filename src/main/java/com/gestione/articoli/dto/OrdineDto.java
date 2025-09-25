package com.gestione.articoli.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

import com.gestione.articoli.model.WorkStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OrdineDto {

    private Long id;
    private LocalDateTime dataOrdine;
    // ID azienda associata
    private Long aziendaId;
    private String nomeAzienda;
    private boolean hasDdt;
    private String nomeDocumento;
    private WorkStatus workStatus; 
    private Set<OrdineArticoloDto> articoli;
}
