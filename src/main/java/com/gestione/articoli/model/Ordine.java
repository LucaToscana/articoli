package com.gestione.articoli.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ordini")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataOrdine;

    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrdineArticolo> articoli = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (this.dataOrdine == null) {
            this.dataOrdine = LocalDateTime.now();
        }
    }
}
