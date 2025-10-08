package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.AziendaDto;
import com.gestione.articoli.mapper.AziendaMapper;
import com.gestione.articoli.model.Azienda;
import com.gestione.articoli.repository.AziendaRepository;
import com.gestione.articoli.service.AziendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AziendaServiceImpl implements AziendaService {

    private final AziendaRepository aziendaRepository;

    @Override
    public AziendaDto createAzienda(AziendaDto dto) {
        Azienda azienda = AziendaMapper.toEntity(dto);
        Azienda saved = aziendaRepository.save(azienda);
        return AziendaMapper.toDto(saved);
    }

    @Override
    public AziendaDto updateAzienda(Long id, AziendaDto dto) {
        Azienda azienda = aziendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Azienda non trovata con id " + id));

        azienda.setNome(dto.getNome());
        azienda.setPartitaIva(dto.getPartitaIva());

        Azienda updated = aziendaRepository.save(azienda);
        return AziendaMapper.toDto(updated);
    }

    @Override
    public void deleteAzienda(Long id) {
        if (!aziendaRepository.existsById(id)) {
            throw new RuntimeException("Azienda non trovata con id " + id);
        }
        aziendaRepository.deleteById(id);
    }

    @Override
    public AziendaDto getAziendaById(Long id) {
        Azienda azienda = aziendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Azienda non trovata con id " + id));
        return AziendaMapper.toDto(azienda);
    }

    @Override
    public List<AziendaDto> getAllAziende() {
        return aziendaRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Azienda::getNome)) // ordina per nome
                .map(AziendaMapper::toDto)
                .collect(Collectors.toList());
    }
}
