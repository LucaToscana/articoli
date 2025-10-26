package com.gestione.articoli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdineConRisultatiDto {
    private OrdineDto ordine;
    private List<OrdineRisultatoDto> risultati;
}