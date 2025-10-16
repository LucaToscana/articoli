package com.gestione.articoli.mapper;


import com.gestione.articoli.dto.LavorazioneDto;
import com.gestione.articoli.model.Lavorazione;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LavorazioneMapper {

    public LavorazioneDto toDto(Lavorazione entity) {
        if (entity == null) return null;
        return LavorazioneDto.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descrizione(entity.getDescrizione())
                .build();
    }

    public Lavorazione toEntity(LavorazioneDto dto) {
        if (dto == null) return null;
        return Lavorazione.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .descrizione(dto.getDescrizione())
                .build();
    }

    public List<LavorazioneDto> toDtoList(List<Lavorazione> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}
