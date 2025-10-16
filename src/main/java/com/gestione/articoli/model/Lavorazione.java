package com.gestione.articoli.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lavorazioni")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lavorazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    private String descrizione;
}
