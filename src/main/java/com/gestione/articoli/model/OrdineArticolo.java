package com.gestione.articoli.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordine_articoli")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdineArticolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordine_id", nullable = false)
    private Ordine ordine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articolo_id", nullable = false)
    private Articolo articolo;

    @Column(nullable = false)
    private int quantita;
    
    @Column(nullable = true)
    @Builder.Default
    private BigDecimal prezzo = BigDecimal.ZERO;
    
    @Column(nullable = true)
    @Builder.Default
    private BigDecimal iva = BigDecimal.ZERO;
    
    @Column(nullable = true)
    @Builder.Default
    private BigDecimal prezzoLordo = BigDecimal.ZERO;
    
    // Lista delle lavorazioni associate a questo ordine-articolo
    @OneToMany(mappedBy = "orderArticle", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Work> works = new ArrayList<>();
}
