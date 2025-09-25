package com.gestione.articoli.controller;

import com.gestione.articoli.config.JwtUtil;
import com.gestione.articoli.dto.UserDto;
import com.gestione.articoli.model.Role;
import com.gestione.articoli.model.User;
import com.gestione.articoli.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
		if (userService.findByUsername(userDto.getUsername()) != null) {
			return ResponseEntity.badRequest().body("Username già in uso");
		}

		User user = new User();
		user.setUsername(userDto.getUsername());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		user.setRoles(Set.of(Role.USER)); // default role

		UserDto savedUserDto = userService.save(UserDto.builder().id(null).username(user.getUsername())
				.password(user.getPassword()).roles(user.getRoles()).build());

		return ResponseEntity.ok(savedUserDto);
	}

	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody UserDto userDto) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();

			// Converti authorities (GrantedAuthority) in Set<Role>
			Set<Role> roles = userDetails.getAuthorities().stream()
					.map(grantedAuthority -> Role.valueOf(grantedAuthority.getAuthority())).collect(Collectors.toSet());

			final String jwt = jwtUtil.generateToken(userDetails.getUsername(), roles);
			return ResponseEntity.ok(new AuthenticationResponse(jwt));
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(401).body("Username o password non validi");
		}
	}

	private record AuthenticationResponse(String jwt) {
	}
}
