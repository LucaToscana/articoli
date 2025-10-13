package com.gestione.articoli.service.imp;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gestione.articoli.dto.AdminDto;
import com.gestione.articoli.model.Role;
import com.gestione.articoli.model.User;
import com.gestione.articoli.repository.UserRepository;
import com.gestione.articoli.service.AdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	private static final int MAX_ADMINS = 50;

	@Override
	public String createAdmin(String username, String password) {
		String normalized = username.trim().toLowerCase();

		// Limite max 16 admin
		long adminCount = userRepository.countByRolesContaining(Role.ADMIN);
		if (adminCount >= MAX_ADMINS) {
			return "Limite massimo di 16 admin raggiunto";
		}

		// Check esistenza
		if (userRepository.existsByUsernameIgnoreCase(normalized)) {
			return "Nome '" + username + "' già presente";
		}

		User user = new User();
		user.setUsername(normalized);
		user.setPassword(passwordEncoder.encode(password));
		user.setRoles(Set.of(Role.ADMIN));
		user.setMachineUser(false);
		user.setActiveInCompany(true);

		userRepository.save(user);
		return "Admin '" + username + "' creato con successo";
	}

	@Override
	public List<AdminDto> getAdmins() {
		return userRepository
				.findByRolesContainingAndActiveInCompanyTrueAndUsernameNotIgnoreCaseAndUsernameNotIgnoreCase(Role.ADMIN,
						"admin", "user")
				.stream()
				.map(user -> AdminDto.builder().id(user.getId()).username(user.getUsername())
						.activeInCompany(user.isActiveInCompany()).roles(user.getRoles()).build())
				.collect(Collectors.toList());
	}

	@Override
	public AdminDto getAdminById(Long id) {
		User admin = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Admin non trovato"));
		return AdminDto.builder().id(admin.getId()).username(admin.getUsername())
				.activeInCompany(admin.isActiveInCompany()).machineUser(false).roles(admin.getRoles()).build();
	}

	public void deleteUser(Long id) {
		var user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Utente non trovato"));

		// Controlla se lo username è "admin" (case-insensitive)
		if ("admin".equalsIgnoreCase(user.getUsername())) {
			throw new RuntimeException("Non puoi eliminare l'utente 'admin'!");
		}

		// Altrimenti elimina
		userRepository.deleteById(id);
	}

	@Override
	public void deleteAdmin(Long id) {
	    var admin = userRepository.findById(id)
	                 .orElseThrow(() -> new RuntimeException("Admin non trovato"));

	    // Controllo username speciali
	    if (isSpecialUsername(admin.getUsername())) {
	        throw new RuntimeException("Non puoi eliminare questo admin!");
	    }

	    userRepository.deleteById(id);
	}
	
	private boolean isSpecialUsername(String username) {
	    if (username == null) return false;
	    String lower = username.toLowerCase();
	    return lower.equals("admin") || lower.equals("user") || lower.equals("manager");
	}
}
