package com.gestione.articoli.mapper;

import com.gestione.articoli.model.OrdineRisultato;
import com.gestione.articoli.dto.OrdineRisultatoDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class OrdineRisultatoMapper {

    public static OrdineRisultatoDto toDto(OrdineRisultato entity) {
        return OrdineRisultatoDto.builder()
                .id(entity.getId())
                .ordineId(entity.getOrdine().getId())
                .articoloId(entity.getArticolo().getId())

                .molaturaReale(entity.getMolaturaReale())
                .molaturaFatturabile(entity.getMolaturaFatturabile())

                .lucidaturaReale(entity.getLucidaturaReale())
                .lucidaturaFatturabile(entity.getLucidaturaFatturabile())

                .saldaturaReale(entity.getSaldaturaReale())
                .saldaturaFatturabile(entity.getSaldaturaFatturabile())

                .foraturaReale(entity.getForaturaReale())
                .foraturaFatturabile(entity.getForaturaFatturabile())

                .filettaturaReale(entity.getFilettaturaReale())
                .filettaturaFatturabile(entity.getFilettaturaFatturabile())

                .montaggioReale(entity.getMontaggioReale())
                .montaggioFatturabile(entity.getMontaggioFatturabile())

                .scatolaturaReale(entity.getScatolaturaReale())
                .scatolaturaFatturabile(entity.getScatolaturaFatturabile())

                .dataRisultato(entity.getDataRisultato())
                .prezzo(entity.getPrezzo())
                .quantita(entity.getQuantita() != null ? entity.getQuantita() : BigDecimal.ZERO)
                .build();
    }

    public static OrdineRisultato toEntity(OrdineRisultatoDto dto) {
        // Prende l'ora corrente in Italia se dataRisultato è null
        LocalDateTime dataRisultato = dto.getDataRisultato() != null
                ? dto.getDataRisultato()
                : ZonedDateTime.now(ZoneId.of("Europe/Rome")).toLocalDateTime();

        return OrdineRisultato.builder()
                .molaturaReale(dto.getMolaturaReale())
                .molaturaFatturabile(dto.getMolaturaFatturabile())
                .lucidaturaReale(dto.getLucidaturaReale())
                .lucidaturaFatturabile(dto.getLucidaturaFatturabile())
                .saldaturaReale(dto.getSaldaturaReale())
                .saldaturaFatturabile(dto.getSaldaturaFatturabile())
                .foraturaReale(dto.getForaturaReale())
                .foraturaFatturabile(dto.getForaturaFatturabile())
                .filettaturaReale(dto.getFilettaturaReale())
                .filettaturaFatturabile(dto.getFilettaturaFatturabile())
                .montaggioReale(dto.getMontaggioReale())
                .montaggioFatturabile(dto.getMontaggioFatturabile())
                .scatolaturaReale(dto.getScatolaturaReale())
                .scatolaturaFatturabile(dto.getScatolaturaFatturabile())
                .dataRisultato(dataRisultato)
                .prezzo(dto.getPrezzo())
                .quantita(dto.getQuantita() != null ? dto.getQuantita() : BigDecimal.ZERO)
                .build();
    }
}
