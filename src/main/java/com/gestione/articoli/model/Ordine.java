package com.gestione.articoli.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ordini")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "articoli") 
@EqualsAndHashCode(onlyExplicitlyIncluded = true) 
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @Builder.Default
    private LocalDateTime dataOrdine =  LocalDateTime.now();;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean hasDdt = false;   
    
    @Builder.Default
    private String nomeDocumento ="";  

    
    @Enumerated(EnumType.STRING) 
    @Column(nullable = false)
    @Builder.Default
    private WorkStatus workStatus = WorkStatus.PAUSED;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "azienda_id", nullable = false)
    private Azienda azienda;
    
    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrdineArticolo> articoli = new HashSet<>();

    @Column(nullable = true, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal costoOrario = BigDecimal.ZERO;

    @Column(nullable = true, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal costoPersonaleMedio = BigDecimal.ZERO;

    @Column(nullable = true, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal iva = BigDecimal.valueOf(22.0000);

    @Column(nullable = true, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal ricaricoBase = BigDecimal.valueOf(200.0000);
    
    @Builder.Default
    private LocalDateTime dataFattura =  LocalDateTime.now();; 
   
    private String numeroFattura;
  
    @Builder.Default   
    @Column(nullable = true, precision = 19, scale = 2)
    private BigDecimal totaleNetto = BigDecimal.ZERO;
    @Builder.Default
    @Column(nullable = true, precision = 19, scale = 2)
    private BigDecimal totaleIva = BigDecimal.ZERO;
    @Builder.Default
    @Column(nullable = true, precision = 19, scale = 2)
    private BigDecimal totaleLordo = BigDecimal.ZERO;
    @Builder.Default
    @Column(nullable = true, precision = 19, scale = 2)
    private BigDecimal totaleMinutiLavorazioni = BigDecimal.ZERO;
    
    @PrePersist
    public void prePersist() {
        if (this.dataOrdine == null) {
            this.dataOrdine = LocalDateTime.now();
        }
    }
    public String generaNumeroFattura() {
        if (this.id != null) {
            return  "ILPICCHIO-" + this.id;
        }
		return "";
    }
}
