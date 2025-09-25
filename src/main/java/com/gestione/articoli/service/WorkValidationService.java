package com.gestione.articoli.service;

import org.springframework.stereotype.Service;

import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.model.Articolo;
import com.gestione.articoli.model.Ordine;
import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.repository.ArticoloRepository;
import com.gestione.articoli.repository.OrdineArticoloRepository;
import com.gestione.articoli.repository.OrdineRepository;

@Service
public class WorkValidationService {

    private final OrdineRepository ordineRepository;
    private final OrdineArticoloRepository ordineArticoloRepository;
    private final ArticoloRepository articoloRepository;

    public WorkValidationService(OrdineRepository ordineRepository,
                                 OrdineArticoloRepository ordineArticoloRepository,
                                 ArticoloRepository articoloRepository) {
        this.ordineRepository = ordineRepository;
        this.ordineArticoloRepository = ordineArticoloRepository;
        this.articoloRepository = articoloRepository;
    }

    public WorkEntities validateAndFetchEntities(WorkDto dto) {
        if (dto.getOrdineArticolo() == null 
            || dto.getOrdineArticolo().getArticoloId() == null
            || dto.getOrdineArticolo().getOrdineId() == null) {
            throw new RuntimeException("Devi fornire sia ordine che articolo per creare un Work");
        }

        Ordine ordine = ordineRepository.findById(dto.getOrdineArticolo().getOrdineId())
                .orElseThrow(() -> new RuntimeException("Ordine non trovato"));

        OrdineArticolo oa = ordineArticoloRepository
                .findByOrdineIdAndArticoloId(dto.getOrdineArticolo().getOrdineId(),
                        dto.getOrdineArticolo().getArticoloId())
                .orElseThrow(() -> new RuntimeException("OrdineArticolo non trovato per ordine/articolo"));

        Articolo articolo = articoloRepository.findByIdWithFigli(dto.getArticolo().getId())
                .orElseThrow(() -> new RuntimeException("Articolo non trovato"));

        return new WorkEntities(ordine, oa, articolo);
    }

    public static class WorkEntities {
        public final Ordine ordine;
        public final OrdineArticolo ordineArticolo;
        public final Articolo articolo;

        public WorkEntities(Ordine ordine, OrdineArticolo ordineArticolo, Articolo articolo) {
            this.ordine = ordine;
            this.ordineArticolo = ordineArticolo;
            this.articolo = articolo;
        }
    }
}