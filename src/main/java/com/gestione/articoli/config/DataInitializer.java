package com.gestione.articoli.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import com.gestione.articoli.model.*;
import com.gestione.articoli.repository.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final LavorazioneRepository lavorazioneRepository;
	private final ParametraggioRepository parametraggioRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
    public void run(String... args) throws Exception {

        // ----------------------------
        // 1️ Creazione lavorazioni base se non esistono
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
                System.out.println("✅ Lavorazione creata: " + nome);
            }
        }

        // ----------------------------
        // 2️ Creazione utente admin
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
            System.out.println("✅ Utente admin creato all'avvio");
        } else {
            System.out.println("ℹ️ Utente admin già presente");
        }

        // ----------------------------
        // 3️ Creazione macchina di default
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
            System.out.println("✅ Macchina creata all'avvio con tutte le lavorazioni");
        } else {
            System.out.println("ℹ️ Macchina già presente");
        }

     // ----------------------------
     // 4️ Creazione parametraggi di base
     // ----------------------------
     if (parametraggioRepository.count() == 0) {
         List<Parametraggio> parametriIniziali = List.of(
             // 🔹 ECONOMICI
             Parametraggio.builder()
                     .nome("RICARICO_BASE")
                     .categoria(CategoriaParametraggio.ECONOMICI)
                     .tipoValore(TipoValoreParametraggio.PERCENTUALE)
                     .valoreNumerico(BigDecimal.valueOf(200))
                     .descrizione("Percentuale di ricarico base")
                     .attivo(true)
                     .build(),

             Parametraggio.builder()
                     .nome("IVA_STANDARD")
                     .categoria(CategoriaParametraggio.ECONOMICI)
                     .tipoValore(TipoValoreParametraggio.PERCENTUALE)
                     .valoreNumerico(BigDecimal.valueOf(22))
                     .descrizione("Aliquota IVA standard")
                     .attivo(true)
                     .build(),

             // 🔹 LAVORAZIONI
             Parametraggio.builder()
                     .nome("COSTO_PERSONALE_ORARIO_MEDIO")
                     .categoria(CategoriaParametraggio.ECONOMICI)
                     .tipoValore(TipoValoreParametraggio.ORARIO) // o AL_MINUTO se definito
                     .valoreNumerico(BigDecimal.valueOf(16))
                     .descrizione("Costo medio del personale per ora. Sono considerati solo gli OPERATORI ATTIVI.")
                     .attivo(true)
                     .build(),

             Parametraggio.builder()
                     .nome("PREZZO_ORARIO_FISSO")
                     .categoria(CategoriaParametraggio.ECONOMICI)
                     .tipoValore(TipoValoreParametraggio.ORARIO) // o AL_MINUTO se definito
                     .valoreNumerico(BigDecimal.valueOf(35))
                     .descrizione("Prezzo calcolato all' ora per le lavorazioni. NON TASSATO.")
                     .attivo(true)
                     .build() ,
                     
             Parametraggio.builder()
                     .nome("COSTO_ORARIO_FISSO")
                     .categoria(CategoriaParametraggio.ECONOMICI)
                     .tipoValore(TipoValoreParametraggio.ORARIO) // o AL_MINUTO se definito
                     .valoreNumerico(BigDecimal.valueOf(2))
                     .descrizione(" Spese generali e strutturali aziendali. Costo all' ora per le lavorazioni, non é incluso COSTO_PERSONALE_ORARIO_MEDIO")
                     .attivo(true)
                     .build()        
                     
                     
         );

         parametraggioRepository.saveAll(parametriIniziali);
         System.out.println("✅ Parametraggi di base creati all’avvio (" + parametriIniziali.size() + " record).");
     } else {
         System.out.println("ℹ️ Parametraggi già presenti (" + parametraggioRepository.count() + " record).");
     }
    }
}
