package com.gestione.articoli.mapper;

import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.dto.OrdineArticoloDto;
import java.util.Base64;

public class OrdineArticoloMapper {

    public static OrdineArticoloDto toDto(OrdineArticolo entity) {
        if (entity == null) return null;

        String immagine = null;
        if (entity.getArticolo() != null && entity.getArticolo().getImmagine() != null) {
            immagine = Base64.getEncoder().encodeToString(entity.getArticolo().getImmagine());
        }

        return OrdineArticoloDto.builder()
                .id(entity.getId())
                .ordineId(entity.getOrdine() != null ? entity.getOrdine().getId() : null)
                .articoloId(entity.getArticolo() != null ? entity.getArticolo().getId() : null)
                .quantita(entity.getQuantita())
                .articoloCodice(entity.getArticolo() != null ? entity.getArticolo().getCodice() : null)
                .articoloDescrizione(entity.getArticolo() != null ? entity.getArticolo().getDescrizione() : null)
                .articoloImmagineBase64(immagine)
                .aziendaNome(entity.getArticolo() != null && entity.getArticolo().getAzienda() != null
                        ? entity.getArticolo().getAzienda().getNome() : null)
                .build();
    }

    public static OrdineArticolo toEntity(OrdineArticoloDto dto) {
        if (dto == null) return null;

        return OrdineArticolo.builder()
                .id(dto.getId())
                .quantita(dto.getQuantita())
                // Ordine e Articolo devono essere settati nel service
                .build();
    }
}
