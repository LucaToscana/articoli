package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.DdtDto;
import com.gestione.articoli.mapper.DdtMapper;
import com.gestione.articoli.model.Ddt;
import com.gestione.articoli.model.Ordine;
import com.gestione.articoli.repository.DdtRepository;
import com.gestione.articoli.repository.OrdineRepository;
import com.gestione.articoli.service.DdtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DdtServiceImpl implements DdtService {

    private final DdtRepository ddtRepository;
    private final OrdineRepository ordineRepository;
    private final DdtMapper ddtMapper;

    @Override
    public DdtDto creaDdt(Long ordineId, String causale, String partenza, String arrivo) {
        Ordine ordine = ordineRepository.findById(ordineId)
                .orElseThrow(() -> new RuntimeException("Ordine non trovato"));

        Long lastProgressivo = ddtRepository.findTopByOrderByProgressivoDesc()
                .map(Ddt::getProgressivo)
                .orElse(0L);

        Long nuovoProgressivo = lastProgressivo + 1;
        String numeroDocumento = formattaNumeroDocumento(nuovoProgressivo);

        Ddt ddt = Ddt.builder()
                .progressivo(nuovoProgressivo)
                .numeroDocumento(numeroDocumento)
                .dataDocumento(LocalDateTime.now())
                .causaleTrasporto(causale)
                .luogoPartenza(partenza)
                .luogoArrivo(arrivo)
                .ordine(ordine)
                .build();

        return ddtMapper.toDto(ddtRepository.save(ddt));
    }

    @Override
    public List<DdtDto> getAll() {
        return ddtRepository.findAll().stream()
                .map(ddtMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DdtDto getById(Long id) {
        Ddt ddt = ddtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DDT non trovato"));
        return ddtMapper.toDto(ddt);
    }

    private String formattaNumeroDocumento(Long progressivo) {
        int anno = LocalDateTime.now().getYear();
        return String.format("%d/%06d-DDT", anno, progressivo);
    }
}
