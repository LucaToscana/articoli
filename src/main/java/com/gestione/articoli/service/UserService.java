package com.gestione.articoli.service;

import com.gestione.articoli.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

	Object findByUsername(String username);

	UserDto save(UserDto build);
}
