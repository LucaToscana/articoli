package com.gestione.articoli.mapper;

import com.gestione.articoli.dto.ArticoloDto;
import com.gestione.articoli.model.Articolo;

import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArticoloMapper {

    public static ArticoloDto toDto(Articolo articolo) {
        if (articolo == null) return null;

        String base64 = null;
        try {
            if (articolo.getImmagine() != null) {
                base64 = Base64.getEncoder().encodeToString(articolo.getImmagine());
            }
        } catch (Exception ignored) {}

        return ArticoloDto.builder()
                .id(articolo.getId())
                .codice(articolo.getCodice())
                .codiceComponente(articolo.getCodiceComponente())
                .descrizione(articolo.getDescrizione())
                .immagineBase64(base64)
                .dataCreazione(articolo.getDataCreazione())
                .prezzoIdeale(articolo.getPrezzoIdeale())
                .attivoPerProduzione(articolo.isAttivoPerProduzione())
                .azienda(AziendaMapper.toDto(articolo.getAzienda()))
                .articoliPadriIds(
                        Optional.ofNullable(articolo.getArticoliPadri())
                                .orElse(Collections.emptySet())
                                .stream().map(Articolo::getId).collect(Collectors.toSet())
                )
                .articoliFigliIds(
                        Optional.ofNullable(articolo.getArticoliFigli())
                                .orElse(Collections.emptySet())
                                .stream().map(Articolo::getId).collect(Collectors.toSet())
                )
                .build();
    }

    public static Articolo toEntity(ArticoloDto dto) {
        if (dto == null) return null;

        byte[] immagine = Optional.ofNullable(dto.getImmagineBase64())
                                  .map(Base64.getDecoder()::decode)
                                  .orElse(null);

        Articolo articolo = Articolo.builder()
                .id(dto.getId())
                .codice(dto.getCodice())
                .codiceComponente(dto.getCodiceComponente())
                .descrizione(dto.getDescrizione())
                .immagine(immagine)
                .dataCreazione(dto.getDataCreazione())
                .prezzoIdeale(dto.getPrezzoIdeale())
                .attivoPerProduzione(dto.isAttivoPerProduzione())
                .azienda(AziendaMapper.toEntity(dto.getAzienda()))
                .build();

        Optional.ofNullable(dto.getArticoliPadriIds())
                .ifPresent(ids -> articolo.setArticoliPadri(
                        ids.stream().map(id -> Articolo.builder().id(id).build()).collect(Collectors.toSet())
                ));

        Optional.ofNullable(dto.getArticoliFigliIds())
                .ifPresent(ids -> articolo.setArticoliFigli(
                        ids.stream().map(id -> Articolo.builder().id(id).build()).collect(Collectors.toSet())
                ));

        return articolo;
    }
}
