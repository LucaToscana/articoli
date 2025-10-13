package com.gestione.articoli.controller;

import com.gestione.articoli.dto.AdminDto;
import com.gestione.articoli.dto.InactiveUsersDto;
import com.gestione.articoli.dto.MachineDto;
import com.gestione.articoli.dto.OperatorDto;
import com.gestione.articoli.dto.UserDto;
import com.gestione.articoli.service.AdminService;
import com.gestione.articoli.service.MachineService;
import com.gestione.articoli.service.OperatorService;
import com.gestione.articoli.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final AdminService adminService;
	private final MachineService machineService;
	private final OperatorService operatorService;

	// Recupera tutti gli utenti
	@GetMapping
	public ResponseEntity<List<UserDto>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	// Recupera solo operatori attivi senza ruoli
	@GetMapping("/operators")
	public ResponseEntity<List<UserDto>> getActiveOperators() {
		return ResponseEntity.ok(userService.getAllActiveOperators());
	}

	// Recupera utente per ID
	@GetMapping("/{id}")
	public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}

	// Crea un nuovo utente
	@PostMapping
	public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
		return ResponseEntity.ok(userService.createUser(dto));
	}

	// Aggiorna un utente
	@PutMapping("/{id}")
	public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto dto) {
		return ResponseEntity.ok(userService.updateUser(id, dto));
	}

	// Elimina un utente
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
	    try {
	        userService.deleteUser(id);
	        return ResponseEntity.ok(Map.of("success", true, "message", "Utente eliminato con successo"));
	    } catch (RuntimeException e) {
	        return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
	    }
	}


	/**
	 * Crea un operatore/postazione. Controlla se esiste già (case-insensitive).
	 */
	@PostMapping("/operator")
	public ResponseEntity<String> createOperator(@RequestBody OperatorDto operator) {
		if (operator == null || operator.getUsername() == null || operator.getUsername().isBlank()) {
			return ResponseEntity.badRequest().body("Username non può essere vuoto");
		}

		// Pulisce spazi e converte in minuscolo per normalizzazione
		String normalizedUsername = operator.getUsername().trim().toLowerCase();

		// Controllo lunghezza minima/massima
		if (normalizedUsername.length() < 3 || normalizedUsername.length() > 20) {
			return ResponseEntity.badRequest().body("Username deve avere tra 3 e 20 caratteri");
		}

		// Controllo se l’utente esiste già (case-insensitive)
		if (userService.existsByUsernameIgnoreCase(normalizedUsername)) {
			return ResponseEntity.badRequest()
					.body("Nome '" + operator.getUsername() + "' già presente");
		}

		// Creazione operatore
		userService.createOperatorIfNotExists(normalizedUsername, operator.getRetribuzioneOraria());
		return ResponseEntity.ok("Operatore '" + operator.getUsername() + "' creato con successo");
	}

	@PostMapping("/admin")
	public ResponseEntity<String> createAdmin(@RequestParam String username, @RequestParam String password) {
		String result = adminService.createAdmin(username, password);
		if (result.startsWith("Admin") && result.endsWith("successo")) {
			return ResponseEntity.ok(result);
		}
		return ResponseEntity.badRequest().body(result);
	}

	@PostMapping("/machine")
	public ResponseEntity<String> createMachine(@RequestParam String name, @RequestParam String password) {
		String result = machineService.createMachine(name, password);
		if (result.endsWith("successo")) {
			return ResponseEntity.ok(result);
		}
		return ResponseEntity.badRequest().body(result);
	}

	@GetMapping("/admins")
	public ResponseEntity<List<AdminDto>> getAdmins() {
		return ResponseEntity.ok(adminService.getAdmins());
	}

	@GetMapping("/machines")
	public ResponseEntity<List<MachineDto>> getMachines() {
		return ResponseEntity.ok(machineService.getMachines());
	}

	@GetMapping("/operators/{id}")
	public ResponseEntity<OperatorDto> getOperatorById(@PathVariable Long id) {
		var operator = operatorService.getOperatorById(id);
		if (operator == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Operatore non trovato");
		}
		return ResponseEntity.ok(operator);
	}

	@GetMapping("/machines/{id}")
	public ResponseEntity<MachineDto> getMachineById(@PathVariable Long id) {
		var machine = machineService.getMachineById(id);
		if (machine == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Macchina non trovata");
		}
		return ResponseEntity.ok(machine);
	}

	@GetMapping("/admins/{id}")
	public ResponseEntity<AdminDto> getAdminById(@PathVariable Long id) {
		try {
			AdminDto admin = adminService.getAdminById(id);
			return ResponseEntity.ok(admin);
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}

	/**
	 * Aggiorna dati di un operatore esistente.
	 */
	@PutMapping("/operators/{id}")
	public ResponseEntity<OperatorDto> updateOperator(@PathVariable Long id, @RequestBody OperatorDto operatorDto) {
		try {
			OperatorDto updated = operatorService.updateOperator(id, operatorDto);
			return ResponseEntity.ok(updated);
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}

	@GetMapping("/inactive")
	public ResponseEntity<InactiveUsersDto> getAllInactiveUsersGrouped() {
		try {
			InactiveUsersDto result = userService.getAllInactiveUsersGrouped();
			return ResponseEntity.ok(result);
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
    @PutMapping("/reactivate/{userId}")
    public ResponseEntity<?> reactivateUser(@PathVariable Long userId) {
        try {
            userService.reactivateUser(userId);
            return ResponseEntity.ok(Collections.singletonMap("message", "Utente riattivato con successo"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
