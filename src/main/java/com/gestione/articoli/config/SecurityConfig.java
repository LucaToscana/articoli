package com.gestione.articoli.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtRequestFilter jwtRequestFilter;
	private final UserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors(cors -> cors.configurationSource(request -> {
			var corsConfig = new org.springframework.web.cors.CorsConfiguration();
			/*
			 * "http://localhost:5173", "http://localhost:5174"
			 */
			/*
			 * "https://ilpicchio.cloud", "https://www.ilpicchio.cloud"
			 */
			corsConfig.setAllowedOrigins(java.util.List.of("http://localhost:5173", "http://localhost:5174"));
			corsConfig.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
			corsConfig.setAllowedHeaders(java.util.List.of("*"));
			corsConfig.setAllowCredentials(true);
			return corsConfig;
		})).csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
				// Login e registrazione liberi
				.requestMatchers("/api/auth/**").permitAll()
				// Articoli
				.requestMatchers(HttpMethod.POST, "/api/articoli").hasRole("ADMIN").requestMatchers("/api/articoli")
				.permitAll().requestMatchers("/api/articoli/parents").permitAll()
				// Aziende
				.requestMatchers("/api/aziende").permitAll()
				// Ordini
				.requestMatchers(HttpMethod.POST, "/api/ordini").hasRole("ADMIN")
				// DDT
				.requestMatchers(HttpMethod.POST, "/api/ddt").hasRole("ADMIN")
				// Works
				.requestMatchers("/api/works").permitAll().requestMatchers("/api/works/**").permitAll()
				// Ordini
				.requestMatchers(HttpMethod.POST, "/api/ordini").hasRole("ADMIN")
				// Ordine Risultati → solo admin può leggere e scrivere
				.requestMatchers("/api/ordine-risultati/**").hasRole("ADMIN")
				// Works
				.requestMatchers("/api/works").permitAll().requestMatchers("/api/works/**").permitAll()
				
				.requestMatchers("/api/statistics/**").hasRole("ADMIN") // solo admin può leggere le statistiche

				// Tutte le altre richieste richiedono autenticazione
				.anyRequest().authenticated()).authenticationProvider(authenticationProvider())
				// Statistiche

				.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Se usi AuthenticationManager direttamente (es. in AuthController)

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
}
