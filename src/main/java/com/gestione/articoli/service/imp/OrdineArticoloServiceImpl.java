package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.OrdineArticoloDto;
import com.gestione.articoli.mapper.OrdineArticoloMapper;
import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.repository.OrdineArticoloRepository;
import com.gestione.articoli.service.OrdineArticoloService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdineArticoloServiceImpl implements OrdineArticoloService {

    private final OrdineArticoloRepository repository;

    @Override
    public OrdineArticoloDto createOrdineArticolo(OrdineArticoloDto dto) {
        OrdineArticolo entity = OrdineArticoloMapper.toEntity(dto);
        return OrdineArticoloMapper.toDto(repository.save(entity));
    }

    @Override
    public OrdineArticoloDto updateOrdineArticolo(Long id, OrdineArticoloDto dto) {
        OrdineArticolo entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrdineArticolo non trovato con id " + id));

        entity.setQuantita(dto.getQuantita());
        // ordine e articolo devono essere settati nel service se cambiano
        return OrdineArticoloMapper.toDto(repository.save(entity));
    }

    @Override
    public void deleteOrdineArticolo(Long id) {
        if(!repository.existsById(id)){
            throw new RuntimeException("OrdineArticolo non trovato con id " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public OrdineArticoloDto getOrdineArticoloById(Long id) {
        OrdineArticolo entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrdineArticolo non trovato con id " + id));
        return OrdineArticoloMapper.toDto(entity);
    }
    @Override
    public List<OrdineArticoloDto> getAllOrdineArticoliByOrdineId(Long ordineId) {
        return repository.findByOrdineId(ordineId)
                .stream()
                .map(OrdineArticoloMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdineArticoloDto> getAllOrdineArticoli() {
        return repository.findAll().stream().map(OrdineArticoloMapper::toDto).collect(Collectors.toList());
    }
}
