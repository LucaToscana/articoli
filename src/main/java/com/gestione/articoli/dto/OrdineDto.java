package com.gestione.articoli.dto;

import lombok.*;

import java.math.BigDecimal;
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
    private LocalDateTime dataFattura;

    private String numeroFattura;

    // Costi e parametri
    private BigDecimal costoOrario;
    private BigDecimal costoPersonaleMedio;
    private BigDecimal iva;
    private BigDecimal ricaricoBase;

    // Totali della fattura
    private BigDecimal totaleNetto;
    private BigDecimal totaleIva;
    private BigDecimal totaleLordo;
    
    private BigDecimal totaleMinutiLavorazioni = BigDecimal.ZERO;

}
