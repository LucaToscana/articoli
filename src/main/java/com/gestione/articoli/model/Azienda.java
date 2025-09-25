package com.gestione.articoli.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aziende")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Azienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(name = "partita_iva", unique = true)
    private String partitaIva;

    @OneToMany(mappedBy = "azienda", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Articolo> articoli = new ArrayList<>();

    public void addArticolo(Articolo articolo) {
        articoli.add(articolo);
        articolo.setAzienda(this);
    }

    public void removeArticolo(Articolo articolo) {
        articoli.remove(articolo);
        articolo.setAzienda(null);
    }
}
