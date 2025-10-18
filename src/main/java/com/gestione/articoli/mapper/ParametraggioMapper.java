package com.gestione.articoli.mapper;


import com.gestione.articoli.dto.ParametraggioDTO;
import com.gestione.articoli.model.Parametraggio;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ParametraggioMapper {

    public ParametraggioDTO toDTO(Parametraggio entity) {
        if (entity == null) return null;
        return ParametraggioDTO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .categoria(entity.getCategoria())
                .tipoValore(entity.getTipoValore())
                .valoreNumerico(entity.getValoreNumerico())
                .valoreTestuale(entity.getValoreTestuale())
                .descrizione(entity.getDescrizione())
                .attivo(entity.isAttivo())
                .dataUltimaModifica(entity.getDataUltimaModifica())
                .build();
    }

    public Parametraggio toEntity(ParametraggioDTO dto) {
        if (dto == null) return null;
        return Parametraggio.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .categoria(dto.getCategoria())
                .tipoValore(dto.getTipoValore())
                .valoreNumerico(dto.getValoreNumerico())
                .valoreTestuale(dto.getValoreTestuale())
                .descrizione(dto.getDescrizione())
                .attivo(dto.isAttivo())
                .dataUltimaModifica(dto.getDataUltimaModifica())
                .build();
    }
}
