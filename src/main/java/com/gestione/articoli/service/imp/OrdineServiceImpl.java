package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.ArticoloHierarchyDto;
import com.gestione.articoli.dto.OrderWithWorksDto;
import com.gestione.articoli.dto.OrdineArticoloDto;
import com.gestione.articoli.dto.OrdineDto;
import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.exception.BusinessException;
import com.gestione.articoli.mapper.ArticoloHierarchyMapper;
import com.gestione.articoli.mapper.OrdineMapper;
import com.gestione.articoli.model.Azienda;
import com.gestione.articoli.model.Ordine;
import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.model.WorkStatus;
import com.gestione.articoli.repository.AziendaRepository;
import com.gestione.articoli.repository.OrdineRepository;
import com.gestione.articoli.service.OrdineService;
import com.gestione.articoli.service.WorkService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrdineServiceImpl implements OrdineService {

    private final OrdineRepository ordineRepository;
    private final OrdineMapper ordineMapper;  
    private final AziendaRepository aziendaRepository;
    private final WorkService workService;


    @Override
    public OrdineDto createOrdine(OrdineDto dto) {
        Ordine ordine = ordineMapper.toEntity(dto);
        Ordine saved = ordineRepository.save(ordine);
        return ordineMapper.toDto(saved);
    }

    public List<OrdineDto> getAllOrdini() {
        return ordineRepository.findAllWithAziendaAndArticoli().stream()
                .map(ordineMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrdineDto getOrdineById(Long id) {
        return ordineRepository.findById(id)
                .map(ordineMapper::toDto)
                .orElse(null);
    }

    @Override
    public OrdineDto updateOrdine(Long id, OrdineDto dto) {
        Logger logger = LoggerFactory.getLogger(getClass());
        if (dto != null && dto.getWorkStatus() != null 
        	    && WorkStatus.COMPLETED.equals(dto.getWorkStatus())) {

        	List<WorkDto> worksNotCompleted = workService
        	        .getNotCompletedManualWorksExcludedActivitiesByOrderWithAllStatus(id);

        	    if (!worksNotCompleted.isEmpty()) {
        	        throw new BusinessException(
        	            "Impossibile completare l'ordine: ci sono ancora lavorazioni non concluse. "
        	        );
        	    }
        }
        logger.info("UpdateOrdine chiamato con id={} e dto={}", id, dto);

        // Recupera l'ordine dal database
        Ordine ordine = ordineRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ordine non trovato con id {}", id);
                    return new RuntimeException("Ordine non trovato con id " + id);
                });
        // --- Aggiorna campi principali ---
        ordine.setDataOrdine(dto.getDataOrdine());
        ordine.setNomeDocumento(dto.getNomeDocumento());
        ordine.setHasDdt(dto.isHasDdt());
        logger.info("Campi principali aggiornati: dataOrdine={}, nomeDocumento={}, hasDdt={}",
                dto.getDataOrdine(), dto.getNomeDocumento(), dto.isHasDdt());
        if (dto.getWorkStatus() != null) {
            ordine.setWorkStatus(dto.getWorkStatus());
            logger.info("Stato lavoro aggiornato: {}", dto.getWorkStatus());
        }
        // --- Aggiorna azienda ---
        if (dto.getAziendaId() != null) {
            Azienda azienda = aziendaRepository.findById(dto.getAziendaId())
                    .orElseThrow(() -> {
                        logger.error("Azienda non trovata con id {}", dto.getAziendaId());
                        return new RuntimeException("Azienda non trovata: " + dto.getAziendaId());
                    });
            ordine.setAzienda(azienda);
            logger.info("Azienda aggiornata: {}", azienda.getNome());
        } else {
            logger.info("Nessuna modifica all'azienda");
        }

        // --- Aggiorna quantità articoli ---
        if (dto.getArticoli() != null && !dto.getArticoli().isEmpty()) {
            for (OrdineArticoloDto articoloDto : dto.getArticoli()) {
                OrdineArticolo ordineArticolo = ordine.getArticoli().stream()
                        .filter(a -> a.getId().equals(articoloDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException(
                                "Articolo ordine non trovato: " + articoloDto.getId()));

                ordineArticolo.setQuantita(articoloDto.getQuantita());
            }
        }


        // Salva l'ordine aggiornato
        ordineRepository.save(ordine);
        logger.info("Ordine salvato con successo: {}", ordine.getId());

        // Converte in DTO e restituisce
        OrdineDto resultDto = ordineMapper.toDto(ordine);
        logger.info("OrdineDto restituito: {}", resultDto);

        return resultDto;
    }

    
    @Override
    public void deleteOrdine(Long id) {
        ordineRepository.deleteById(id);
    }
    @Override
    public List<ArticoloHierarchyDto> getGerarchiaArticoliByOrdineId(Long ordineId) {
        Ordine ordine = ordineRepository.findById(ordineId)
                .orElseThrow(() -> new RuntimeException("Ordine non trovato"));

        return ordine.getArticoli()
                .stream()
                // Ordinamento stabile per ID
                .sorted(Comparator.comparing(OrdineArticolo::getId))
                .map(oa -> {
                    ArticoloHierarchyDto dto = ArticoloHierarchyMapper.toHierarchyDto(oa.getArticolo());
                    dto.setArticoloOrdine(oa.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    public OrderWithWorksDto getOrderWithWorks(Long ordineId) {
        Ordine ordine = ordineRepository.findById(ordineId)
                .orElseThrow(() -> new RuntimeException("Ordine non trovato"));

        List<OrderWithWorksDto.OrderArticleWithWorks> articoli = ordine.getArticoli().stream()
            .map(oa -> OrderWithWorksDto.OrderArticleWithWorks.builder()
                    .id(oa.getId())
                    .articoloId(oa.getArticolo().getId())
                    .codice(oa.getArticolo().getCodice())
                    .descrizione(oa.getArticolo().getDescrizione())
                    .quantita(oa.getQuantita())
                    .works(workService.getWorksByOrderArticleId(oa.getId())) // recupero WorkDto dal service
                    .build()
            ).collect(Collectors.toList());

        return OrderWithWorksDto.builder()
                .id(ordine.getId())
                .dataOrdine(ordine.getDataOrdine())
                .hasDdt(ordine.isHasDdt())
                .nomeDocumento(ordine.getNomeDocumento())
                .workStatus(ordine.getWorkStatus().name())
                .aziendaId(ordine.getAzienda().getId())
                .aziendaNome(ordine.getAzienda().getNome())
                .articoli(articoli)
                .build();
    }
}
