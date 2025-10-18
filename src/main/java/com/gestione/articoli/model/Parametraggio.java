package com.gestione.articoli.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parametraggi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Parametraggio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoriaParametraggio categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoValoreParametraggio tipoValore;

    @Column(precision = 12, scale = 4)
    private BigDecimal valoreNumerico;

    @Column(length = 500)
    private String valoreTestuale;

    @Column(length = 500)
    private String descrizione;

    @Builder.Default
    private boolean attivo = true;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dataUltimaModifica = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    private void aggiornaData() {
        this.dataUltimaModifica = LocalDateTime.now();
    }
}
