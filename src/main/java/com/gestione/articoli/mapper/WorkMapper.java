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
import com.gestione.articoli.model.User;
import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.dto.WorkSummaryProjection;
import com.gestione.articoli.dto.OrdineDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
                .originalStartTime(entity.getOriginalStartTime())             
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
                .originalStartTime(dto.getOriginalStartTime())             
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


    /**
     * Converte un Work in WorkDto includendo il totale dei minuti.
     *
     * @param entity Work da convertire
     * @param totalMinutes minuti totali da assegnare
     * @return WorkDto con totalMinutes valorizzato
     */
    public static WorkDto toDtoWithTotalMinutes(Work entity, BigDecimal totalMinutes) {
        WorkDto dto = toDto(entity); // riusa il metodo esistente
        dto.setTotalMinutes(totalMinutes); // imposta il campo extra
        return dto;
    }
    public static WorkDto workSummaryProjectionToDto(WorkSummaryProjection proj) {
        // Creo l’oggetto Work e popolo i campi base
        Work work = new Work();
        work.setId(proj.getId());
        work.setOrderArticle(new OrdineArticolo()); // se vuoi puoi popolare solo l'id
        work.getOrderArticle().setId(proj.getOrderArticleId());
        work.setQuantita(proj.getQuantita());
        work.setStatus(WorkStatus.valueOf(proj.getStatus())); // se status è enum
        work.setStartTime(proj.getStartTime());
        work.setOriginalStartTime(proj.getOriginalStartTime());
        work.setEndTime(proj.getEndTime());
        
        // Popolo gli utenti solo con l’ID
        if (proj.getManagerId() != null) {
            User manager = new User();
            manager.setId(proj.getManagerId());
            work.setManager(manager);
        }
        if (proj.getOperatorId() != null) {
            User operator = new User();
            operator.setId(proj.getOperatorId());
            work.setOperator(operator);
        }
        if (proj.getOperator2Id() != null) {
            User operator2 = new User();
            operator2.setId(proj.getOperator2Id());
            work.setOperator2(operator2);
        }
        if (proj.getOperator3Id() != null) {
            User operator3 = new User();
            operator3.setId(proj.getOperator3Id());
            work.setOperator3(operator3);
        }

        // Popolo l’articolo
        if (proj.getArticoloId() != null) {
            Articolo articolo = new Articolo();
            articolo.setId(proj.getArticoloId());
            work.setArticolo(articolo);
        }

        // Enum e campi stringa
        if (proj.getActivity() != null) work.setActivity(WorkActivityType.valueOf(proj.getActivity()));
        if (proj.getSpecifiche() != null) work.setSpecifiche(WorkSpecificType.valueOf(proj.getSpecifiche()));
        if (proj.getGrana() != null) work.setGrana(GranaType.valueOf(proj.getGrana()));
        if (proj.getPastaColore() != null) work.setPastaColore(PastaColoreType.valueOf(proj.getPastaColore()));

        // Ora creo il DTO da Work
        WorkDto workDto = WorkMapper.toDto(work);

        // Imposto totalMinutes dal projection
        workDto.setTotalMinutes(proj.getTotalMinutes());

        return workDto;
    }

}
