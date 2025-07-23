package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.ArticoloDto;
import com.gestione.articoli.mapper.ArticoloMapper;
import com.gestione.articoli.model.Articolo;
import com.gestione.articoli.repository.ArticoloRepository;
import com.gestione.articoli.service.ArticoloService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticoloServiceImpl implements ArticoloService {

    private final ArticoloRepository articoloRepository;

    @Override
    public ArticoloDto save(ArticoloDto articoloDto) {
        Articolo articolo = ArticoloMapper.toEntity(articoloDto);
        Articolo saved = articoloRepository.save(articolo);
        return ArticoloMapper.toDto(saved);
    }

    @Override
    public ArticoloDto findById(Long id) {
        return articoloRepository.findById(id)
                .map(ArticoloMapper::toDto)
                .orElse(null);
    }

    @Override
    public List<ArticoloDto> findAll() {
        return articoloRepository.findAll().stream()
                .map(ArticoloMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        articoloRepository.deleteById(id);
    }
}
