package com.gestione.articoli.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "works")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Ordine a cui appartiene la lavorazione
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_article_id", nullable = false)
    private OrdineArticolo orderArticle;

    @Column(nullable = false)
    private int quantita;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkStatus status;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    // 🔹 Utente gestore della lavorazione (admin, responsabile)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    // 🔹 Utente che esegue la lavorazione (operatore/macchinista principale)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private User operator;

    // 🔹 Operatore 2
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator2_id")
    private User operator2;

    // 🔹 Operatore 3
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator3_id")
    private User operator3;

    // 🔹 Articolo specifico della lavorazione
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articolo_id", nullable = false)
    private Articolo articolo;

    // 🔹 Tipo attività principale
    @Enumerated(EnumType.STRING)
    @Column(name = "activity", nullable = false)
    private WorkActivityType activity;

    
    @ElementCollection(targetClass = WorkPositionType.class)
    @CollectionTable(name = "work_positions", joinColumns = @JoinColumn(name = "work_id"))
    @Column(name = "posizione")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<WorkPositionType> posizioni = new ArrayList<>();
    
    // 🔹 Tipo attività specifica
    @Enumerated(EnumType.STRING)
    @Column(name = "specifiche", nullable = true)
    private WorkSpecificType specifiche;

    // 🔹 Grana (valori numerici come 800, 600)
    @Enumerated(EnumType.STRING)
    @Column(name = "grana", nullable = true)
    private GranaType grana;

    // 🔹 Pasta con colore (rossa, gialla, bianca)
    @Enumerated(EnumType.STRING)
    @Column(name = "pasta_colore", nullable = true)
    private PastaColoreType pastaColore;
}
