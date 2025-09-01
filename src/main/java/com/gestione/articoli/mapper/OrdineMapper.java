package com.gestione.articoli.mapper;

import com.gestione.articoli.dto.*;
import com.gestione.articoli.model.*;

import java.util.stream.Collectors;
import java.util.Base64;

public class OrdineMapper {

    public static OrdineDto toDto(Ordine ordine) {
        if (ordine == null) return null;

        return OrdineDto.builder()
                .id(ordine.getId())
                .dataOrdine(ordine.getDataOrdine())
                .articoli(ordine.getArticoli().stream()
                        .map(OrdineArticoloMapper::toDto)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static Ordine toEntity(OrdineDto dto) {
        if (dto == null) return null;

        Ordine ordine = Ordine.builder()
                .id(dto.getId())
                .dataOrdine(dto.getDataOrdine())
                .build();

        if (dto.getArticoli() != null) {
            dto.getArticoli().forEach(aDto -> {
                OrdineArticolo oa = OrdineArticoloMapper.toEntity(aDto);
                oa.setOrdine(ordine);
                ordine.getArticoli().add(oa);
            });
        }

        return ordine;
    }
}
