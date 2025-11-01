package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.StartWorkDto;
import com.gestione.articoli.dto.TotalWorkTimeDto;
import com.gestione.articoli.dto.UserDto;
import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.dto.WorkIdPosizioneProjection;
import com.gestione.articoli.dto.WorkSummaryProjection;
import com.gestione.articoli.mapper.ArticoloMapper;
import com.gestione.articoli.mapper.OrdineArticoloMapper;
import com.gestione.articoli.mapper.WorkMapper;
import com.gestione.articoli.model.*;
import com.gestione.articoli.repository.*;
import com.gestione.articoli.service.UserService;
import com.gestione.articoli.service.WorkService;
import com.gestione.articoli.service.WorkValidationService;
import com.gestione.articoli.utils.WorkUtils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkServiceImpl implements WorkService {

	private final WorkRepository workRepository;
	private final OrdineArticoloRepository ordineArticoloRepository;
	private final OrdineRepository ordineRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final ArticoloRepository articoloRepository;
	private final WorkValidationService validationService;

	/**
	 * Crea un nuovo Work a partire dal DTO fornito.
	 *
	 * @param dto dati del Work da creare
	 * @return WorkDto creato
	 */
	@Override
	public WorkDto createWork(WorkDto dto) {
		User manager = userService.getAuthenticatedUser();
		WorkValidationService.WorkEntities entities = validationService.validateAndFetchEntities(dto);
		Ordine ordine = entities.ordine;
		OrdineArticolo oa = entities.ordineArticolo;
		Articolo articolo = entities.articolo;

		Work work = WorkMapper.toEntity(dto);
		work.setOrderArticle(oa);
		work.setArticolo(articolo);
		work.setManager(manager);

		assignOperatorsToWork(work, dto);
		WorkUtils.applyOptionalEnumsToWork(dto, work);

		if (oa.getWorks() == null || oa.getWorks().isEmpty()) {
			ordine.setWorkStatus(WorkStatus.IN_PROGRESS);
			ordineRepository.save(ordine);
		}

		work.setStatus(dto.getStatus() != null ? WorkStatus.valueOf(dto.getStatus()) : work.getStatus());
		Work newWork = workRepository.save(work);
		newWork.setArticolo(articolo);
		return WorkMapper.toDto(newWork);
	}

	/**
	 * Avvia un lavoro basato sul DTO fornito.
	 *
	 * @param dto dati per avviare il Work
	 * @return WorkDto appena avviato
	 */
	@Transactional
	@Override
	public WorkDto startWork(StartWorkDto dto) {
		// Controlla se l'utente ha il ruolo USER (macchina)
		boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
		boolean isUser = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
		Work operatorWork = new Work();
		User manager = userService.getAuthenticatedUser();
		operatorWork.setManager(manager);
		Work work = workRepository.findById(dto.getWorkId())
				.orElseThrow(() -> new RuntimeException("Lavoro non trovato con ID: " + dto.getWorkId()));

		OrdineArticolo ordineArticolo = ordineArticoloRepository
				.findByOrdineIdAndArticoloId(work.getOrderArticle().getOrdine().getId(),
						work.getOrderArticle().getArticolo().getId())
				.orElseThrow(() -> new RuntimeException("OrdineArticolo non trovato per ordine/articolo"));
		operatorWork.setOrderArticle(ordineArticolo);

		Articolo articolo = articoloRepository.findByIdWithFigli(work.getArticolo().getId())
				.orElseThrow(() -> new RuntimeException("Articolo non trovato"));
		operatorWork.setArticolo(articolo);

		if (dto.getLavorazione() != null && !dto.getLavorazione().trim().isEmpty())
			operatorWork.setActivity(WorkActivityType.valueOf(dto.getLavorazione().toUpperCase()));

		if (dto.getSpecifiche() != null && !dto.getSpecifiche().trim().isEmpty())
			operatorWork.setSpecifiche(WorkSpecificType.valueOf(dto.getSpecifiche()));

		if (dto.getGrana() != null && !dto.getGrana().trim().isEmpty())
			operatorWork.setGrana(GranaType.valueOf(dto.getGrana()));

		if (dto.getPasta() != null && !dto.getPasta().trim().isEmpty())
			operatorWork.setPastaColore(PastaColoreType.valueOf(dto.getPasta()));

		if (dto.getPosizione() != null && !dto.getPosizione().isEmpty()) {
			List<WorkPositionType> posizioni = dto.getPosizione().stream().map(WorkPositionType::valueOf)
					.collect(Collectors.toList());
			operatorWork.setPosizioni(posizioni);
		}

		if (dto.getOperatorIds() != null && !dto.getOperatorIds().isEmpty()) {
			List<User> operators = userRepository.findAllById(dto.getOperatorIds());
			if (operators.size() > 0)
				operatorWork.setOperator(operators.get(0));
			if (operators.size() > 1)
				operatorWork.setOperator2(operators.get(1));
			if (operators.size() > 2)
				operatorWork.setOperator3(operators.get(2));
		}

		if (dto.getQuantita() != null && dto.getQuantita() > 0)
			operatorWork.setQuantita(dto.getQuantita());

		operatorWork
				.setStatus(WorkActivityType.DISPONIBILITA_LOTTO.name().equals(dto.getLavorazione()) ? WorkStatus.PAUSED
						: WorkStatus.IN_PROGRESS);

		if (null != dto.getStartTime() && null != dto.getEndTime()) {

			LocalDateTime start = dto.getStartTime();
			LocalDateTime end = dto.getEndTime();

// Ottieni millisecondi correnti
			// Ottieni il tempo corrente in Italia
			ZonedDateTime nowItaly = ZonedDateTime.now(ZoneId.of("Europe/Rome"));

			// Ottieni i millisecondi dall'inizio del secondo
			int currentMillis = nowItaly.getNano() / 1_000_000;

			// Converti millisecondi in nanosecondi
			int currentNano = currentMillis * 1_000_000;

// Aggiorna startTime e endTime con i millisecondi correnti
			dto.setStartTime(start.withNano(currentNano));
			dto.setEndTime(end.withNano(currentNano));

			operatorWork.setOriginalStartTime(dto.getStartTime());
			operatorWork.setStartTime(dto.getStartTime());
			operatorWork.setEndTime(dto.getEndTime());

		} else {
			LocalDateTime originalStartTime = ZonedDateTime.now(ZoneId.of("Europe/Rome")).toLocalDateTime();
			operatorWork.setOriginalStartTime(originalStartTime);
			operatorWork.setStartTime(originalStartTime);
		}
		if (!WorkActivityType.DISPONIBILITA_LOTTO.name().equals(dto.getLavorazione())
				&& !WorkActivityType.DISPONIBILITA_LAVORAZIONE.name().equals(dto.getLavorazione()) && isAdmin
				&& null!= dto.getPostazioneId() && dto.getPostazioneId() != 1L) {
			operatorWork.setStatus(WorkStatus.PAUSED);
			userRepository.findByUsername("admin").ifPresent(user -> {
				operatorWork.setManager(user);
			});		}
		// Utente standard (postazione 2)
		if (isUser && dto.getPostazioneId() != null && dto.getPostazioneId() == 2) {
			userRepository.findByUsername("user").ifPresent(user -> {
				operatorWork.setManager(user);
			});
		}

		workRepository.save(operatorWork);
		return WorkMapper.toDto(operatorWork);
	}

	/**
	 * Chiude un Work impostando lo status e l'endTime.
	 *
	 * @param workId    ID del Work
	 * @param newStatus nuovo stato da impostare
	 * @return WorkDto aggiornato
	 */
	@Transactional
	@Override
	public WorkDto closeWork(Long workId, WorkStatus newStatus) {
		LocalDateTime endTime = ZonedDateTime.now(ZoneId.of("Europe/Rome")).toLocalDateTime();

		Work work = workRepository.findById(workId)
				.orElseThrow(() -> new RuntimeException("Lavoro non trovato con ID: " + workId));
		work.setStatus(newStatus);
		work.setEndTime(endTime);
		return WorkMapper.toDto(workRepository.save(work));
	}

	/**
	 * Aggiorna i dati di un Work esistente.
	 *
	 * @param id  ID del Work
	 * @param dto DTO con dati aggiornati
	 * @return WorkDto aggiornato
	 */
	@Override
	public WorkDto updateWork(Long id, WorkDto dto) {
		Work work = workRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Work non trovato con id " + id));

		// Aggiorna lo stato principale
		if (dto.getStatus() != null) {
			WorkStatus newStatus = WorkStatus.valueOf(dto.getStatus());
			work.setStatus(newStatus);

			// Se è DISPONIBILITA_LAVORAZIONE aggiorna anche i lavori DISPONIBILITA_LOTTO
			if (work.getActivity() == WorkActivityType.DISPONIBILITA_LAVORAZIONE
					&& (newStatus == WorkStatus.PAUSED || newStatus == WorkStatus.COMPLETED)) {

				List<Work> lottoWorks = workRepository.findByOrderArticleAndActivity(work.getOrderArticle(),
						WorkActivityType.DISPONIBILITA_LOTTO);

				for (Work lottoWork : lottoWorks) {
					lottoWork.setStatus(newStatus);
					workRepository.save(lottoWork);
				}
			}
		}

		work.setStartTime(dto.getStartTime());
		work.setEndTime(dto.getEndTime());

		return WorkMapper.toDto(workRepository.save(work));
	}

	/**
	 * Cancella un Work manuale , cancella anche quallia associati , non usare per altri work.
	 *
	 * @param id ID del Work
	 */
	@Override
	@Transactional
	public void deleteWork(Long id) {
		// Recupera la work principale
		Work work = workRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Work non trovata con id " + id));

		OrdineArticolo ordineArticolo = work.getOrderArticle();
		LocalDateTime originalStartTime = work.getOriginalStartTime();
		WorkActivityType activityType = work.getActivity();

		if (ordineArticolo == null || originalStartTime == null || activityType == null) {
			throw new RuntimeException("Work senza ordine, originalStartTime o activityType non impostato");
		}

		// Recupera tutte le lavorazioni con stesso ordine, stesso originalStartTime e
		// stessa activity
		List<Work> lavoriDaEliminare = workRepository.findByOrderArticleAndOriginalStartTimeAndActivity(ordineArticolo,
				originalStartTime, activityType);

		if (lavoriDaEliminare.isEmpty()) {
			throw new RuntimeException(
					"Nessuna lavorazione trovata con ordine, originalStartTime e activity corrispondenti");
		}

		// Elimina tutte
		workRepository.deleteAll(lavoriDaEliminare);

	}

	@Override
	public List<WorkDto> getStepsByWork(Long id) {
		// Recupera la work principale
		Work work = workRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Work non trovata con id " + id));

		OrdineArticolo ordineArticolo = work.getOrderArticle();
		LocalDateTime originalStartTime = work.getOriginalStartTime();
		WorkActivityType activityType = work.getActivity();

		if (ordineArticolo == null || originalStartTime == null || activityType == null) {
			throw new RuntimeException("Work senza ordine, originalStartTime o activityType non impostato");
		}

		// Recupera tutte le lavorazioni con stesso ordine, stesso originalStartTime e
		// stessa activity
		List<Work> lavoriDaVisualizzare = workRepository
				.findByOrderArticleAndOriginalStartTimeAndActivity(ordineArticolo, originalStartTime, activityType);

		List<WorkDto> lavoriDto = lavoriDaVisualizzare.stream().map(WorkMapper::toDto).toList();
		return lavoriDto;
	}

	@Transactional
	public void cleanCompletedOrderWorks(Long orderId) {
		List<WorkActivityType> activitiesToDelete = List.of(WorkActivityType.DISPONIBILITA_LOTTO,
				WorkActivityType.DISPONIBILITA_LAVORAZIONE);

		// 1️⃣ Recupera i Work da cancellare
		List<Work> worksToDelete = workRepository.findByOrderArticleOrdineIdAndActivityIn(orderId, activitiesToDelete);

		// 2️⃣ Cancella uno ad uno
		for (Work w : worksToDelete) {
			workRepository.delete(w);
		}

		// 3️⃣ Eventuale secondo delete, escludendo alcuni status
		List<WorkStatus> excludedStatuses = List.of(WorkStatus.IN_PROGRESS);
		List<Work> worksToDelete2 = workRepository.findByOrderArticleOrdineIdAndStatusNotIn(orderId, excludedStatuses);
		for (Work w : worksToDelete2) {
			workRepository.delete(w);
		}
	}

	/**
	 * Recupera un Work per ID.
	 *
	 * @param id ID del Work
	 * @return WorkDto corrispondente
	 */
	@Override
	public WorkDto getWorkById(Long id) {
		Work work = workRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Work non trovato con id " + id));
		return WorkMapper.toDto(work);
	}

	/**
	 * Recupera tutti i Work.
	 *
	 * @return lista di WorkDto
	 */
	@Override
	public List<WorkDto> getAllWorks() {
		return workRepository.findAll().stream().map(WorkMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * Recupera i Work di un OrdineArticolo.
	 *
	 * @param orderArticleId ID dell'OrdineArticolo
	 * @return lista di WorkDto
	 */
	@Override
	public List<WorkDto> getWorksByOrderArticle(Long orderArticleId) {
		OrdineArticolo oa = ordineArticoloRepository.findById(orderArticleId)
				.orElseThrow(() -> new RuntimeException("OrdineArticolo non trovato con id " + orderArticleId));
		return workRepository.findByOrderArticle(oa).stream().map(WorkMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * Recupera tutti i Work di un OrdineArticolo ordinati per startTime
	 * decrescente.
	 *
	 * @param orderArticleId ID OrdineArticolo
	 * @return lista di WorkDto
	 */
	@Override
	public List<WorkDto> getWorksByOrderArticleId(Long orderArticleId) {
		return workRepository.findByOrderArticleId(orderArticleId).stream()
				.sorted(Comparator.comparing(Work::getStartTime).reversed()).map(WorkMapper::toDto)
				.collect(Collectors.toList());
	}

	/**
	 * Recupera tutti i lavori di tutti gli articoli di un ordine.
	 *
	 * @param orderId ID dell'ordine
	 * @return lista di WorkDto
	 */
	@Override
	public List<WorkDto> getWorksByOrderId(Long orderId) {
		return ordineArticoloRepository.findByOrdineId(orderId).stream()
				.flatMap(oa -> oa.getWorks().stream().sorted(Comparator.comparing(Work::getStartTime).reversed()))
				.map(WorkMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * Recupera i lavori in corso di disponibilità lavorazione.
	 *
	 * @return lista di WorkDto
	 */
	@Override
	public List<WorkDto> getInProgressAvailabilityWorks() {
	    List<Work> works = workRepository.findAvailabilityWorksInProgressWithOrderInProgress();

	    List<WorkDto> workDtos = works.stream()
	            .map(WorkMapper::toDto)
	            .collect(Collectors.toList());

	    populateWorkInfo(workDtos);

	    return workDtos;
	}
	/**
	 * Recupera i lavori in corso di disponibilità lotto.
	 *
	 * @return lista di WorkDto
	 */
	@Override
	public List<WorkDto> getInProgressLottoWorks() {
	    List<Work> works = workRepository.findInProgressLottoWorks();

	    List<WorkDto> workDtos = works.stream()
	            .map(WorkMapper::toDto)
	            .collect(Collectors.toList());

	    populateWorkInfo(workDtos);

	    return workDtos;
	}

	/**
	 * Recupera i lavori in corso di disponibilità lotto per ordini in_progress .
	 *
	 * @return lista di WorkDto
	 */
	@Override
	public List<WorkDto> getInProgressLottoWorksWithOrderInProgress() {
	    List<Work> works = workRepository.findInProgressLottoWorksWithOrderInProgress();

	    List<WorkDto> workDtos = works.stream()
	            .map(WorkMapper::toDto)
	            .collect(Collectors.toList());

	    populateWorkInfo(workDtos);

	    return workDtos;
	}

	/**
	 * Recupera tutti i lavori lotto.
	 *
	 * @return lista di WorkDto
	 */
	@Override
	public List<WorkDto> getLottoWorks() {
		return workRepository.findLottoWorks().stream().map(WorkMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * Recupera tutti i lavori manuali in corso esclusi alcuni tipi di attività.
	 *
	 * @return lista di WorkDto
	 */
	@Override
	public List<WorkDto> getInProgressManualWorks() {
		// Recupera username dell'utente/macchina dal contesto di sicurezza
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if (username == null || username.isBlank()) {
			throw new RuntimeException("Utente non autenticato");
		}

		// Controlla se l'utente ha il ruolo USER (macchina)
		boolean isUser = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

		// Recupera tutti i lavori manuali in corso, escludendo certe attività
		List<WorkDto> allManualWorks = workRepository
				.findInProgressManualWorksExcludedActivities(List.of(WorkActivityType.DISPONIBILITA_LOTTO.name(),
						WorkActivityType.DISPONIBILITA_LAVORAZIONE.name()))
				.stream().map(WorkMapper::toDto).collect(Collectors.toList());

		// Se è un utente macchina, filtriamo i lavori in base alle attività della
		// macchina
		if (isUser) {
			User machineUser = userRepository.findByUsernameAndMachineUserTrue(username)
					.orElseThrow(() -> new RuntimeException("Macchina non trovata"));

			Set<String> machineActivities = machineUser.getLavorazioni().stream()
					.map(lav -> lav.getNome().toUpperCase()).collect(Collectors.toSet());

			allManualWorks = allManualWorks.stream().filter(work -> machineActivities.contains(work.getActivity()))
					.collect(Collectors.toList());
		}

		// Imposta infoOrdine e infoArticolo su ogni WorkDto senza ulteriori query
		populateWorkInfo(allManualWorks);
		return allManualWorks;
	}
	@Override
	public List<WorkDto> getInProgressManualWorksCreatedByAdmin() {
	    // Recupera username dell'utente/macchina dal contesto di sicurezza
	    String username = SecurityContextHolder.getContext().getAuthentication().getName();
	    if (username == null || username.isBlank()) {
	        throw new RuntimeException("Utente non autenticato");
	    }

	    // Controlla se l'utente ha il ruolo USER (macchina)
	    boolean isUser = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
	            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

	    // Recupera tutti i lavori manuali in corso creati dall’admin (ID = 1), escludendo certe attività
	    List<WorkDto> allManualWorks = workRepository
	            .findInProgressManualWorksByCreatorIdExcludedActivities(
	                    1L, // creator_id dell’admin
	                    List.of(
	                            WorkActivityType.DISPONIBILITA_LOTTO.name(),
	                            WorkActivityType.DISPONIBILITA_LAVORAZIONE.name()
	                    )
	            )
	            .stream()
	            .map(WorkMapper::toDto)
	            .collect(Collectors.toList());
	    List<WorkDto> filteredManualWorks = allManualWorks.stream()
	    	    .filter(w -> w.getStartTime().equals(w.getOriginalStartTime()))
				.filter(w -> w.getStatus().equals( WorkStatus.PAUSED.name()))
	    	    .filter(w -> w.getManager() != null && w.getManager().getId() == 1L)
	    	    .collect(Collectors.toList());	    // Se è un utente macchina, filtriamo i lavori in base alle attività della macchina
	    if (isUser) {
	        User machineUser = userRepository.findByUsernameAndMachineUserTrue(username)
	                .orElseThrow(() -> new RuntimeException("Macchina non trovata"));

	        Set<String> machineActivities = machineUser.getLavorazioni().stream()
	                .map(lav -> lav.getNome().toUpperCase())
	                .collect(Collectors.toSet());

	        filteredManualWorks = filteredManualWorks.stream()
	                .filter(work -> machineActivities.contains(work.getActivity()))
	                .collect(Collectors.toList());
	    }

	    // Imposta infoOrdine e infoArticolo su ogni WorkDto senza ulteriori query
	    populateWorkInfo(filteredManualWorks);

	    return filteredManualWorks;
	}

	@Override
	public List<WorkDto> getManualWorksWithTotalMinutesByOrderInProgress(Long orderId) {
		List<WorkSummaryProjection> summaries = workRepository
				.findActiveManualWorksWithTotalMinutesByOrderInProgressExcludingActivities(
						List.of(WorkActivityType.DISPONIBILITA_LOTTO.name(),
								WorkActivityType.DISPONIBILITA_LAVORAZIONE.name()),
						orderId);

		return summaries.stream().map(WorkMapper::workSummaryProjectionToDto) // usa il tuo metodo
				.toList();
	}

	@Override
	public List<WorkDto> getManualWorksWithTotalMinutesByOrder(Long orderId) {
		// 1️⃣ Recupera i summary principali con minuti totali
		List<WorkSummaryProjection> summaries = workRepository
				.findActiveManualWorksWithTotalMinutesByOrderExcludingActivities(
						List.of(WorkActivityType.DISPONIBILITA_LOTTO.name(),
								WorkActivityType.DISPONIBILITA_LAVORAZIONE.name()),
						orderId);

		// 2️⃣ Estrai tutti gli ID dei lavori
		List<Long> workIds = summaries.stream().map(WorkSummaryProjection::getId).toList();

		// 3️⃣ Recupera tutte le posizioni in un’unica query
		List<WorkIdPosizioneProjection> positions = workRepository.findPosizioniByWorkIds(workIds);

		// 4️⃣ Raggruppa per workId
		Map<Long, List<String>> posizioniMap = positions.stream()
				.collect(Collectors.groupingBy(WorkIdPosizioneProjection::getWorkId,
						Collectors.mapping(WorkIdPosizioneProjection::getPosizione, Collectors.toList())));
		List<WorkDto> summariesWithPositions = summaries.stream().map(summary -> {
			WorkDto dto = WorkMapper.workSummaryProjectionToDto(summary);
			dto.setPosizioni(posizioniMap.getOrDefault(summary.getId(), List.of()));
			return dto;
		}).toList();

		// 1️⃣ Estrai tutti gli orderArticleId e articoloId
		Set<Long> orderArticleIds = summariesWithPositions.stream().map(WorkDto::getOrderArticleId)
				.collect(Collectors.toSet());

		Set<Long> articoloIds = summariesWithPositions.stream().map(dto -> dto.getArticolo().getId())
				.collect(Collectors.toSet());

		// 2️⃣ Recupera tutti gli OrdineArticolo in un'unica query
		Map<Long, OrdineArticolo> orderArticleMap = ordineArticoloRepository.findAllById(orderArticleIds).stream()
				.collect(Collectors.toMap(OrdineArticolo::getId, oa -> oa));

		// 3️⃣ Recupera tutti gli Articolo in un'unica query
		Map<Long, Articolo> articoloMap = articoloRepository.findAllById(articoloIds).stream()
				.collect(Collectors.toMap(Articolo::getId, a -> a));

		// 4️⃣ Mappa i dati nei DTO
		for (WorkDto dto : summariesWithPositions) {
			OrdineArticolo oa = orderArticleMap.get(dto.getOrderArticleId());
			if (oa == null)
				throw new RuntimeException("OrdineArticolo non trovato con id " + dto.getOrderArticleId());
			dto.setOrdineArticolo(OrdineArticoloMapper.toDto(oa));

			Articolo articolo = articoloMap.get(dto.getArticolo().getId());
			if (articolo == null)
				throw new RuntimeException("Articolo non trovato con id " + dto.getArticolo().getId());
			dto.setArticolo(ArticoloMapper.toDto(articolo));
		}
		// 5️⃣ Mappa i summary nei DTO e assegna le posizioni
		return summariesWithPositions;
	}

	@Override
	public List<WorkDto> getInProgressManualByOrder(Long id) {
		return workRepository
				.findInProgressManualWorksExcludedActivitiesByOrder(List.of(WorkActivityType.DISPONIBILITA_LOTTO.name(),
						WorkActivityType.DISPONIBILITA_LAVORAZIONE.name()), id)
				.stream().map(WorkMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public List<WorkDto> getNotCompletedManualWorksExcludedActivitiesByOrderWithAllStatus(Long id) {
		return workRepository
				.findNotCompletedManualWorksExcludedActivitiesByOrderWithAllStatus(
						List.of(WorkActivityType.DISPONIBILITA_LOTTO.name(),
								WorkActivityType.DISPONIBILITA_LAVORAZIONE.name()),
						id)
				.stream().map(WorkMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * Esegue la transizione di un Work chiudendo quello corrente e creando un nuovo
	 * Work.
	 *
	 * @param workId    ID del Work
	 * @param newStatus nuovo stato
	 * @return WorkDto del nuovo Work
	 */
	@Transactional
	@Override
	public WorkDto transitionWork(Long workId, WorkStatus newStatus) {
		Work current = workRepository.findById(workId)
				.orElseThrow(() -> new RuntimeException("Work non trovato: " + workId));
		LocalDateTime end = ZonedDateTime.now(ZoneId.of("Europe/Rome")).toLocalDateTime();


		Work cloned = new Work();
		cloned.setOrderArticle(current.getOrderArticle());
		cloned.setArticolo(current.getArticolo());
		cloned.setActivity(current.getActivity());
		cloned.setSpecifiche(current.getSpecifiche());
		cloned.setGrana(current.getGrana());
		cloned.setPastaColore(current.getPastaColore());
		if (current.getPosizioni() != null)
			cloned.setPosizioni(new ArrayList<>(current.getPosizioni()));
		if (current.getOperator() != null)
			cloned.setOperator(userRepository.getReferenceById(current.getOperator().getId()));
		if (current.getOperator2() != null)
			cloned.setOperator2(userRepository.getReferenceById(current.getOperator2().getId()));
		if (current.getOperator3() != null)
			cloned.setOperator3(userRepository.getReferenceById(current.getOperator3().getId()));
		cloned.setQuantita(current.getQuantita());
		cloned.setStatus(newStatus);
		cloned.setOriginalStartTime(current.getOriginalStartTime());
		LocalDateTime start = ZonedDateTime.now(ZoneId.of("Europe/Rome")).toLocalDateTime();
		cloned.setStartTime(start);
        
		
		
		if (current != null 
			    && current.getStartTime() != null
			    && current.getOriginalStartTime() != null
			    && current.getStatus() != null
			    && current.getManager() != null
			    && current.getManager().getUsername() != null) {

			    boolean isStartTimeEqual = current.getStartTime().equals(current.getOriginalStartTime());
			    boolean isPaused = current.getStatus() == WorkStatus.PAUSED;
			    boolean isAdmin = "admin".equals(current.getManager().getUsername());

			    if (isStartTimeEqual && isPaused && isAdmin) {
			        // Cancella il lavoro
			        workRepository.delete(current);
			    } else {
			        // Aggiorna endTime e salva
			        current.setEndTime(end);
			        workRepository.save(current);
			    }
			} else {
		        current.setEndTime(end);
		        workRepository.save(current);
			}

		return WorkMapper.toDto(workRepository.save(cloned));
	}

	/**
	 * Calcola i secondi totali di lavoro per un OrdineArticolo.
	 *
	 * @param orderArticleId ID dell'OrdineArticolo
	 * @return secondi totali
	 */
	@Override
	public long calculateTotalWorkSeconds(Long orderArticleId) {
		OrdineArticolo oa = ordineArticoloRepository.findById(orderArticleId)
				.orElseThrow(() -> new RuntimeException("OrdineArticolo non trovato con id " + orderArticleId));

		List<WorkStatus> excluded = List.of(WorkStatus.PAUSED, WorkStatus.CANCELLED);
		Long totalSeconds = workRepository.getTotalWorkDurationInSeconds(oa, excluded);
		return totalSeconds != null ? totalSeconds : 0L;
	}

	/**
	 * Restituisce il totale del tempo di lavoro in formato ore:minuti:secondi.
	 *
	 * @param orderArticleId ID OrdineArticolo
	 * @return TotalWorkTimeDto
	 */
	@Override
	public TotalWorkTimeDto getTotalWorkTimeDto(Long orderArticleId) {
		long totalSec = calculateTotalWorkSeconds(orderArticleId);
		long hours = totalSec / 3600;
		long minutes = (totalSec % 3600) / 60;
		long seconds = totalSec % 60;
		return TotalWorkTimeDto.builder().hours(hours).minutes(minutes).seconds(seconds).build();
	}

	/**
	 * Chiude l'ultimo Work di un OrdineArticolo.
	 *
	 * @param oa OrdineArticolo
	 */
	public void closeLastWork(OrdineArticolo oa) {
		LocalDateTime end = ZonedDateTime.now(ZoneId.of("Europe/Rome")).toLocalDateTime();

		oa.getWorks().stream().max(Comparator.comparing(Work::getStartTime)).ifPresent(work -> {
			work.setEndTime(end);
			workRepository.save(work);
		});
	}

	/**
	 * Recupera i Work di disponibilità per un ordine specifico.
	 *
	 * @param orderId ID dell'ordine
	 * @return lista di WorkDto
	 */
	@Override
	public List<WorkDto> getAvailabilityWorksByOrder(Long orderId) {
		return workRepository.findAvailabilityWorksByOrder(orderId).stream().map(WorkMapper::toDto)
				.collect(Collectors.toList());
	}

	/**
	 * Recupera i Work lotto per un ordine specifico.
	 *
	 * @param id ID dell'ordine
	 * @return lista di WorkDto
	 */
	@Override
	public List<WorkDto> getLottoWorksByOrder(Long id) {
		return workRepository.findLottoWorksByOrder(id).stream().map(WorkMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * Aggiorna un Work di tipo lotto.
	 *
	 * @param id  ID del Work
	 * @param dto DTO con dati aggiornati
	 * @return WorkDto aggiornato
	 */
	@Override
	public WorkDto updateLottoWork(Long id, WorkDto dto) {

		Work work = workRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Work non trovato con id " + id));

		WorkStatus newStatus = dto.getStatus() != null ? WorkStatus.valueOf(dto.getStatus()) : work.getStatus();

		// ✅ Se la lavorazione è di tipo DISPONIBILITA_LAVORAZIONE,
		// e si vuole impostare IN_PROGRESS ma la lavorazione principale è in pausa o
		// completata
		if (work.getActivity() == WorkActivityType.DISPONIBILITA_LOTTO) {

			// Recupera la lavorazione principale associata all’ordine/articolo
			List<Work> mainWorks = workRepository.findByOrderArticleAndActivity(work.getOrderArticle(),
					WorkActivityType.DISPONIBILITA_LAVORAZIONE);

			if (!mainWorks.isEmpty()) {
				Work mainWork = mainWorks.get(0); // in genere una sola lavorazione principale

				if (mainWork.getStatus() == WorkStatus.PAUSED || mainWork.getStatus() == WorkStatus.COMPLETED) {
					throw new IllegalStateException(String.format(
							"Impossibile mettere in corso la disponibilità di lavorazione: la lavorazione principale è attualmente %s.",
							mainWork.getStatus() == WorkStatus.PAUSED ? "in pausa" : "completata"));
				}
			}
		}

		work.setStatus(newStatus);
		work.setQuantita(dto.getQuantita());

		// Operatori (lookup se presente ID)
		work.setOperator(dto.getOperator() != null && dto.getOperator().getId() != null
				? userRepository.findById(dto.getOperator().getId()).orElse(null)
				: null);

		work.setOperator2(dto.getOperator2() != null && dto.getOperator2().getId() != null
				? userRepository.findById(dto.getOperator2().getId()).orElse(null)
				: null);

		work.setOperator3(dto.getOperator3() != null && dto.getOperator3().getId() != null
				? userRepository.findById(dto.getOperator3().getId()).orElse(null)
				: null);

		Work newWork = workRepository.save(work);
		return WorkMapper.toDto(newWork);
	}

	/**
	 * Cancella un Work di tipo lotto.
	 *
	 * @param id ID del Work
	 */
	@Override
	public void deleteLottoWork(Long id) {
		if (!workRepository.existsById(id))
			throw new RuntimeException("Work non trovato con id " + id);
		workRepository.deleteById(id);
	}

	// ---------------- METODI PRIVATI ----------------

	/**
	 * Assegna gli operatori a un Work.
	 *
	 * @param work Work
	 * @param dto  WorkDto
	 */
	private void assignOperatorsToWork(Work work, WorkDto dto) {
		assignOperatorIfPresent(work::setOperator, dto.getOperator(), "Operatore");
		assignOperatorIfPresent(work::setOperator2, dto.getOperator2(), "Operatore 2");
		assignOperatorIfPresent(work::setOperator3, dto.getOperator3(), "Operatore 3");
	}

	/**
	 * Helper per assegnare un operatore a un setter.
	 *
	 * @param setter      Consumer setter
	 * @param operatorDto DTO dell'operatore
	 * @param errorMsg    messaggio errore se non trovato
	 */
	private void assignOperatorIfPresent(Consumer<User> setter, UserDto operatorDto, String errorMsg) {
		if (operatorDto != null && operatorDto.getId() != null) {
			User user = userRepository.findById(operatorDto.getId())
					.orElseThrow(() -> new RuntimeException(errorMsg + " non trovato"));
			setter.accept(user);
		}
	}
	public void populateWorkInfo(List<WorkDto> works) {
	    if (works == null || works.isEmpty()) {
	        return;
	    }
		// Raccogli tutti gli orderArticleId presenti nei workDto
		Set<Long> orderArticleIds = works.stream().map(WorkDto::getOrderArticleId).collect(Collectors.toSet());

		List<OrdineArticolo> ordineArticoli = ordineArticoloRepository.findAllById(orderArticleIds);
		Map<Long, OrdineArticolo> ordineArticoloMap = ordineArticoli.stream()
				.collect(Collectors.toMap(OrdineArticolo::getId, oa -> oa));
	    for (WorkDto work : works) {
	        if (work == null || work.getOrderArticleId() == null) continue;

	        OrdineArticolo oa = ordineArticoloMap.get(work.getOrderArticleId());
	        if (oa == null) continue;

	        // Info ordine
	        if (oa.getOrdine() != null) {
	            work.setInfoOrdine(
	                oa.getOrdine().getNomeDocumento() != null ? oa.getOrdine().getNomeDocumento() : "-"
	            );

	            if (oa.getOrdine().getAzienda() != null) {
	                work.setNomeAzienda(
	                    oa.getOrdine().getAzienda().getNome() != null ? oa.getOrdine().getAzienda().getNome() : "-"
	                );
	            } else {
	                work.setNomeAzienda("-");
	            }
	        } else {
	            work.setInfoOrdine("-");
	            work.setNomeAzienda("-");
	        }

	        // Info articolo
	        if (work.getArticolo() != null) {
	            work.setInfoArticolo(
	                work.getArticolo().getDescrizione() != null ? work.getArticolo().getDescrizione() : "-"
	            );

	            Long workArticoloId = work.getArticolo().getId();
	            if (oa.getArticolo() != null) {
	                Long parentArticoloId = oa.getArticolo().getId();
	                if (workArticoloId != null && parentArticoloId != null && !workArticoloId.equals(parentArticoloId)) {
	                    work.setInfoArticoloPadre(
	                        oa.getArticolo().getDescrizione() != null ? oa.getArticolo().getDescrizione() : "-"
	                    );
	                }
	            }
	        } else {
	            work.setInfoArticolo("-");
	        }
	    }
	}
	@Override
	public void deletePlannedWork(Long id) {
		if (!workRepository.existsById(id))
			throw new RuntimeException("Lavoro pianificato  non trovato con id " + id);
		workRepository.deleteById(id);		
	}
	private BigDecimal toBigDecimal(Object obj) {
		if (obj == null)
			return BigDecimal.ZERO;
		if (obj instanceof BigDecimal)
			return (BigDecimal) obj;
		if (obj instanceof Double)
			return BigDecimal.valueOf((Double) obj);
		try {
			return new BigDecimal(obj.toString());
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	@Override
	@Transactional
	public WorkDto duplicatePlannedWork(Long id) {
	    Work original = workRepository.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Work non trovato con id: " + id));

	    Work duplicated = new Work();

	    // 🔁 Copia campi principali
	    duplicated.setOrderArticle(original.getOrderArticle());
	    duplicated.setArticolo(original.getArticolo());
	    duplicated.setActivity(original.getActivity());
	    duplicated.setSpecifiche(original.getSpecifiche());
	    duplicated.setGrana(original.getGrana());
	    duplicated.setPastaColore(original.getPastaColore());
	    duplicated.setQuantita(original.getQuantita());
	    duplicated.setManager(original.getManager());

	    if (original.getPosizioni() != null)
	        duplicated.setPosizioni(new ArrayList<>(original.getPosizioni()));

	    if (original.getOperator() != null)
	        duplicated.setOperator(original.getOperator());
	    if (original.getOperator2() != null)
	        duplicated.setOperator2(original.getOperator2());
	    if (original.getOperator3() != null)
	        duplicated.setOperator3(original.getOperator3());

	    duplicated.setStatus(WorkStatus.PAUSED);

 	    LocalDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Rome")).toLocalDateTime();
	    duplicated.setOriginalStartTime(now);
	    duplicated.setStartTime(now);

 	    Work saved = workRepository.save(duplicated);

	    return WorkMapper.toDto(saved);
	}

	@Override
	public WorkDto updatePlannedWork(Long id, WorkDto dto) {
	    Work work = workRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Work non trovato con id: " + id));

 	    if (dto.getOrderArticleId() != null) {
	        OrdineArticolo orderArticle = ordineArticoloRepository.findById(dto.getOrderArticleId())
	                .orElseThrow(() -> new EntityNotFoundException(
	                        "OrderArticle non trovato con id: " + dto.getOrderArticleId()));
	        work.setOrderArticle(orderArticle);
	    }

 	    if (dto.getArticolo() != null && dto.getArticolo().getId() != null) {
	        Articolo articolo = articoloRepository.findById(dto.getArticolo().getId())
	                .orElseThrow(() -> new EntityNotFoundException("Articolo non trovato con id: " + dto.getArticolo().getId()));
	        work.setArticolo(articolo);
	    }

	     if (dto.getManager() != null) work.setManager(getUserSafe(dto.getManager()));
	     work.setQuantita(dto.getQuantita());

 	    if (dto.getActivity() != null) {
	        work.setActivity(WorkActivityType.valueOf(dto.getActivity()));
	    }

 	    if (dto.getSpecifiche() != null) {
	        work.setSpecifiche(WorkSpecificType.valueOf(dto.getSpecifiche()));
	    }

 	    if (dto.getGrana() != null) {
	        work.setGrana(GranaType.valueOf(dto.getGrana()));
	    }

 	    if (dto.getPastaColore() != null) {
	        work.setPastaColore(PastaColoreType.valueOf(dto.getPastaColore()));
	    }

 	    if (dto.getPosizioni() != null) {
	        List<WorkPositionType> posizioniEnum = dto.getPosizioni().stream()
	                .map(WorkPositionType::valueOf)
	                .collect(Collectors.toList());
	        work.setPosizioni(posizioniEnum);
	    }

 	    work.setOperator(getUserSafe(dto.getOperator()));
	    work.setOperator2(getUserSafe(dto.getOperator2()));
	    work.setOperator3(getUserSafe(dto.getOperator3()));

 	    if (dto.getStatus() != null) {
	        work.setStatus(WorkStatus.PAUSED);
	    }
 
	    LocalDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Rome")).toLocalDateTime();
	    work.setOriginalStartTime(now);
	    work.setStartTime(now);
 	    
	    Work saved = workRepository.save(work);
	    return WorkMapper.toDto(saved);
	}

	// Metodo helper per operatori
	private User getUserSafe(UserDto dto) {
	    if (dto != null && dto.getId() != null) {
	        return userRepository.findById(dto.getId())
	                .orElseThrow(() -> new EntityNotFoundException("Operatore non trovato con id: " + dto.getId()));
	    }
	    return null;
	}

}
