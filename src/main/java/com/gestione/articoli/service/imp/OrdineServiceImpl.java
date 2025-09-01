package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.OrdineDto;
import com.gestione.articoli.mapper.OrdineMapper;
import com.gestione.articoli.model.Ordine;
import com.gestione.articoli.repository.OrdineRepository;
import com.gestione.articoli.service.OrdineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrdineServiceImpl implements OrdineService {

    private final OrdineRepository ordineRepository;

    public OrdineServiceImpl(OrdineRepository ordineRepository) {
        this.ordineRepository = ordineRepository;
    }

    @Override
    public OrdineDto createOrdine(OrdineDto dto) {
        Ordine ordine = OrdineMapper.toEntity(dto);
        Ordine saved = ordineRepository.save(ordine);
        return OrdineMapper.toDto(saved);
    }

    @Override
    public List<OrdineDto> getAllOrdini() {
        return ordineRepository.findAll().stream()
                .map(OrdineMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrdineDto getOrdineById(Long id) {
        return ordineRepository.findById(id)
                .map(OrdineMapper::toDto)
                .orElse(null);
    }

    @Override
    public void deleteOrdine(Long id) {
        ordineRepository.deleteById(id);
    }
}
