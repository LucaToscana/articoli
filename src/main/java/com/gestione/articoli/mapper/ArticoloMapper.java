package com.gestione.articoli.mapper;

import com.gestione.articoli.dto.ArticoloDto;
import com.gestione.articoli.model.Articolo;

public class ArticoloMapper {

    public static ArticoloDto toDto(Articolo articolo) {
        if (articolo == null) return null;

        return ArticoloDto.builder()
                .id(articolo.getId())
                .codice(articolo.getCodice())
                .descrizione(articolo.getDescrizione())
                .build();
    }

    public static Articolo toEntity(ArticoloDto dto) {
        if (dto == null) return null;

        return Articolo.builder()
                .id(dto.getId())
                .codice(dto.getCodice())
                .descrizione(dto.getDescrizione())
                .build();
    }
}
