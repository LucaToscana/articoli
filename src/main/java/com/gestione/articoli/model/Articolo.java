package com.gestione.articoli.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "articoli")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Articolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codice;

    private String descrizione;
}
