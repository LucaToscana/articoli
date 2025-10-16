package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.LavorazioneDto;
import com.gestione.articoli.dto.MachineDto;
import com.gestione.articoli.mapper.LavorazioneMapper;
import com.gestione.articoli.model.Lavorazione;
import com.gestione.articoli.model.Role;
import com.gestione.articoli.model.User;
import com.gestione.articoli.model.Work;
import com.gestione.articoli.model.WorkActivityType;
import com.gestione.articoli.model.WorkStatus;
import com.gestione.articoli.repository.LavorazioneRepository;
import com.gestione.articoli.repository.UserRepository;
import com.gestione.articoli.repository.WorkRepository;
import com.gestione.articoli.service.MachineService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

	private final UserRepository userRepository;
	private final LavorazioneRepository lavorazioneRepository;
	private final PasswordEncoder passwordEncoder;
	private final WorkRepository workRepository;
    private final LavorazioneMapper lavorazioneMapper; 

	@Override
	public String createMachine(String name, String password, List<Long> lavorazioniIds) {
		String normalized = name.trim().toLowerCase();

		if (userRepository.existsByUsernameIgnoreCase(normalized)) {
			return "Nome '" + name + "' già esistente";
		}

		User machine = new User();
		machine.setUsername(normalized);
		machine.setPassword(passwordEncoder.encode(password));
		machine.setRoles(Set.of(Role.USER));
		machine.setMachineUser(true);
		machine.setActiveInCompany(true);

		Set<Lavorazione> lavorazioni = new HashSet<>(lavorazioneRepository.findAllById(lavorazioniIds));
		machine.setLavorazioni(lavorazioni);

		userRepository.save(machine);
		return "Postazione '" + name + "' creata con successo";
	}

	@Override
	public List<MachineDto> getMachines() {
		List<User> users = userRepository.findByMachineUserTrueAndActiveInCompanyTrue();

		return users.stream().filter(user -> !isSpecialUsername(user.getUsername())) // esclude i nomi speciali
				.map(user -> MachineDto.builder().id(user.getId()).username(user.getUsername())
						.activeInCompany(user.isActiveInCompany()).machineUser(user.isMachineUser())
						.lavorazioni(
								user.getLavorazioni() != null
										? user.getLavorazioni().stream().map(
												l -> LavorazioneDto.builder().id(l.getId()).nome(l.getNome()).build())
												.collect(Collectors.toList())
										: Collections.emptyList())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public MachineDto getMachineById(Long id) {
		User m = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Postazione non trovata"));

		// Converti Set<Lavorazione> in List<LavorazioneDto>
		List<LavorazioneDto> lavorazioniDto = m.getLavorazioni().stream()
				.map(l -> LavorazioneDto.builder().id(l.getId()).nome(l.getNome()).build())
				.collect(Collectors.toList());

		MachineDto machine = MachineDto.builder().id(m.getId()).username(m.getUsername())
				.activeInCompany(m.isActiveInCompany()).machineUser(true).lavorazioni(lavorazioniDto).build();

		return machine;
	}

	@Override
	public MachineDto updateMachine(Long id, MachineDto machineDto) {
		// Recupera la macchina dal DB
		User existing = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Postazione non trovata"));

		// Aggiorna nome postazione
		if (machineDto.getUsername() != null && !machineDto.getUsername().trim().isEmpty()) {
			existing.setUsername(machineDto.getUsername().trim().toLowerCase());
		}

		// Aggiorna stato attivo
		if (machineDto.getActiveInCompany() != null) {
			existing.setActiveInCompany(machineDto.getActiveInCompany());

			// Controllo lavorazioni in corso
			List<Work> activeWorks = workRepository.findByManagerAndStatusAndEndTimeIsNull(existing,
					WorkStatus.IN_PROGRESS);

			boolean hasRelevantWork = activeWorks.stream()
					.anyMatch(work -> work.getActivity() != WorkActivityType.DISPONIBILITA_LOTTO
							&& work.getActivity() != WorkActivityType.DISPONIBILITA_LAVORAZIONE);

			if (hasRelevantWork) {
				throw new RuntimeException("Errore: la postazione ha lavorazioni in corso. Chiudile e riprova.");
			}
		}

		// -------------------------------
		// Aggiorna lavorazioni
		if (machineDto.getLavorazioni() != null) {
			// Prendi la lista dal DTO
			List<LavorazioneDto> lavorazioniList = new ArrayList<>(machineDto.getLavorazioni());
			LavorazioneMapper mapper = new LavorazioneMapper();

			// Converti ogni elemento in entità se serve
			Set<Lavorazione> lavorazioniSet = lavorazioniList.stream().map(mapper::toEntity) // supponendo che toEntity
																								// ritorni Lavorazione
					.collect(Collectors.toSet());

			// Imposta il set nel tuo oggetto esistente
			existing.setLavorazioni(lavorazioniSet);
		}
		// -------------------------------

		// Salva macchina aggiornata
		User updated = userRepository.save(existing);

		// Costruisci DTO da ritornare
		List<LavorazioneDto> lavorazioniDtos = updated.getLavorazioni() != null
				? updated.getLavorazioni().stream()
						.map(work -> LavorazioneDto.builder().id(work.getId()).nome(work.getNome()).build()).toList()
				: List.of();

		return MachineDto.builder().id(updated.getId()).username(updated.getUsername())
				.activeInCompany(updated.isActiveInCompany()).machineUser(updated.isMachineUser())
				.lavorazioni(lavorazioniDtos).build();
	}

	@Override
	public void deleteMachine(Long id) {
		var machine = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Macchina non trovata"));

		// Username speciali
		if (isSpecialUsername(machine.getUsername())) {
			throw new RuntimeException("Non puoi eliminare questa macchina!");
		}

		userRepository.deleteById(id);
	}

	@Override
	public MachineDto getMyActivity() {
	    // Recupera username della macchina loggata dal contesto di sicurezza
	    String username = SecurityContextHolder.getContext().getAuthentication().getName();

	    if (username == null || username.isBlank()) {
	        throw new RuntimeException("Utente non autenticato");
	    }

	    // Recupera l'entità User corrispondente
	    User machineUser = userRepository.findByUsernameAndMachineUserTrue(username)
	            .orElseThrow(() -> new RuntimeException("Macchina non trovata"));

	    // Mappa le lavorazioni associate a MachineDto
	    MachineDto dto = MachineDto.builder()
	            .id(machineUser.getId())
	            .username(machineUser.getUsername())
	            .activeInCompany(machineUser.isActiveInCompany())
	            .machineUser(machineUser.isMachineUser())
	            .lavorazioni(lavorazioneMapper.toDtoList(machineUser.getLavorazioni().stream().toList()))
	            .build();

	    return dto;
	}

	public void updateMachinePassword(Long id, String newPassword) {
	    var machine = userRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Macchina non trovata con ID: " + id));

	    if (!machine.isMachineUser()) {
	        throw new RuntimeException("L'utente specificato non è una macchina/postazione");
	    }

	    machine.setPassword(passwordEncoder.encode(newPassword));
	    userRepository.save(machine);
	}

// 🔹 Metodo helper per username speciali
	private boolean isSpecialUsername(String username) {
		if (username == null)
			return false;
		String lower = username.toLowerCase();
		return lower.equals("admin") || lower.equals("user") || lower.equals("manager");
	}
}
