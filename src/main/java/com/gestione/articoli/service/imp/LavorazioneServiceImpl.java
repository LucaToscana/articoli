package com.gestione.articoli.service.imp;


import com.gestione.articoli.dto.LavorazioneDto;
import com.gestione.articoli.mapper.LavorazioneMapper;
import com.gestione.articoli.model.Lavorazione;
import com.gestione.articoli.repository.LavorazioneRepository;
import com.gestione.articoli.service.LavorazioneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LavorazioneServiceImpl implements LavorazioneService {

    private final LavorazioneRepository lavorazioneRepository;
    private final LavorazioneMapper lavorazioneMapper;

    @Override
    public List<LavorazioneDto> findAll() {
        return lavorazioneMapper.toDtoList(lavorazioneRepository.findAll());
    }

    @Override
    public LavorazioneDto findById(Long id) {
        Lavorazione lavorazione = lavorazioneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lavorazione non trovata con id: " + id));
        return lavorazioneMapper.toDto(lavorazione);
    }

    @Override
    public LavorazioneDto save(LavorazioneDto lavorazioneDto) {
        if (lavorazioneRepository.existsByNome(lavorazioneDto.getNome())) {
            throw new IllegalArgumentException("Lavorazione già esistente: " + lavorazioneDto.getNome());
        }
        Lavorazione entity = lavorazioneMapper.toEntity(lavorazioneDto);
        return lavorazioneMapper.toDto(lavorazioneRepository.save(entity));
    }

    @Override
    public LavorazioneDto update(Long id, LavorazioneDto lavorazioneDto) {
        Lavorazione existing = lavorazioneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lavorazione non trovata con id: " + id));

        existing.setNome(lavorazioneDto.getNome());
        existing.setDescrizione(lavorazioneDto.getDescrizione());

        return lavorazioneMapper.toDto(lavorazioneRepository.save(existing));
    }

    @Override
    public void deleteById(Long id) {
        lavorazioneRepository.deleteById(id);
    }
}
