package com.gestione.articoli.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AziendaDto {

    private Long id;
    private String nome;
    private String partitaIva;

    // Lista degli ID degli articoli collegati (senza includere tutti i dati degli articoli)
    @Builder.Default
    private List<Long> articoliIds = new ArrayList<>();

    // Costruttore comodo con solo nome
    public AziendaDto(String nome) {
        this.nome = nome;
    }

    // Costruttore con nome e partita IVA
    public AziendaDto(String nome, String partitaIva) {
        this.nome = nome;
        this.partitaIva = partitaIva;
    }
}
