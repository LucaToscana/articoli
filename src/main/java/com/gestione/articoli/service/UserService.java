package com.gestione.articoli.service;

import com.gestione.articoli.dto.InactiveUsersDto;
import com.gestione.articoli.dto.LavorazioneDto;
import com.gestione.articoli.dto.UserDto;
import com.gestione.articoli.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

	Object findByUsername(String username);

	UserDto save(UserDto build);

	User getAuthenticatedUser();

	List<UserDto> getAllActiveOperators();

	boolean existsByUsernameIgnoreCase(String normalizedUsername);

	String createAdmin(String username, String password);

	void createOperatorIfNotExists(String username, BigDecimal retribuzioneOraria);

	List<UserDto> getAllInactiveOperators();

	List<UserDto> getAllInactiveUsers();

	InactiveUsersDto getAllInactiveUsersGrouped();


	void reactivateUser(Long userId) throws Exception;
	
    // 🔹 Gestione lavorazioni
    Set<LavorazioneDto> getLavorazioniByUserId(Long id);
    void removeLavorazioneFromUser(Long id, Long lavorazioneId);

	void addLavorazioneToUser(Long id, Long lavorazioneId);

}
