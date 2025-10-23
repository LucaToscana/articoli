package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.ArticoloDto;
import com.gestione.articoli.dto.ArticoloHierarchyDto;
import com.gestione.articoli.dto.FastOrderDto;
import com.gestione.articoli.dto.OrderWithWorksDto;
import com.gestione.articoli.dto.OrdineArticoloDto;
import com.gestione.articoli.dto.OrdineArticoloPrezzoDto;
import com.gestione.articoli.dto.OrdineDto;
import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.exception.BusinessException;
import com.gestione.articoli.mapper.ArticoloHierarchyMapper;
import com.gestione.articoli.mapper.ArticoloMapper;
import com.gestione.articoli.mapper.OrdineArticoloMapper;
import com.gestione.articoli.mapper.OrdineMapper;
import com.gestione.articoli.model.Articolo;
import com.gestione.articoli.model.Azienda;
import com.gestione.articoli.model.Ordine;
import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.model.OrdineRisultato;
import com.gestione.articoli.model.WorkActivityType;
import com.gestione.articoli.model.WorkStatus;
import com.gestione.articoli.repository.ArticoloRepository;
import com.gestione.articoli.repository.AziendaRepository;
import com.gestione.articoli.repository.OrdineArticoloRepository;
import com.gestione.articoli.repository.OrdineRepository;
import com.gestione.articoli.repository.OrdineRisultatoRepository;
import com.gestione.articoli.repository.WorkRepository;
import com.gestione.articoli.service.ArticoloService;
import com.gestione.articoli.service.OrdineArticoloService;
import com.gestione.articoli.service.OrdineService;
import com.gestione.articoli.service.WorkService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.convert.DtoInstantiatingConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrdineServiceImpl implements OrdineService {

	private final OrdineRepository ordineRepository;
	private final OrdineArticoloRepository ordineArticoloRepository;
	private final OrdineArticoloService ordineArticoloService;
	private final WorkRepository workRepository;
	private final OrdineRisultatoRepository ordineRisultatoRepository;
	private final OrdineMapper ordineMapper;
	private final AziendaRepository aziendaRepository;
	private final WorkService workService;
	private final ArticoloService articoloService;
	private final ArticoloRepository articoloRepository;

	@Override
	public OrdineDto createOrdine(OrdineDto dto) {
		Ordine ordine = ordineMapper.toEntity(dto);
		Ordine saved = ordineRepository.save(ordine);
		return ordineMapper.toDto(saved);
	}

	public List<OrdineDto> getAllOrdini() {
		return ordineRepository.findAllWithAziendaAndArticoli().stream().map(ordineMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public OrdineDto getOrdineById(Long id) {
		// 🔹 1. Recupera l'entità dal repository
		Optional<Ordine> ordineOpt = ordineRepository.findById(id);

		// 🔹 2. Controlla se è presente
		if (ordineOpt.isEmpty()) {
			return null;
		}

		Ordine ordine = ordineOpt.get();

		// 🔹 3. Mappa a DTO
		OrdineDto ordineDto = ordineMapper.toDto(ordine);
		// 🔹 4. Controlla se ha articoli
		if (ordineDto.getArticoli() == null || ordineDto.getArticoli().isEmpty()) {
			List<OrdineArticoloDto> articoli = new ArrayList<>();
			articoli = ordineArticoloService.getAllOrdineArticoliByOrdineId(ordine.getId())	;				
			Set<OrdineArticoloDto> set = articoli.stream().collect(Collectors.toSet());
			ordineDto.setArticoli(set);
		}
		// 🔹 5. Ritorna il DTO finale
		return ordineDto;
	}

	@Override
	public OrdineDto updateOrdine(Long id, OrdineDto dto) {
		Logger logger = LoggerFactory.getLogger(getClass());
		if (dto != null && dto.getWorkStatus() != null && (WorkStatus.COMPLETED.equals(dto.getWorkStatus())
				|| WorkStatus.PAUSED.equals(dto.getWorkStatus()))) {

			List<WorkDto> worksNotCompleted = workService
					.getNotCompletedManualWorksExcludedActivitiesByOrderWithAllStatus(id);

			if (!worksNotCompleted.isEmpty()) {
				throw new BusinessException(
						"Impossibile modificare lo stato dell'ordine: ci sono ancora lavorazioni non concluse. ");
			}
		}
		// Recupera l'ordine dal database
		Ordine ordine = ordineRepository.findById(id).orElseThrow(() -> {
			logger.error("Ordine non trovato con id {}", id);
			return new RuntimeException("Ordine non trovato con id " + id);
		});
		// --- Aggiorna campi principali ---
		ordine.setDataOrdine(dto.getDataOrdine());
		ordine.setNomeDocumento(dto.getNomeDocumento());
		ordine.setHasDdt(dto.isHasDdt());
		logger.info("Campi principali aggiornati: dataOrdine={}, nomeDocumento={}, hasDdt={}", dto.getDataOrdine(),
				dto.getNomeDocumento(), dto.isHasDdt());
		if (dto.getWorkStatus() != null) {
			ordine.setWorkStatus(dto.getWorkStatus());
			logger.info("Stato lavoro aggiornato: {}", dto.getWorkStatus());
		}
		// --- Aggiorna azienda ---
		if (dto.getAziendaId() != null) {
			Azienda azienda = aziendaRepository.findById(dto.getAziendaId()).orElseThrow(() -> {
				logger.error("Azienda non trovata con id {}", dto.getAziendaId());
				return new RuntimeException("Azienda non trovata: " + dto.getAziendaId());
			});
			ordine.setAzienda(azienda);
			logger.info("Azienda aggiornata: {}", azienda.getNome());
		} else {
			logger.info("Nessuna modifica all'azienda");
		}

		// --- Aggiorna quantità articoli ---
		if (dto.getArticoli() != null && !dto.getArticoli().isEmpty()) {
			for (OrdineArticoloDto articoloDto : dto.getArticoli()) {
				OrdineArticolo ordineArticolo = ordine.getArticoli().stream()
						.filter(a -> a.getId().equals(articoloDto.getId())).findFirst()
						.orElseThrow(() -> new RuntimeException("Articolo ordine non trovato: " + articoloDto.getId()));

				ordineArticolo.setQuantita(articoloDto.getQuantita());
			}
		}

		// Salva l'ordine aggiornato
		ordineRepository.save(ordine);

		if (WorkStatus.COMPLETED.equals(dto.getWorkStatus())) {
			workService.cleanCompletedOrderWorks(id);
		}

		logger.info("Ordine salvato con successo: {}", ordine.getId());

		// Converte in DTO e restituisce
		OrdineDto resultDto = ordineMapper.toDto(ordine);
		logger.info("OrdineDto restituito: {}", resultDto);

		return resultDto;
	}

	@Override
	@Transactional
	public void deleteOrdine(Long id) {
		Ordine ordine = ordineRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Ordine non trovato con ID: " + id));

		// 🔹 Elimina prima tutte le entità figlie collegate
		ordineRisultatoRepository.deleteByOrdineId(id);
		workRepository.deleteByOrderArticle_Ordine_Id(id);
		ordineArticoloRepository.deleteByOrdineId(id);

		// 🔹 Poi elimina l’ordine
		ordineRepository.deleteById(ordine.getId());
	}

	@Override
	public List<ArticoloHierarchyDto> getGerarchiaArticoliByOrdineId(Long ordineId) {
		Ordine ordine = ordineRepository.findById(ordineId)
				.orElseThrow(() -> new RuntimeException("Ordine non trovato"));

		return ordine.getArticoli().stream()
				// Ordinamento stabile per ID
				.sorted(Comparator.comparing(OrdineArticolo::getId)).map(oa -> {
					ArticoloHierarchyDto dto = ArticoloHierarchyMapper.toHierarchyDto(oa.getArticolo());
					dto.setArticoloOrdine(oa.getId());
					return dto;
				}).collect(Collectors.toList());
	}

	public OrderWithWorksDto getOrderWithWorks(Long ordineId) {
		Ordine ordine = ordineRepository.findById(ordineId)
				.orElseThrow(() -> new RuntimeException("Ordine non trovato"));

		List<OrderWithWorksDto.OrderArticleWithWorks> articoli = ordine.getArticoli().stream()
				.map(oa -> OrderWithWorksDto.OrderArticleWithWorks.builder().id(oa.getId())
						.articoloId(oa.getArticolo().getId()).codice(oa.getArticolo().getCodice())
						.descrizione(oa.getArticolo().getDescrizione()).quantita(oa.getQuantita())
						.works(workService.getWorksByOrderArticleId(oa.getId())) // recupero WorkDto dal service
						.build())
				.collect(Collectors.toList());

		return OrderWithWorksDto.builder().id(ordine.getId()).dataOrdine(ordine.getDataOrdine())
				.hasDdt(ordine.isHasDdt()).nomeDocumento(ordine.getNomeDocumento())
				.workStatus(ordine.getWorkStatus().name()).aziendaId(ordine.getAzienda().getId())
				.aziendaNome(ordine.getAzienda().getNome()).articoli(articoli).build();
	}

	@Override
	@Transactional
	public ArticoloDto createFastOrder(FastOrderDto dto) {
		if (dto == null) {
			throw new RuntimeException("FastOrderDto non può essere nullo");
		}
		ArticoloDto articoloDto = dto.getArticolo();
		Articolo savedArticoloEntity = new Articolo();
		if (dto.getArticolo() != null && dto.getArticolo().getId() != null && dto.isFromArticle()) {
			savedArticoloEntity = articoloRepository.findById(dto.getArticolo().getId()).orElse(new Articolo()); // oppure
																													// //
																													// lanci//
																													// un'eccezione
		} else {
			// 1️⃣ Salva articolo tramite servizio e ottieni l'entity
			// imposta visibilità e quantità
			articoloDto.setAttivoPerProduzione(true);
			articoloDto.setDescrizione(dto.getOrdine().getNomeDocumento());
			// Questo metodo deve ritornare l'entity salvata
			savedArticoloEntity = articoloService.saveAndGetEntity(articoloDto);
		}

		// 2️⃣ Crea ordine con entità Azienda
		Ordine ordine = Ordine.builder().workStatus(WorkStatus.IN_PROGRESS).azienda(savedArticoloEntity.getAzienda()) // entity
																														// corretta
				.nomeDocumento(dto.getOrdine() != null ? dto.getOrdine().getNomeDocumento() : "")
				.hasDdt(dto.getOrdine() != null && dto.getOrdine().isHasDdt()).build();

		ordine = ordineRepository.save(ordine);

		// 3️⃣ Collega articolo all'ordine
		OrdineArticolo ordineArticolo = OrdineArticolo.builder().ordine(ordine).articolo(savedArticoloEntity) // entity
																												// corretta
				.quantita(dto.getQuantita() != null && dto.getQuantita() > 0 ? dto.getQuantita() : 1).build();

		OrdineArticolo ordineArticoloWork = ordineArticoloRepository.save(ordineArticolo);
		ArticoloDto newArticle = ArticoloMapper.toDto(savedArticoloEntity);

		if (dto.isImmediatelyVisible()) {
			LocalDateTime start = ZonedDateTime.now(ZoneId.of("Europe/Rome")).toLocalDateTime();

			WorkDto workDisponibilita = new WorkDto();
			workDisponibilita.setArticolo(newArticle);
			workDisponibilita.setOrdine(ordineMapper.toDto(ordine));
			workDisponibilita.setStatus(WorkStatus.IN_PROGRESS.name());
			workDisponibilita.setActivity(WorkActivityType.DISPONIBILITA_LAVORAZIONE.name());
			workDisponibilita.setOrderArticleId(ordineArticoloWork.getId());
			workDisponibilita.setOrdineArticolo(OrdineArticoloMapper.toDto(ordineArticoloWork));
			workDisponibilita.setStartTime(start);
			workDisponibilita.setOriginalStartTime(start);
			workService.createWork(workDisponibilita);

			WorkDto workLotto = new WorkDto();
			workLotto.setArticolo(newArticle);
			workLotto.setOrdine(ordineMapper.toDto(ordine));
			workLotto.setStatus(WorkStatus.IN_PROGRESS.name());
			workLotto.setActivity(WorkActivityType.DISPONIBILITA_LOTTO.name());
			workLotto.setOrderArticleId(ordineArticoloWork.getId());
			workLotto.setQuantita(dto.getQuantita());
			workLotto.setOrdineArticolo(OrdineArticoloMapper.toDto(ordineArticoloWork));
			workLotto.setStartTime(start);
			workLotto.setOriginalStartTime(start);
			workService.createWork(workLotto);
		}
		return newArticle;
	}

	@Override
	public List<OrdineArticoloPrezzoDto> getPrezziByOrdine(Long ordineId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public void aggiornaPrezziCreaDatiFattura(List<OrdineArticoloPrezzoDto> prezziDtoList) {
		if (prezziDtoList == null || prezziDtoList.isEmpty()) {
			throw new RuntimeException("La lista dei prezzi è vuota");
		}

		Long idOrdine = (prezziDtoList != null && !prezziDtoList.isEmpty()) ? prezziDtoList.get(0).getOrdineId() : null;

		if (idOrdine == null) {
			throw new RuntimeException("Lista dei prezzi vuota o ordineId mancante.");
		}

		// 🔹 Recupera ordine
		Ordine ordine = ordineRepository.findById(idOrdine)
				.orElseThrow(() -> new RuntimeException("Ordine non trovato con id " + idOrdine));

		// 🔹 Recupera dati di base dal risultato ordine
		List<OrdineRisultato> risultati = ordineRisultatoRepository.findByOrdineId(idOrdine);
		if (risultati.isEmpty()) {
			throw new RuntimeException("Nessun risultato ordine trovato per l'ordine " + idOrdine);
		}

		OrdineRisultato risultato = Optional.ofNullable(risultati).filter(list -> !list.isEmpty())
				.map(list -> list.get(0))
				.orElseThrow(() -> new RuntimeException("Nessun risultato trovato per l'ordine richiesto."));
		BigDecimal costoOrario = risultato.getCOSTO_ORARIO_FISSO();
		BigDecimal costoPersonale = risultato.getCOSTO_PERSONALE_ORARIO_MEDIO();
		BigDecimal ivaPercentuale = risultato.getIVA_STANDARD();
		BigDecimal ricaricoBase = risultato.getRICARICO_BASE();

		// 🔹 Imposta dati di fattura sull’ordine
		LocalDateTime dataFattura = ZonedDateTime.now(ZoneId.of("Europe/Rome")).toLocalDateTime();
		String numeroFattura = ordine.generaNumeroFattura();

		ordine.setNumeroFattura(numeroFattura);
		ordine.setCostoOrario(costoOrario);
		ordine.setCostoPersonaleMedio(costoPersonale);
		ordine.setIva(ivaPercentuale);
		ordine.setRicaricoBase(ricaricoBase);
		ordine.setDataFattura(dataFattura);

		// 🔹 Totali iniziali
		BigDecimal totaleNetto = BigDecimal.ZERO;
		BigDecimal totaleIva = BigDecimal.ZERO;
		BigDecimal totaleLordo = BigDecimal.ZERO;

		// 🔹 Calcolo per ogni articolo
		for (OrdineArticolo articolo : ordine.getArticoli()) {

			// Cerca il corrispondente nella lista DTO
			OrdineArticoloPrezzoDto dto = prezziDtoList.stream()
					.filter(p -> p.getArticoloId().equals(articolo.getArticolo().getId())).findFirst().orElse(null);

			if (dto == null)
				continue;

			// Prezzo unitario
			BigDecimal prezzoUnitario = dto.getPrezzoUnitario() != null ? dto.getPrezzoUnitario() : BigDecimal.ZERO;
			articolo.setPrezzo(prezzoUnitario);

			// IVA unitaria = prezzoUnitario * (ivaPercentuale / 100)
			BigDecimal ivaUnitaria = prezzoUnitario.multiply(ivaPercentuale)
					.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
			articolo.setIva(ivaUnitaria);
			// Prezzo lordo unitario = prezzo + iva
			BigDecimal prezzoLordoUnitario = prezzoUnitario.add(ivaUnitaria);
			articolo.setPrezzoLordo(prezzoLordoUnitario);
			// Totale per la quantità
			BigDecimal quantita = BigDecimal.valueOf(articolo.getQuantita());
			BigDecimal totaleArticoloNetto = prezzoUnitario.multiply(quantita);
			BigDecimal totaleArticoloIva = ivaUnitaria.multiply(quantita);
			BigDecimal totaleArticoloLordo = prezzoLordoUnitario.multiply(quantita);

			// Somma ai totali complessivi
			totaleNetto = totaleNetto.add(totaleArticoloNetto);
			totaleIva = totaleIva.add(totaleArticoloIva);
			totaleLordo = totaleLordo.add(totaleArticoloLordo);

			// Salva articolo aggiornato
			ordineArticoloRepository.save(articolo);
		}

		// 🔹 Arrotondamenti finali
		totaleNetto = totaleNetto.setScale(2, RoundingMode.HALF_UP);
		totaleIva = totaleIva.setScale(2, RoundingMode.HALF_UP);
		totaleLordo = totaleLordo.setScale(2, RoundingMode.HALF_UP);

		// 🔹 Imposta totali sull’ordine
		ordine.setTotaleNetto(totaleNetto);
		ordine.setTotaleIva(totaleIva);
		ordine.setTotaleLordo(totaleLordo);

		// 🔹 Salva ordine aggiornato
		ordineRepository.save(ordine);
	}

}
