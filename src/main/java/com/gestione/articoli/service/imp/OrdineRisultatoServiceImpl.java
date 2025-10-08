package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.OrdineRisultatoDto;
import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.mapper.OrdineRisultatoMapper;
import com.gestione.articoli.model.Articolo;
import com.gestione.articoli.model.Ordine;
import com.gestione.articoli.model.OrdineRisultato;
import com.gestione.articoli.repository.ArticoloRepository;
import com.gestione.articoli.repository.OrdineRepository;
import com.gestione.articoli.repository.OrdineRisultatoRepository;
import com.gestione.articoli.service.OrdineRisultatoService;
import com.gestione.articoli.service.WorkService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdineRisultatoServiceImpl implements OrdineRisultatoService {

	private final OrdineRisultatoRepository repository;
	private final WorkService workService;
	private final OrdineRepository ordineRepository;
	private final ArticoloRepository articoloRepository;

	@Override
	public OrdineRisultatoDto save(OrdineRisultatoDto dto) {
		OrdineRisultato entity = OrdineRisultatoMapper.toEntity(dto);
		entity = repository.save(entity);
		return OrdineRisultatoMapper.toDto(entity);
	}

	@Override
	public OrdineRisultatoDto getById(Long id) {
		return repository.findById(id).map(OrdineRisultatoMapper::toDto).orElse(null);
	}

	@Override
	public List<OrdineRisultatoDto> getAll() {
		return repository.findAll().stream().map(OrdineRisultatoMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public List<OrdineRisultatoDto> getByOrdineId(Long ordineId) {
		return repository.findByOrdineId(ordineId).stream().map(OrdineRisultatoMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Override
	public List<OrdineRisultato> generaRisultatiDaWorks(Long ordineId, BigDecimal prezzo) {
		// 🔹 1. Recupera l’ordine
		Ordine ordine = ordineRepository.findById(ordineId)
				.orElseThrow(() -> new EntityNotFoundException("Ordine non trovato con id: " + ordineId));

		// 🔹 2. Recupera tutti i lavori manuali con totalMinutes
		List<WorkDto> works = workService.getManualWorksWithTotalMinutesByOrder(ordineId);

		// 🔹 3. Raggruppa per articolo
		Map<Long, List<WorkDto>> lavoriPerArticolo = works.stream().filter(w -> w.getArticolo() != null)
				.collect(Collectors.groupingBy(w -> w.getArticolo().getId()));

		List<OrdineRisultato> risultati = new ArrayList<>();

		for (Map.Entry<Long, List<WorkDto>> entry : lavoriPerArticolo.entrySet()) {
			Long articoloId = entry.getKey();
			List<WorkDto> lavori = entry.getValue();

			Articolo articolo = articoloRepository.findById(articoloId)
					.orElseThrow(() -> new EntityNotFoundException("Articolo non trovato: " + articoloId));

			OrdineRisultato risultato = OrdineRisultato.createEmpty(ordine, articolo, prezzo);


			// 🔹 4. Calcolo minuti reali e fatturabili per ogni attività
			for (WorkDto w : lavori) {
				BigDecimal durataReale = w.getTotalMinutes() != null ? w.getTotalMinutes() : BigDecimal.ZERO;

				int operatorCount = 0;
				if (w.getOperator() != null)
					operatorCount++;
				if (w.getOperator2() != null)
					operatorCount++;
				if (w.getOperator3() != null)
					operatorCount++;
				if (operatorCount == 0)
					operatorCount = 1;

				BigDecimal durataFatturabile = durataReale.multiply(BigDecimal.valueOf(operatorCount));

				switch (w.getActivity()) {
				case "MOLATURA" -> {
					risultato.setMolaturaReale(risultato.getMolaturaReale().add(durataReale));
					risultato.setMolaturaFatturabile(risultato.getMolaturaFatturabile().add(durataFatturabile));
				}
				case "LUCIDATURA" -> {
					risultato.setLucidaturaReale(risultato.getLucidaturaReale().add(durataReale));
					risultato.setLucidaturaFatturabile(risultato.getLucidaturaFatturabile().add(durataFatturabile));
				}
				case "SALDATURA" -> {
					risultato.setSaldaturaReale(risultato.getSaldaturaReale().add(durataReale));
					risultato.setSaldaturaFatturabile(risultato.getSaldaturaFatturabile().add(durataFatturabile));
				}
				case "FORATURA" -> {
					risultato.setForaturaReale(risultato.getForaturaReale().add(durataReale));
					risultato.setForaturaFatturabile(risultato.getForaturaFatturabile().add(durataFatturabile));
				}
				case "FILETTATURA" -> {
					risultato.setFilettaturaReale(risultato.getFilettaturaReale().add(durataReale));
					risultato.setFilettaturaFatturabile(risultato.getFilettaturaFatturabile().add(durataFatturabile));
				}
				case "MONTAGGIO" -> {
					risultato.setMontaggioReale(risultato.getMontaggioReale().add(durataReale));
					risultato.setMontaggioFatturabile(risultato.getMontaggioFatturabile().add(durataFatturabile));
				}
				case "SCATOLATURA" -> {
					risultato.setScatolaturaReale(risultato.getScatolaturaReale().add(durataReale));
					risultato.setScatolaturaFatturabile(risultato.getScatolaturaFatturabile().add(durataFatturabile));
				}
				default -> {
					/* ignora altre attività */ }
				}

				// Quantità totale
				risultato.setQuantita(risultato.getQuantita().add(BigDecimal.valueOf(w.getQuantita())));
			}

			risultati.add(risultato);
		}

		// 🔹 5. Salva i risultati
		return repository.saveAll(risultati);
	}

}
