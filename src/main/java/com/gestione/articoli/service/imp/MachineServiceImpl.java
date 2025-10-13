package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.MachineDto;
import com.gestione.articoli.model.Role;
import com.gestione.articoli.model.User;
import com.gestione.articoli.repository.UserRepository;
import com.gestione.articoli.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public String createMachine(String name, String password) {
		String normalized = name.trim().toLowerCase();

		if (userRepository.existsByUsernameIgnoreCase(normalized)) {
			return "Postazione con nome '" + name + "' già esistente";
		}

		User machine = new User();
		machine.setUsername(normalized);
		machine.setPassword(passwordEncoder.encode(password));
		machine.setRoles(Set.of(Role.USER));
		machine.setMachineUser(true);
		machine.setActiveInCompany(true);

		userRepository.save(machine);
		return "Postazione '" + name + "' creata con successo";
	}

	@Override
	public List<MachineDto> getMachines() {
	    List<User> users = userRepository.findByMachineUserTrueAndActiveInCompanyTrue();

	    return users.stream()
	            .filter(user -> !isSpecialUsername(user.getUsername())) // esclude i nomi speciali
	            .map(user -> MachineDto.builder()
	                    .id(user.getId())
	                    .username(user.getUsername())
	                    .activeInCompany(user.isActiveInCompany())
	                    .machineUser(user.isMachineUser())
	                    .build())
	            .collect(Collectors.toList());
	}
	@Override
	public MachineDto getMachineById(Long id) {
		User m = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Postazione non trovata"));
		return MachineDto.builder().id(m.getId()).username(m.getUsername()).activeInCompany(m.isActiveInCompany())
				.machineUser(true).build();
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

// 🔹 Metodo helper per username speciali
	private boolean isSpecialUsername(String username) {
		if (username == null)
			return false;
		String lower = username.toLowerCase();
		return lower.equals("admin") || lower.equals("user") || lower.equals("manager");
	}
}
