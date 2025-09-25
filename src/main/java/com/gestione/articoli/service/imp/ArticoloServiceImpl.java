package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.ArticoloDto;
import com.gestione.articoli.dto.ArticoloHierarchyDto;
import com.gestione.articoli.mapper.ArticoloHierarchyMapper;
import com.gestione.articoli.mapper.ArticoloMapper;
import com.gestione.articoli.mapper.AziendaMapper;
import com.gestione.articoli.model.Articolo;
import com.gestione.articoli.repository.ArticoloRepository;
import com.gestione.articoli.service.ArticoloService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticoloServiceImpl implements ArticoloService {

	private final ArticoloRepository articoloRepository;

	@Override
	@Transactional
	public ArticoloDto save(ArticoloDto articoloDto) {
		Articolo articolo;

		// 1️ Recupera articolo se update, altrimenti crea nuovo
		if (articoloDto.getId() != null) {
			articolo = articoloRepository.findByIdWithFigliAndPadri(articoloDto.getId())
					.orElseThrow(() -> new RuntimeException("Articolo non trovato: " + articoloDto.getId()));
		} else {
			articolo = new Articolo();
		}

		// 2️ Aggiorna campi base
		articolo.setCodice(articoloDto.getCodice());
		articolo.setCodiceComponente(articoloDto.getCodiceComponente());
		articolo.setDescrizione(articoloDto.getDescrizione());
		articolo.setPrezzoIdeale(articoloDto.getPrezzoIdeale());
		articolo.setAttivoPerProduzione(articoloDto.isAttivoPerProduzione());
		articolo.setDataCreazione(articoloDto.getDataCreazione());
		articolo.setAzienda(articoloDto.getAzienda() != null ? AziendaMapper.toEntity(articoloDto.getAzienda()) : null);

		// 3️ Salva articolo principale per avere l'ID
		final Articolo savedArticolo = articoloRepository.save(articolo);

		// 4️ Gestione figli
		updateFigli(savedArticolo, articoloDto.getArticoliFigliIds());

		// 5️ Gestione padri
		updatePadri(savedArticolo, articoloDto.getArticoliPadriIds());

		return ArticoloMapper.toDto(savedArticolo);
	}

	// Metodo helper per gestire i figli
	private void updateFigli(final Articolo articolo, final Set<Long> figliIdsParam) {
		final Set<Long> nuoviFigliIds = Optional.ofNullable(figliIdsParam).orElse(Collections.emptySet());

		final Set<Articolo> figliCorrenti = articolo.getArticoliFigli() != null
				? new HashSet<>(articolo.getArticoliFigli())
				: new HashSet<>();

		final Set<Long> tuttiFigliId = new HashSet<>(nuoviFigliIds);
		figliCorrenti.forEach(f -> tuttiFigliId.add(f.getId()));

		final List<Articolo> articoliCoinvolti = articoloRepository.findAllById(tuttiFigliId);
		final Map<Long, Articolo> articoliMap = articoliCoinvolti.stream()
				.collect(Collectors.toMap(Articolo::getId, a -> a));

		final Set<Articolo> figliDaAggiungere = nuoviFigliIds.stream().map(articoliMap::get)
				.filter(f -> !figliCorrenti.contains(f)).collect(Collectors.toSet());

		final Set<Articolo> figliDaRimuovere = figliCorrenti.stream().filter(f -> !nuoviFigliIds.contains(f.getId()))
				.collect(Collectors.toSet());

		figliDaAggiungere.forEach(f -> f.getArticoliPadri().add(articolo));
		figliDaRimuovere.forEach(f -> f.getArticoliPadri().remove(articolo));

		articoloRepository.saveAll(concatSets(figliDaAggiungere, figliDaRimuovere));

		articolo.getArticoliFigli().clear();
		articolo.getArticoliFigli().addAll(articoliMap.entrySet().stream()
				.filter(e -> nuoviFigliIds.contains(e.getKey())).map(Map.Entry::getValue).collect(Collectors.toSet()));
	}

	// Metodo helper per gestire i padri
	private void updatePadri(final Articolo articolo, final Set<Long> padriIdsParam) {
		final Set<Long> nuoviPadriIds = Optional.ofNullable(padriIdsParam).orElse(Collections.emptySet());

		final Set<Articolo> padriCorrenti = articolo.getArticoliPadri() != null
				? new HashSet<>(articolo.getArticoliPadri())
				: new HashSet<>();

		final Set<Long> tuttiPadriId = new HashSet<>(nuoviPadriIds);
		padriCorrenti.forEach(p -> tuttiPadriId.add(p.getId()));

		final List<Articolo> articoliCoinvolti = articoloRepository.findAllById(tuttiPadriId);
		final Map<Long, Articolo> padriMap = articoliCoinvolti.stream()
				.collect(Collectors.toMap(Articolo::getId, a -> a));

		final Set<Articolo> padriDaAggiungere = nuoviPadriIds.stream().map(padriMap::get)
				.filter(p -> !padriCorrenti.contains(p)).collect(Collectors.toSet());

		final Set<Articolo> padriDaRimuovere = padriCorrenti.stream().filter(p -> !nuoviPadriIds.contains(p.getId()))
				.collect(Collectors.toSet());

		padriDaAggiungere.forEach(p -> p.getArticoliFigli().add(articolo));
		padriDaRimuovere.forEach(p -> p.getArticoliFigli().remove(articolo));

		articoloRepository.saveAll(concatSets(padriDaAggiungere, padriDaRimuovere));

		articolo.getArticoliPadri().clear();
		articolo.getArticoliPadri().addAll(padriMap.entrySet().stream().filter(e -> nuoviPadriIds.contains(e.getKey()))
				.map(Map.Entry::getValue).collect(Collectors.toSet()));
	}

	@Override
	@Transactional(readOnly = true)
	public ArticoloDto findById(Long id) {
		Articolo articolo = articoloRepository.findByIdWithFigli(id)
				.orElseThrow(() -> new RuntimeException("Articolo non trovato"));
		return ArticoloMapper.toDto(articolo);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ArticoloDto> findAllParents() {
		return articoloRepository.findAllParents().stream().map(ArticoloMapper::toDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ArticoloDto> findAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("dataCreazione").descending());
		return articoloRepository.findAllByOrderByDataCreazioneDesc(pageable).map(ArticoloMapper::toDto);
	}

	@Override
	public void deleteById(Long id) {
		articoloRepository.deleteById(id);
	}

	@Override
	public Optional<Articolo> findEntityById(Long id) {
		return articoloRepository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ArticoloDto> findAllNoPagination() {
		return articoloRepository.findAllByOrderByDataCreazioneDesc().stream().map(ArticoloMapper::toDto)
				.collect(Collectors.toList());
	}
	@Override
	@Transactional(readOnly = true)
	public ArticoloHierarchyDto getGerarchia(Long id) {
		Articolo articolo = articoloRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Articolo non trovato: " + id));
		return ArticoloHierarchyMapper.toHierarchyDto(articolo, new HashSet<>());
	}

	// Utility per unire due set
	private <T> Set<T> concatSets(final Set<T> a, final Set<T> b) {
		final Set<T> result = new HashSet<>(a);
		result.addAll(b);
		return result;
	}
}
