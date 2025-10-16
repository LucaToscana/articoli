package com.gestione.articoli.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import com.gestione.articoli.model.User;
import com.gestione.articoli.model.Role;
import com.gestione.articoli.model.Lavorazione;
import com.gestione.articoli.repository.UserRepository;
import com.gestione.articoli.repository.LavorazioneRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LavorazioneRepository lavorazioneRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // ----------------------------
        // 1️⃣ Creazione lavorazioni base se non esistono
        // ----------------------------
        String[] lavorazioniBase = {
            "Molatura", "Lucidatura", "Saldatura", "Foratura", "Filettatura", "Montaggio", "Scatolatura"
        };

        for (String nome : lavorazioniBase) {
            if (lavorazioneRepository.findByNome(nome).isEmpty()) {
                Lavorazione lav = new Lavorazione();
                lav.setNome(nome);
                lav.setDescrizione(nome + " di default");
                lavorazioneRepository.save(lav);
                System.out.println("Lavorazione creata: " + nome);
            }
        }

        // ----------------------------
        // 2️⃣ Creazione utente admin
        // ----------------------------
        String defaultAdmin = "admin";
        if (userRepository.findByUsername(defaultAdmin).isEmpty()) {
            User admin = new User();
            admin.setUsername(defaultAdmin);
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles(Set.of(Role.ADMIN));
            admin.setMachineUser(false);
            admin.setActiveInCompany(true);
            admin.setRetribuzioneOraria(BigDecimal.ZERO);
            userRepository.save(admin);
            System.out.println("Utente admin creato all'avvio");
        } else {
            System.out.println("Utente admin già presente");
        }

        // ----------------------------
        // 3️⃣ Creazione macchina di default
        // ----------------------------
        String defaultMachine = "machine1";
        if (userRepository.findByUsername(defaultMachine).isEmpty()) {
            User machine = new User();
            machine.setUsername(defaultMachine);
            machine.setPassword(passwordEncoder.encode("machine"));
            machine.setRoles(Set.of(Role.USER));
            machine.setMachineUser(true);
            machine.setActiveInCompany(true);
            machine.setRetribuzioneOraria(BigDecimal.ZERO);

            // Assegna tutte le lavorazioni create
            Set<Lavorazione> tutteLavorazioni = Set.copyOf(lavorazioneRepository.findAll());
            machine.setLavorazioni(tutteLavorazioni);

            userRepository.save(machine);
            System.out.println("Macchina creata all'avvio con tutte le lavorazioni");
        } else {
            System.out.println("Macchina già presente");
        }
    }
}
