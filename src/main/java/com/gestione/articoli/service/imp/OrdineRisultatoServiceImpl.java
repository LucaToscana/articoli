package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.OrdineArticoloDto;
import com.gestione.articoli.dto.OrdineArticoloPrezzoDto;
import com.gestione.articoli.dto.OrdineRisultatoDto;
import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.mapper.OrdineMapper;
import com.gestione.articoli.mapper.OrdineRisultatoMapper;
import com.gestione.articoli.model.Articolo;
import com.gestione.articoli.model.Ordine;
import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.model.OrdineRisultato;
import com.gestione.articoli.repository.ArticoloRepository;
import com.gestione.articoli.repository.OrdineArticoloRepository;
import com.gestione.articoli.repository.OrdineRepository;
import com.gestione.articoli.repository.OrdineRisultatoRepository;
import com.gestione.articoli.service.OrdineArticoloService;
import com.gestione.articoli.service.OrdineRisultatoService;
import com.gestione.articoli.service.OrdineService;
import com.gestione.articoli.service.WorkService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdineRisultatoServiceImpl implements OrdineRisultatoService {

	private final OrdineRisultatoRepository risultatiRepository;
	private final OrdineService ordineService;
	private final WorkService workService;
	private final OrdineRepository ordineRepository;
	private final ArticoloRepository articoloRepository;
	private final OrdineArticoloService ordineArticoloService;
	private final OrdineArticoloRepository ordineArticoloRepository;

	@Override
	public OrdineRisultatoDto save(OrdineRisultatoDto dto) {
		OrdineRisultato entity = OrdineRisultatoMapper.toEntity(dto);
		entity = risultatiRepository.save(entity);
		return OrdineRisultatoMapper.toDto(entity);
	}

	@Override
	public OrdineRisultatoDto getById(Long id) {
		return risultatiRepository.findById(id).map(OrdineRisultatoMapper::toDto).orElse(null);
	}

	@Override
	@Transactional
	public void deleteByOrdineId(Long ordineId) {
		Ordine ordine = ordineRepository.findById(ordineId).orElseThrow(
				() -> new EntityNotFoundException("Ordine non trovato: " + ordineId));
		ordine.setNumeroFattura(null);
        risultatiRepository.deleteByOrdineId(ordineId);
	}

	@Override
	public List<OrdineRisultatoDto> getAll() {
		return risultatiRepository.findAll().stream().map(OrdineRisultatoMapper::toDto).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public List<OrdineRisultatoDto> getByOrdineId(Long ordineId) {
		return risultatiRepository.findByOrdineIdWithJoin(ordineId).stream().map(r -> {
			OrdineRisultatoDto dto = new OrdineRisultatoDto();
			dto.setRicaricoBase(r.getRICARICO_BASE());
			dto.setCostoOrarioFisso(r.getCOSTO_ORARIO_FISSO());
			dto.setCostoPersonaleOrarioMedio(r.getCOSTO_PERSONALE_ORARIO_MEDIO());
			dto.setIvaStandard(r.getIVA_STANDARD());
			dto.setPrezzoOrarioFisso(r.getPREZZO_ORARIO_FISSO());
			dto.setPrezzoEffettivo(r.getPREZZO_EFFETTIVO());
			// dati OrdineRisultato
			dto.setId(r.getId());
			dto.setMolaturaReale(r.getMolaturaReale());
			dto.setMolaturaFatturabile(r.getMolaturaFatturabile());
			dto.setLucidaturaReale(r.getLucidaturaReale());
			dto.setLucidaturaFatturabile(r.getLucidaturaFatturabile());
			dto.setSaldaturaReale(r.getSaldaturaReale());
			dto.setSaldaturaFatturabile(r.getSaldaturaFatturabile());
			dto.setForaturaReale(r.getForaturaReale());
			dto.setForaturaFatturabile(r.getForaturaFatturabile());
			dto.setFilettaturaReale(r.getFilettaturaReale());
			dto.setFilettaturaFatturabile(r.getFilettaturaFatturabile());
			dto.setMontaggioReale(r.getMontaggioReale());
			dto.setMontaggioFatturabile(r.getMontaggioFatturabile());
			dto.setScatolaturaReale(r.getScatolaturaReale());
			dto.setScatolaturaFatturabile(r.getScatolaturaFatturabile());
			
			dto.setStaccareReale(r.getStaccareReale());
			dto.setStaccareFatturabile(r.getStaccareFatturabile());
			dto.setLavareReale(r.getLavareReale());
			dto.setLavareFatturabile(r.getLavareFatturabile());
			dto.setAnnerireReale(r.getAnnerireReale());
			dto.setAnnerireFatturabile(r.getAnnerireFatturabile());
			dto.setSatinareReale(r.getSatinareReale());
			dto.setSatinareFatturabile(r.getSatinareFatturabile());
			
			dto.setDataRisultato(r.getDataRisultato());
			//dto.setPrezzo(r.getPrezzo());
			dto.setQuantita(r.getQuantita());

			// dati Ordine
			Ordine o = r.getOrdine();
			dto.setOrdineId(o.getId());
			dto.setWorkStatus(o.getWorkStatus());
			dto.setDataOrdine(o.getDataOrdine());
			dto.setAziendaNome(o.getAzienda() != null ? o.getAzienda().getNome() : null);

			// dati Articolo
			Articolo a = r.getArticolo();
			dto.setArticoloId(a.getId());
			String codice = a.getCodice();
			if (a.getCodiceComponente() != null && !a.getCodiceComponente().isEmpty()) {
				codice += "/" + a.getCodiceComponente();
			}
			dto.setArticoloCodice(codice);

			return dto;
		}).collect(Collectors.toList());
	}

	@Transactional
	@Override
	public void delete(Long id) {
		risultatiRepository.deleteById(id);
	}

	@Transactional
	@Override
	public List<OrdineRisultato> generaRisultatiDaWorks(Long ordineId, OrdineRisultatoDto parametriCalcoloDto) {
		// 🔹 1. Recupera l’ordine
		Ordine ordine = ordineRepository.findById(ordineId)
				.orElseThrow(() -> new EntityNotFoundException("Ordine non trovato con id: " + ordineId));

		// 🔹 2. Elimina eventuali risultati precedenti per questo ordine
		risultatiRepository.deleteByOrdineId(ordineId);

		// 🔹 3. Recupera tutti i lavori manuali con totalMinutes
		List<WorkDto> works = workService.getManualWorksWithTotalMinutesByOrder(ordineId);

		// 🔹 4. Recupera tutti gli articoli dell’ordine
		List<OrdineArticoloDto> articoliOrdine = ordineArticoloService.getAllOrdineArticoliByOrdineId(ordineId);

		// 🔹 5. Raggruppa lavori per OrdineArticolo.id
		Map<Long, List<WorkDto>> lavoriPerOrdineArticolo = works.stream()
				.filter(w -> w.getOrdineArticolo() != null && w.getOrdineArticolo().getId() != null)
				.collect(Collectors.groupingBy(w -> w.getOrdineArticolo().getId()));

		List<OrdineRisultato> risultati = new ArrayList<>();
        BigDecimal totaleMinutiReali = BigDecimal.ZERO;

		// 🔹 6. Genera risultati per ogni OrdineArticolo
		for (OrdineArticoloDto ordineArticolo : articoliOrdine) {
			Long ordineArticoloId = ordineArticolo.getId();
			List<WorkDto> lavori = lavoriPerOrdineArticolo.getOrDefault(ordineArticoloId, Collections.emptyList());

			Articolo articolo = articoloRepository.findById(ordineArticolo.getArticoloId()).orElseThrow(
					() -> new EntityNotFoundException("Articolo non trovato: " + ordineArticolo.getArticoloId()));
		    BigDecimal totaleMinutiRealiArticolo = BigDecimal.ZERO;
		    BigDecimal totaleMinutiFatturabiliArticolo = BigDecimal.ZERO;

			OrdineRisultato risultato = OrdineRisultato.createEmpty(ordine, articolo,
					parametriCalcoloDto.getPrezzoOrarioFisso());

			// Quantità totale dell’articolo nell’ordine
			risultato.setQuantita(BigDecimal.valueOf(ordineArticolo.getQuantita()));

			// 🔹 7. Calcolo minuti reali e fatturabili per ogni attività
			for (WorkDto w : lavori) {
				BigDecimal durataReale = w.getTotalMinutes() != null ? w.getTotalMinutes() : BigDecimal.ZERO;
	            totaleMinutiReali = totaleMinutiReali.add(durataReale);
	            totaleMinutiRealiArticolo = totaleMinutiRealiArticolo.add(durataReale);  // totale per articolo

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
				totaleMinutiFatturabiliArticolo = totaleMinutiFatturabiliArticolo.add(durataFatturabile);
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
				case "STACCARE" -> {
				    risultato.setStaccareReale(risultato.getStaccareReale().add(durataReale));
				    risultato.setStaccareFatturabile(risultato.getStaccareFatturabile().add(durataFatturabile));
				}
				case "LAVARE" -> {
				    risultato.setLavareReale(risultato.getLavareReale().add(durataReale));
				    risultato.setLavareFatturabile(risultato.getLavareFatturabile().add(durataFatturabile));
				}
				case "ANNERIRE" -> {
				    risultato.setAnnerireReale(risultato.getAnnerireReale().add(durataReale));
				    risultato.setAnnerireFatturabile(risultato.getAnnerireFatturabile().add(durataFatturabile));
				}
				case "SATINARE" -> {
				    risultato.setSatinareReale(risultato.getSatinareReale().add(durataReale));
				    risultato.setSatinareFatturabile(risultato.getSatinareFatturabile().add(durataFatturabile));
				}
				default -> {
					/* ignora altre attività */ }
				}
			}
			risultato.setPrezzo(parametriCalcoloDto.getPrezzo());
			risultato.setCOSTO_ORARIO_FISSO(parametriCalcoloDto.getCostoOrarioFisso());
			risultato.setCOSTO_PERSONALE_ORARIO_MEDIO(parametriCalcoloDto.getCostoPersonaleOrarioMedio());
			risultato.setIVA_STANDARD(parametriCalcoloDto.getIvaStandard());
			risultato.setRICARICO_BASE(parametriCalcoloDto.getRicaricoBase());
			risultato.setPREZZO_ORARIO_FISSO(parametriCalcoloDto.getPrezzoOrarioFisso());
			risultato.setPREZZO_EFFETTIVO(parametriCalcoloDto.getPrezzoOrarioFisso());
			risultati.add(risultato);
			
			OrdineArticolo articoloOrdine = ordineArticoloRepository.findById(ordineArticoloId)
				    .orElseThrow(() -> new EntityNotFoundException("OrdineArticolo non trovato con id: " + ordineArticoloId));

				// Imposta totale minuti
				articoloOrdine.setTotaleMinutiLavorazioni(totaleMinutiFatturabiliArticolo);

				// Calcolo: (totaleMinutiRealiArticolo / 60) * prezzoOrarioFisso / quantita
				BigDecimal minutiInOre = totaleMinutiFatturabiliArticolo
				    .divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);

				BigDecimal prezzoOrarioFisso = parametriCalcoloDto.getPrezzoOrarioFisso() != null
				    ? parametriCalcoloDto.getPrezzoOrarioFisso()
				    : BigDecimal.ZERO;

				BigDecimal quantita = BigDecimal.valueOf(articoloOrdine.getQuantita());

				// costo unitario = (minutiInOre * prezzoOrarioFisso) / quantita
				BigDecimal prezzoUnitario = minutiInOre
					    .multiply(prezzoOrarioFisso)
					    .divide(quantita, 4, RoundingMode.HALF_UP); // calcolo originale

					// arrotondamento a 2 decimali
					BigDecimal prezzoArrotondato = prezzoUnitario.setScale(2, RoundingMode.CEILING);

				System.out.println(">>> Prezzo unitario calcolato: " + prezzoUnitario);

				// Se vuoi salvarlo in un campo, puoi farlo così:
				articoloOrdine.setPrezzo(prezzoArrotondato);

			ordineArticoloRepository.save(articoloOrdine);
		}
		// 🔹 9. Salva tutti i risultati nel risultatiRepository
		List<OrdineRisultato> risultatiSalvati = risultatiRepository.saveAll(risultati);
		articoliOrdine = ordineArticoloService.getAllOrdineArticoliByOrdineId(ordineId);
		// 🔹 10. Aggiorna i prezzi unitari degli articoli nell’ordine
		List<OrdineArticoloPrezzoDto> prezziDtoList = articoliOrdine.stream()
		    .map(a -> new OrdineArticoloPrezzoDto(
		        a.getOrdineId(),
		        a.getArticoloId(),
		        a.getPrezzo()
		    ))
		    .collect(Collectors.toList());

		ordineService.aggiornaPrezziCreaDatiFattura(prezziDtoList);
		ordine = ordineRepository.findById(ordineId)
				.orElseThrow(() -> new EntityNotFoundException("Ordine non trovato con id: " + ordineId));
        ordine.setTotaleMinutiLavorazioni(totaleMinutiReali);
        ordineRepository.save(ordine);
		// 🔹 11. Ritorna i risultati salvati
		return risultatiSalvati;
		
	}

}
