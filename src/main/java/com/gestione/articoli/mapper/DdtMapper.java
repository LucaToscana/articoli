package com.gestione.articoli.mapper;


import com.gestione.articoli.dto.DdtDto;
import com.gestione.articoli.model.Ddt;
import com.gestione.articoli.model.Ordine;
import org.springframework.stereotype.Component;

@Component
public class DdtMapper {

    public DdtDto toDto(Ddt ddt) {
        return DdtDto.builder()
                .id(ddt.getId())
                .progressivo(ddt.getProgressivo())
                .numeroDocumento(ddt.getNumeroDocumento())
                .dataDocumento(ddt.getDataDocumento())
                .causaleTrasporto(ddt.getCausaleTrasporto())
                .luogoPartenza(ddt.getLuogoPartenza())
                .luogoArrivo(ddt.getLuogoArrivo())
                .ordineId(ddt.getOrdine() != null ? ddt.getOrdine().getId() : null)
                .build();
    }

    public Ddt toEntity(DdtDto dto, Ordine ordine) {
        if (dto == null) {
            return null;
        }
        return Ddt.builder()
                .id(dto.getId())
                .progressivo(dto.getProgressivo())
                .numeroDocumento(dto.getNumeroDocumento())
                .dataDocumento(dto.getDataDocumento())
                .causaleTrasporto(dto.getCausaleTrasporto())
                .luogoPartenza(dto.getLuogoPartenza())
                .luogoArrivo(dto.getLuogoArrivo())
                .ordine(ordine) // lo passo dal service, recuperandolo dal repository
                .build();
    }
}
