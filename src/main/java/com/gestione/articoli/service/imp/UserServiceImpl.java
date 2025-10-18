package com.gestione.articoli.service.imp;

import com.gestione.articoli.config.PasswordProperties;
import com.gestione.articoli.dto.AdminDto;
import com.gestione.articoli.dto.InactiveUsersDto;
import com.gestione.articoli.dto.LavorazioneDto;
import com.gestione.articoli.dto.MachineDto;
import com.gestione.articoli.dto.OperatorDto;
import com.gestione.articoli.dto.UserDto;
import com.gestione.articoli.mapper.LavorazioneMapper;
import com.gestione.articoli.mapper.UserMapper;
import com.gestione.articoli.model.Lavorazione;
import com.gestione.articoli.model.Role;
import com.gestione.articoli.model.User;
import com.gestione.articoli.repository.LavorazioneRepository;
import com.gestione.articoli.repository.UserRepository;
import com.gestione.articoli.repository.WorkRepository;
import com.gestione.articoli.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final WorkRepository workRepository;
    private final LavorazioneRepository lavorazioneRepository;
    private final LavorazioneMapper lavorazioneMapper; 
	private final PasswordEncoder passwordEncoder;
	private final PasswordProperties passwordProperties;
	private static final int MAX_ADMINS = 16;

	@Override
	public UserDto createUser(UserDto userDto) {
		User user = UserMapper.toEntity(userDto);
		// Qui potresti aggiungere la codifica password se serve
		User saved = userRepository.save(user);
		return UserMapper.toDto(saved);
	}

	@Override
	public UserDto getUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id " + id));
		return UserMapper.toDto(user);
	}

	@Override
	public List<UserDto> getAllUsers() {
		return userRepository.findAll().stream().map(UserMapper::toDto) // usa l'istanza, metodo non statico
				.collect(Collectors.toList());
	}

	@Override
	public List<UserDto> getAllActiveOperators() {
		return userRepository.findByRolesIsEmptyAndActiveInCompanyTrueOrderByUsernameAsc().stream()
				.map(UserMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public List<UserDto> getAllInactiveOperators() {
		return userRepository.findByRolesIsEmptyAndActiveInCompanyFalseOrderByUsernameAsc().stream()
				.map(UserMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public List<UserDto> getAllInactiveUsers() {
		return userRepository.findByActiveInCompanyFalseOrderByUsernameAsc().stream().map(UserMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public UserDto updateUser(Long id, UserDto userDto) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id " + id));

		// aggiorna campi da DTO (qui esempio)
		user.setUsername(userDto.getUsername());
		// aggiorna ruoli o altro se serve...

		User updated = userRepository.save(user);
		return UserMapper.toDto(updated);
	}

	@Override
	public void deleteUser(Long id) {
		// Ottieni l'utente autenticato
		String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		User currentUser = userRepository.findByUsername(currentUsername)
				.orElseThrow(() -> new RuntimeException("Utente autenticato non trovato"));

		// Impedisci di eliminare se stesso
		if (currentUser.getId().equals(id)) {
			throw new RuntimeException("Non puoi eliminare te stesso mentre sei connesso al sistema!");
		}

		var user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Operatore non trovato"));

		// Username speciali
		if (isSpecialUsername(user.getUsername())) {
			throw new RuntimeException("Non puoi eliminare questo operatore!");
		}
		
		if (user.getRoles() != null && !user.getRoles().isEmpty() && user.getRoles().contains(Role.ADMIN)) {
		    long adminCount = userRepository.countActiveByRole(Role.ADMIN);
		    if (adminCount < 5) {
		        throw new RuntimeException(
		            "Impossibile rimuovere questo admin: nel database devono essere presenti un minimo numero di admin."
		        );
		    }
		}

		// Controlla se è assegnato a un Work come operator1/2/3 o manager
		boolean isAssigned = workRepository.existsByOperatorOrOperator2OrOperator3OrManager(user, user, user, user);

		if (isAssigned) {
			throw new RuntimeException(
					"Impossibile eliminare l’operatore: risulta ancora associato ad attività o calcoli passati/presenti. "
							+ "Potrebbe essere collegato a un lotto pianificato, una lavorazione in corso o conclusa, oppure aver eseguito operazioni lato amministrazione. "
							+ "Se non è più attivo, puoi disattivarlo per mantenere i dati storici senza perdere informazioni critiche.");
		}

		userRepository.deleteById(id);

	}

	@Override
	public UserDto findByUsername(String username) {
		return userRepository.findByUsername(username).map(UserMapper::toDto).orElse(null);
	}

	@Override
	public UserDto save(UserDto userDto) {
		User userEntity = UserMapper.toEntity(userDto);
		// eventuale gestione password
		User savedUser = userRepository.save(userEntity);
		return UserMapper.toDto(savedUser);
	}

	/**
	 * Restituisce l'utente autenticato corrente.
	 * 
	 * @return User autenticato
	 * @throws RuntimeException se non autenticato o utente non trovato
	 */
	@Override
	public User getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("Utente non autenticato");
		}

		String username = authentication.getName();

		return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utente non trovato"));
	}

	@Override
	public void createOperatorIfNotExists(String username, BigDecimal retribuzioneOraria) {
		if (userRepository.findByUsername(username).isEmpty()) {
			User user = new User();
			user.setUsername(username);
			user.setPassword(passwordEncoder.encode(passwordProperties.getOperators()));
			user.setRoles(Set.of());
			user.setMachineUser(false);
			user.setActiveInCompany(true);
			user.setRetribuzioneOraria(retribuzioneOraria != null ? retribuzioneOraria : BigDecimal.ZERO);

			userRepository.save(user);
		}
	}

	@Override
	public boolean existsByUsernameIgnoreCase(String username) {
		return userRepository.existsByUsernameIgnoreCase(username);
	}

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
			return "Admin con username '" + username + "' già presente";
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
	public InactiveUsersDto getAllInactiveUsersGrouped() {

		// Admins inattivi
		List<AdminDto> admins = userRepository.findByRolesAndActiveInCompanyFalse(Role.ADMIN).stream()
				.map(user -> AdminDto.builder().id(user.getId()).username(user.getUsername())
						.activeInCompany(user.isActiveInCompany()).machineUser(false).roles(user.getRoles()).build())
				.sorted((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername())).collect(Collectors.toList());

		// Operators inattivi (nessun ruolo)
		List<OperatorDto> operators = userRepository.findByRolesIsEmptyAndActiveInCompanyFalse().stream()
				.map(user -> OperatorDto.builder().id(user.getId()).username(user.getUsername())
						.activeInCompany(user.isActiveInCompany()).machineUser(user.isMachineUser())
						.retribuzioneOraria(user.getRetribuzioneOraria()).build())
				.sorted((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername())).collect(Collectors.toList());

		// Machines inattive
		List<MachineDto> machines = userRepository.findByMachineUserTrueAndActiveInCompanyFalse().stream()
				.map(user -> MachineDto.builder().id(user.getId()).username(user.getUsername())
						.activeInCompany(user.isActiveInCompany()).machineUser(true).build())
				.sorted((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername())).collect(Collectors.toList());

		// Wrapper DTO
		return InactiveUsersDto.builder().admins(admins).operators(operators).machines(machines).build();
	}

	@Override
	public void reactivateUser(Long userId) throws Exception {
		User user = userRepository.findById(userId).orElseThrow(() -> new Exception("Utente non trovato"));
		user.setActiveInCompany(true);
		userRepository.save(user);
	}

	// 🔹 Metodo helper per controllare username speciali
	private boolean isSpecialUsername(String username) {
		if (username == null)
			return false;
		String lower = username.toLowerCase();
		return lower.equals("admin") || lower.equals("user") || lower.equals("manager") || lower.equals("admin_lt")
				|| lower.equals("i");
	}
    @Override
    public Set<LavorazioneDto> getLavorazioniByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User non trovato con id: " + userId));

        return user.getLavorazioni().stream()
                .map(lavorazioneMapper::toDto) // 🔹 usa il mapper iniettato
                .collect(Collectors.toSet());
    }
    @Override
    public void addLavorazioneToUser(Long userId, Long lavorazioneId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User non trovato con id: " + userId));
        
        if (!user.isMachineUser()) {
            throw new IllegalArgumentException("Solo macchine possono avere lavorazioni assegnate.");
        }

        Lavorazione lavorazione = lavorazioneRepository.findById(lavorazioneId)
                .orElseThrow(() -> new IllegalArgumentException("Lavorazione non trovata con id: " + lavorazioneId));

        user.getLavorazioni().add(lavorazione);
        userRepository.save(user);
    }
    @Override
    public void removeLavorazioneFromUser(Long userId, Long lavorazioneId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User non trovato con id: " + userId));

        if (!user.isMachineUser()) {
            throw new IllegalArgumentException("Solo macchine possono rimuovere lavorazioni.");
        }

        Lavorazione lavorazione = lavorazioneRepository.findById(lavorazioneId)
                .orElseThrow(() -> new IllegalArgumentException("Lavorazione non trovata con id: " + lavorazioneId));

        user.getLavorazioni().remove(lavorazione);
        userRepository.save(user);
    }
    @Override
    public BigDecimal getCostoPersonaleMedio() {
        List<User> activeOperators = userRepository.findByRolesIsEmptyAndActiveInCompanyTrueOrderByUsernameAsc();

        if (activeOperators.isEmpty()) {
            return BigDecimal.ZERO; // default se nessun utente valido
        }

        BigDecimal sum = activeOperators.stream()
                .map(User::getRetribuzioneOraria)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal media = sum.divide(
                BigDecimal.valueOf(activeOperators.size()), 
                4, 
                java.math.RoundingMode.CEILING );
        return media ;
    }

}
