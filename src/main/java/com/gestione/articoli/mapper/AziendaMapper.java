package com.gestione.articoli.mapper;

import com.gestione.articoli.dto.AziendaDto;
import com.gestione.articoli.model.Azienda;

import java.util.stream.Collectors;

public class AziendaMapper {

    public static AziendaDto toDto(Azienda azienda) {
        if (azienda == null) return null;

        return AziendaDto.builder()
                .id(azienda.getId())
                .nome(azienda.getNome())
                .partitaIva(azienda.getPartitaIva())
               /* .articoliIds(
                        azienda.getArticoli() != null
                                ? azienda.getArticoli().stream()
                                        .map(a -> a.getId())
                                        .collect(Collectors.toList())
                                : null
                )*/
                .build();
    }

    public static Azienda toEntity(AziendaDto dto) {
        if (dto == null) return null;

        Azienda.AziendaBuilder builder = Azienda.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .partitaIva(dto.getPartitaIva());

        return builder.build();
    }
}
