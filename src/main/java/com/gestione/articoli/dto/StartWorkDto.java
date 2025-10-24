package com.gestione.articoli.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartWorkDto {
    
    private Long workId;                   // ID del lavoro selezionato
    private List<Long> operatorIds;        // ID degli operatori selezionati
    private String lavorazione;            // Tipo lavorazione selezionato
    private String specifiche;             // Specifiche selezionate
    private String grana;                  // Grana selezionata
    private String pasta;                  // Colore pasta selezionato
    private List<String> posizione;
    private Integer quantita;
    private LocalDateTime startTime;   // nuova
    private LocalDateTime endTime; 
    private Long postazioneId;// nuova
}