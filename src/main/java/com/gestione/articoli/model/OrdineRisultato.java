package com.gestione.articoli.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordine_risultati")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrdineRisultato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // 🔹 Ordine di riferimento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordine_id", nullable = false)
    private Ordine ordine;

    // 🔹 Articolo di riferimento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articolo_id", nullable = false)
    private Articolo articolo;

    // ===============================
    // 🔹 MOLATURA
    // ===============================
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal molaturaReale = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal molaturaFatturabile = BigDecimal.ZERO;

    // ===============================
    // 🔹 LUCIDATURA
    // ===============================
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal lucidaturaReale = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal lucidaturaFatturabile = BigDecimal.ZERO;

    // ===============================
    // 🔹 SALDATURA
    // ===============================
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal saldaturaReale = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal saldaturaFatturabile = BigDecimal.ZERO;

    // ===============================
    // 🔹 FORATURA
    // ===============================
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal foraturaReale = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal foraturaFatturabile = BigDecimal.ZERO;

    // ===============================
    // 🔹 FILETTATURA
    // ===============================
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal filettaturaReale = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal filettaturaFatturabile = BigDecimal.ZERO;

    // ===============================
    // 🔹 MONTAGGIO
    // ===============================
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal montaggioReale = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal montaggioFatturabile = BigDecimal.ZERO;

    // ===============================
    // 🔹 SCATOLATURA
    // ===============================
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal scatolaturaReale = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal scatolaturaFatturabile = BigDecimal.ZERO;

    // ===============================
    // 🔹 DATI GENERALI
    // ===============================
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dataRisultato = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal prezzo = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal quantita = BigDecimal.ZERO;

    
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal PREZZO_EFFETTIVO = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal PREZZO_ORARIO_FISSO = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal COSTO_ORARIO_FISSO = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal COSTO_PERSONALE_ORARIO_MEDIO = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal IVA_STANDARD = BigDecimal.valueOf(22.0000);

    @Column(nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal RICARICO_BASE = BigDecimal.valueOf(20.0000);


    public static OrdineRisultato createEmpty(Ordine ordine, Articolo articolo, BigDecimal prezzo) {
    	return OrdineRisultato.builder()
                .ordine(ordine)
                .articolo(articolo)
                .prezzo(prezzo != null ? prezzo : BigDecimal.ZERO)
                .molaturaReale(BigDecimal.ZERO)
                .molaturaFatturabile(BigDecimal.ZERO)
                .lucidaturaReale(BigDecimal.ZERO)
                .lucidaturaFatturabile(BigDecimal.ZERO)
                .saldaturaReale(BigDecimal.ZERO)
                .saldaturaFatturabile(BigDecimal.ZERO)
                .foraturaReale(BigDecimal.ZERO)
                .foraturaFatturabile(BigDecimal.ZERO)
                .filettaturaReale(BigDecimal.ZERO)
                .filettaturaFatturabile(BigDecimal.ZERO)
                .montaggioReale(BigDecimal.ZERO)
                .montaggioFatturabile(BigDecimal.ZERO)
                .scatolaturaReale(BigDecimal.ZERO)
                .scatolaturaFatturabile(BigDecimal.ZERO)
                .quantita(BigDecimal.ZERO)
                .dataRisultato(LocalDateTime.now())
                .build();
    }
    @Override
    public String toString() {
        return "OrdineRisultato{" +
                "id=" + id +
                ", ordineId=" + (ordine != null ? ordine.getId() : null) +
                ", articoloId=" + (articolo != null ? articolo.getId() : null) +
                ", quantita=" + quantita +
                ", prezzo=" + prezzo +
                '}';
    }

}
