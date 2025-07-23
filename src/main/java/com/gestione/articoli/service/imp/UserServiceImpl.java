package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.UserDto;
import com.gestione.articoli.mapper.UserMapper;
import com.gestione.articoli.model.User;
import com.gestione.articoli.repository.UserRepository;
import com.gestione.articoli.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        // Qui potresti aggiungere la codifica password se serve
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id " + id));
        return userMapper.toDto(user);
    }
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)  // usa l'istanza, metodo non statico
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
        return userMapper.toDto(updated);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id " + id));
        userRepository.delete(user);
    }

    @Override
    public UserDto findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Override
    public UserDto save(UserDto userDto) {
        User userEntity = userMapper.toEntity(userDto);
        // eventuale gestione password
        User savedUser = userRepository.save(userEntity);
        return userMapper.toDto(savedUser);
    }

}
