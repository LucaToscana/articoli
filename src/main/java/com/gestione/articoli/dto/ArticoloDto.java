package com.gestione.articoli.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticoloDto {

    private Long id;
    private String codice;
    private String codiceComponente;
    private String descrizione;

    private String immagineBase64; 

    private LocalDateTime dataCreazione;
    private BigDecimal prezzoIdeale;
    private boolean conDdt;
    private boolean attivoPerProduzione;
    private AziendaDto azienda;

    // 🔹 nuovi campi per relazioni molti-a-molti
    private Set<Long> articoliPadriIds;
    private Set<Long> articoliFigliIds;

    // Costruttore comodo minimo
    public ArticoloDto(Long id, String codice) {
        this.id = id;
        this.codice = codice;
    }

    // Costruttore comodo con codice + descrizione
    public ArticoloDto(String codice, String descrizione) {
        this.codice = codice;
        this.descrizione = descrizione;
    }

    // Costruttore comodo con info principali
    public ArticoloDto(String codice, String descrizione,
                       BigDecimal prezzoIdeale,
                       boolean conDdt, boolean attivoPerProduzione) {
        this.codice = codice;
        this.descrizione = descrizione;
        this.prezzoIdeale = prezzoIdeale != null ? prezzoIdeale : BigDecimal.ZERO;
        this.conDdt = conDdt;
        this.attivoPerProduzione = attivoPerProduzione;
    }

    // Costruttore completo senza immagine (es. query leggere)
    public ArticoloDto(Long id, String codice, String codiceComponente,
                       String descrizione, LocalDateTime dataCreazione,
                       BigDecimal prezzoIdeale, boolean conDdt, boolean attivoPerProduzione,
                       Set<Long> articoliPadriIds, Set<Long> articoliFigliIds) {
        this.id = id;
        this.codice = codice;
        this.codiceComponente = codiceComponente;
        this.descrizione = descrizione;
        this.dataCreazione = dataCreazione;
        this.prezzoIdeale = prezzoIdeale != null ? prezzoIdeale : BigDecimal.ZERO;
        this.conDdt = conDdt;
        this.attivoPerProduzione = attivoPerProduzione;
        this.articoliPadriIds = articoliPadriIds;
        this.articoliFigliIds = articoliFigliIds;
    }

    // Costruttore con azienda e relazioni
    public ArticoloDto(Long id, String codice, String codiceComponente,
                       String descrizione, LocalDateTime dataCreazione,
                       BigDecimal prezzoIdeale, boolean conDdt, boolean attivoPerProduzione,
                       AziendaDto azienda, Set<Long> articoliPadriIds, Set<Long> articoliFigliIds) {
        this.id = id;
        this.codice = codice;
        this.codiceComponente = codiceComponente;
        this.descrizione = descrizione;
        this.dataCreazione = dataCreazione;
        this.prezzoIdeale = prezzoIdeale != null ? prezzoIdeale : BigDecimal.ZERO;
        this.conDdt = conDdt;
        this.attivoPerProduzione = attivoPerProduzione;
        this.azienda = azienda;
        this.articoliPadriIds = articoliPadriIds;
        this.articoliFigliIds = articoliFigliIds;
    }

    // Costruttore minimo con codice e azienda
    public ArticoloDto(String codice, AziendaDto azienda) {
        this.codice = codice;
        this.azienda = azienda;
    }
}
