package com.gestione.articoli.service;

import com.gestione.articoli.dto.OrdineRisultatoDto;
import com.gestione.articoli.dto.StatisticsDto;
import com.gestione.articoli.model.Articolo;
import com.gestione.articoli.model.Ordine;
import com.gestione.articoli.model.OrdineRisultato;
import com.gestione.articoli.repository.OrdineRisultatoRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

	private final OrdineRisultatoRepository repository;

	public StatisticsService(OrdineRisultatoRepository repository) {
		this.repository = repository;
	}

	public StatisticsDto calculateStatistics(LocalDateTime start, LocalDateTime end, Long aziendaId) {

		// Normalizza start a inizio giorno
		start = start.with(LocalTime.MIN); // 00:00:00

		// Normalizza end a fine giorno
		LocalDateTime todayEnd = LocalDateTime.now().with(LocalTime.MAX); // oggi 23:59:59.999
		if (end.isAfter(todayEnd)) {
			end = todayEnd;
		} else {
			end = end.with(LocalTime.MAX); // fine del giorno selezionato
		}

		// Assicurati che start non sia dopo end
		if (start.isAfter(end)) {
			start = end.minusDays(1).with(LocalTime.MIN);
		}
		List<OrdineRisultato> results = repository.findByOrdineDataRangeAndAzienda(start, end, aziendaId);

		if (results.isEmpty()) {
			return StatisticsDto.builder().totaleOrdini(0L).totaleOre(BigDecimal.ZERO).totaleEuro(BigDecimal.ZERO)
					.mediaArticoliOra(BigDecimal.ZERO).build();
		}

		BigDecimal totaleOre = BigDecimal.ZERO;
		BigDecimal totaleEuro = BigDecimal.ZERO;
		BigDecimal totaleArticoli = BigDecimal.ZERO;
		// Ottieni il numero di ordini distinti
		long totaleOrdini = results.stream().map(r -> r.getOrdine().getId()) // prendi solo l'id dell'ordine
				.distinct() // mantieni solo i valori distinti
				.count(); // conta quanti ordini distinti ci sono
		for (OrdineRisultato r : results) {
			// Somma dei minuti fatturabili usando getter pubblici
			BigDecimal totaleMinutiFatturabili = r.getMolaturaFatturabile().add(r.getLucidaturaFatturabile())
					.add(r.getSaldaturaFatturabile()).add(r.getForaturaFatturabile()).add(r.getFilettaturaFatturabile())
					.add(r.getMontaggioFatturabile()).add(r.getScatolaturaFatturabile());

			// Conversione in ore per questo record
			BigDecimal oreRecord = totaleMinutiFatturabili.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

			// Somma al totale
			totaleOre = totaleOre.add(oreRecord);

			// Calcolo euro per questo record
			BigDecimal euroRecord = r.getPrezzo().multiply(oreRecord);
			totaleEuro = totaleEuro.add(euroRecord);

			// Somma articoli
			totaleArticoli = totaleArticoli.add(r.getQuantita());
		}

		BigDecimal mediaArticoliOra = totaleOre.compareTo(BigDecimal.ZERO) > 0
				? totaleArticoli.divide(totaleOre, 2, RoundingMode.HALF_UP)
				: BigDecimal.ZERO;

		return StatisticsDto.builder().totaleOrdini(totaleOrdini).totaleOre(totaleOre).totaleEuro(totaleEuro)
				.mediaArticoliOra(mediaArticoliOra).build();
	}

	@Transactional
	public Map<Long, List<OrdineRisultatoDto>> getOrdiniDettaglio(LocalDateTime start, LocalDateTime end,
			Long aziendaId) {
		// prendi tutti i risultati grezzi
		// Normalizza start a inizio giorno
		start = start.with(LocalTime.MIN); // 00:00:00

		// Normalizza end a fine giorno
		LocalDateTime todayEnd = LocalDateTime.now().with(LocalTime.MAX); // oggi 23:59:59.999
		if (end.isAfter(todayEnd)) {
			end = todayEnd;
		} else {
			end = end.with(LocalTime.MAX); // fine del giorno selezionato
		}

		// Assicurati che start non sia dopo end
		if (start.isAfter(end)) {
			start = end.minusDays(1).with(LocalTime.MIN);
		}
		List<OrdineRisultato> risultati = repository.findByOrdineDataRangeAndAzienda(start, end, aziendaId);

		// mappa ogni risultato in un DTO
		List<OrdineRisultatoDto> dtos = risultati.stream().map(r -> {
			OrdineRisultatoDto dto = new OrdineRisultatoDto();

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
			dto.setDataRisultato(r.getDataRisultato());
			dto.setPrezzo(r.getPrezzo());
			dto.setQuantita(r.getQuantita());

			Ordine o = r.getOrdine();
			dto.setOrdineId(o.getId());
			dto.setWorkStatus(o.getWorkStatus());
			dto.setDataOrdine(o.getDataOrdine());
			dto.setAziendaNome(o.getAzienda() != null ? o.getAzienda().getNome() : null);

			Articolo a = r.getArticolo();
			dto.setArticoloId(a.getId());
			String codice = a.getCodice();
			if (a.getCodiceComponente() != null && !a.getCodiceComponente().isEmpty()) {
				codice += "/" + a.getCodiceComponente();
			}
			dto.setArticoloCodice(codice);

			return dto;
		}).toList();

		// raggruppa per ordineId
		Map<Long, List<OrdineRisultatoDto>> risultatiPerOrdine = dtos.stream().collect(
				Collectors.groupingBy(OrdineRisultatoDto::getOrdineId, LinkedHashMap::new, Collectors.toList()));

		return risultatiPerOrdine;
	}

}
