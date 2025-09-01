package com.gestione.articoli.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.gestione.articoli.model.User;
import com.gestione.articoli.model.Role;
import com.gestione.articoli.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String defaultAdmin = "admin";
        String defaultUser = "user";//tel/tablet??


        // Controlla se l'utente esiste già
        if (userRepository.findByUsername(defaultAdmin).isEmpty()) {
            User admin = new User();
            admin.setUsername(defaultAdmin);
            admin.setPassword(passwordEncoder.encode("admin"));  // password di default
            admin.setRoles(Set.of(Role.ADMIN));  // ruolo admin

            userRepository.save(admin);

            System.out.println("Utente admin creato all'avvio");
        } else {
            System.out.println("Utente admin già presente");
        }

        // Controlla se l'utente esiste già
        if (userRepository.findByUsername(defaultUser).isEmpty()) {
            User user = new User();
            user.setUsername(defaultUser);
            user.setPassword(passwordEncoder.encode("user"));  // password di default
            user.setRoles(Set.of(Role.USER));  // ruolo user

            userRepository.save(user);

            System.out.println("Utente admin creato all'avvio");
        } else {
            System.out.println("Utente admin già presente");
        }
    }
}
