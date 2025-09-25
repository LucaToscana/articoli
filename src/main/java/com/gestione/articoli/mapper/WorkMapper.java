package com.gestione.articoli.mapper;

import com.gestione.articoli.model.Articolo;
import com.gestione.articoli.model.GranaType;
import com.gestione.articoli.model.Work;
import com.gestione.articoli.model.WorkActivityType;
import com.gestione.articoli.model.WorkPositionType;
import com.gestione.articoli.model.WorkSpecificType;
import com.gestione.articoli.model.WorkStatus;
import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.model.PastaColoreType;
import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.dto.OrdineDto;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class WorkMapper {

    public static WorkDto toDto(Work entity) {
        if (entity == null) return null;

        var articoloDto = entity.getArticolo() != null
                ? ArticoloMapper.toDto(entity.getArticolo())
                : null;

        OrdineDto ordineDto = null;
        if (entity.getOrderArticle() != null && entity.getOrderArticle().getOrdine() != null) {
            var ordine = entity.getOrderArticle().getOrdine();
            ordineDto = OrdineDto.builder()
                    .id(ordine.getId())
                    .dataOrdine(ordine.getDataOrdine())
                    .aziendaId(ordine.getAzienda() != null ? ordine.getAzienda().getId() : null)
                    .nomeAzienda(ordine.getAzienda() != null ? ordine.getAzienda().getNome() : null)
                    .hasDdt(ordine.isHasDdt())
                    .nomeDocumento(ordine.getNomeDocumento())
                    .workStatus(ordine.getWorkStatus())
                    .articoli(ordine.getArticoli() != null
                            ? ordine.getArticoli().stream()
                                .map(OrdineArticoloMapper::toDto)
                                .collect(Collectors.toSet())
                            : null)
                    .build();
        }

        return WorkDto.builder()
                .id(entity.getId())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .activity(entity.getActivity() != null ? entity.getActivity().name() : null)
                .posizioni(entity.getPosizioni() != null 
					        ? entity.getPosizioni().stream()
					            .map(WorkPositionType::name)
					            .collect(Collectors.toList())
					        : null)                
                .specifiche(entity.getSpecifiche() != null ? entity.getSpecifiche().name() : null)
                .grana(entity.getGrana() != null ? entity.getGrana().name() : null)
                .pastaColore(entity.getPastaColore() != null ? entity.getPastaColore().name() : null)
                .quantita(entity.getQuantita())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .orderArticleId(entity.getOrderArticle() != null ? entity.getOrderArticle().getId() : null)
                .articolo(articoloDto)
                .ordine(ordineDto)
                .ordineArticolo(entity.getOrderArticle() != null ? OrdineArticoloMapper.toDto(entity.getOrderArticle()) : null)
                .manager(UserMapper.toDto(entity.getManager()))
                .operator(UserMapper.toDto(entity.getOperator()))
                .operator2(UserMapper.toDto(entity.getOperator2()))
                .operator3(UserMapper.toDto(entity.getOperator3()))
                .build();
    }

    public static Work toEntity(WorkDto dto) {
        if (dto == null) return null;

        Work.WorkBuilder builder = Work.builder()
                .id(dto.getId())
                .quantita(dto.getQuantita())
                .status(dto.getStatus() != null ? WorkStatus.valueOf(dto.getStatus()) : null)
                .activity(dto.getActivity() != null ? WorkActivityType.valueOf(dto.getActivity()) : null)
                .posizioni(dto.getPosizioni() != null 
                ? dto.getPosizioni().stream()
                      .map(WorkPositionType::valueOf)
                      .collect(Collectors.toList())
                : new ArrayList<>())                
                .specifiche(dto.getSpecifiche() != null ? WorkSpecificType.valueOf(dto.getSpecifiche()) : null)
                .grana(dto.getGrana() != null ? GranaType.valueOf(dto.getGrana()) : null)
                .pastaColore(dto.getPastaColore() != null ? PastaColoreType.valueOf(dto.getPastaColore()) : null)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .manager(UserMapper.toEntity(dto.getManager()))
                .operator(UserMapper.toEntity(dto.getOperator()))
                .operator2(UserMapper.toEntity(dto.getOperator2()))
                .operator3(UserMapper.toEntity(dto.getOperator3()));

        // 🔹 Imposta l’articolo se presente
        if (dto.getArticolo() != null && dto.getArticolo().getId() != null) {
            Articolo articolo = new Articolo();
            articolo.setId(dto.getArticolo().getId());
            builder.articolo(articolo);
        }

        // 🔹 Imposta l’OrdineArticolo se presente
        if (dto.getOrderArticleId() != null) {
            OrdineArticolo oa = new OrdineArticolo();
            oa.setId(dto.getOrderArticleId());
            builder.orderArticle(oa);
        }

        return builder.build();
    }
}
