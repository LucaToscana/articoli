package com.gestione.articoli.model;

import jakarta.persistence.*;
import lombok.*;

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

    @PrePersist
    public void prePersist() {
        if (this.dataOrdine == null) {
            this.dataOrdine = LocalDateTime.now();
        }
    }
}
