package com.gestione.articoli.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "articoli")
public class Articolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codice;

    private String codiceComponente;
    private String descrizione;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "immagine")
    private byte[] immagine;

    @Column(nullable = false)
    private LocalDateTime dataCreazione;

    @Column(nullable = false)
    private BigDecimal prezzoIdeale;

    @Column(name = "attivo_per_produzione", nullable = false)
    private boolean attivoPerProduzione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "azienda_id")
    private Azienda azienda;

    @ManyToMany
    @JoinTable(
            name = "articoli_relazioni",
            joinColumns = @JoinColumn(name = "articolo_id"),
            inverseJoinColumns = @JoinColumn(name = "articolo_padre_id")
    )
    private Set<Articolo> articoliPadri = new HashSet<>();

    @ManyToMany(mappedBy = "articoliPadri")
    private Set<Articolo> articoliFigli = new HashSet<>();

    @OneToMany(mappedBy = "articolo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrdineArticolo> ordini = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (this.dataCreazione == null) this.dataCreazione = LocalDateTime.now();
        if (this.prezzoIdeale == null) this.prezzoIdeale = BigDecimal.ZERO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Articolo)) return false;
        Articolo other = (Articolo) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
