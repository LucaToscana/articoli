package com.gestione.articoli.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdineArticoloPrezzoDto {

    private Long ordineId;      // id dell'ordine
    private Long articoloId;    // id dell'articolo
    private BigDecimal prezzoUnitario; // prezzo unitario da aggiornare
}
