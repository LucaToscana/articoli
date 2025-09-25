package com.gestione.articoli.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ddt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ddt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long progressivo; // numerico

    @Column(nullable = false, unique = true)
    private String numeroDocumento; // formattato (es. "2025/000123-DDT")

    private LocalDateTime dataDocumento;

    private String causaleTrasporto;
    private String luogoPartenza;
    private String luogoArrivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordine_id", nullable = false)
    private Ordine ordine;

    @PrePersist
    public void prePersist() {
        if (this.dataDocumento == null) {
            this.dataDocumento = LocalDateTime.now();
        }
    }
}
