package com.gestione.articoli.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.gestione.articoli.model.WorkStatus;
import com.gestione.articoli.model.OrdineRisultato;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdineRisultatoDto {

    private Long id;
    private Long ordineId;
    private WorkStatus workStatus;
    private LocalDateTime dataOrdine;
    private String aziendaNome;
    private Long articoloId;
    private String articoloCodice;

    // MOLATURA
    private BigDecimal molaturaReale;
    private BigDecimal molaturaFatturabile;

    // LUCIDATURA
    private BigDecimal lucidaturaReale;
    private BigDecimal lucidaturaFatturabile;

    // SALDATURA
    private BigDecimal saldaturaReale;
    private BigDecimal saldaturaFatturabile;

    // FORATURA
    private BigDecimal foraturaReale;
    private BigDecimal foraturaFatturabile;

    // FILETTATURA
    private BigDecimal filettaturaReale;
    private BigDecimal filettaturaFatturabile;

    // MONTAGGIO
    private BigDecimal montaggioReale;
    private BigDecimal montaggioFatturabile;

    // SCATOLATURA
    private BigDecimal scatolaturaReale;
    private BigDecimal scatolaturaFatturabile;

    private LocalDateTime dataRisultato;
    private BigDecimal prezzo;
    private BigDecimal quantita;

    /**
     * Costruttore per mappare direttamente OrdineRisultato -> OrdineRisultatoDto
     */
    public OrdineRisultatoDto(OrdineRisultato r) {
        this.id = r.getId();
        this.ordineId = r.getOrdine().getId();
        this.workStatus = r.getOrdine().getWorkStatus();
        this.dataOrdine = r.getOrdine().getDataOrdine();
        this.aziendaNome = r.getOrdine().getAzienda().getNome();
        this.articoloId = r.getArticolo().getId();
        this.articoloCodice = r.getArticolo().getCodice();

        this.molaturaReale = r.getMolaturaReale();
        this.molaturaFatturabile = r.getMolaturaFatturabile();
        this.lucidaturaReale = r.getLucidaturaReale();
        this.lucidaturaFatturabile = r.getLucidaturaFatturabile();
        this.saldaturaReale = r.getSaldaturaReale();
        this.saldaturaFatturabile = r.getSaldaturaFatturabile();
        this.foraturaReale = r.getForaturaReale();
        this.foraturaFatturabile = r.getForaturaFatturabile();
        this.filettaturaReale = r.getFilettaturaReale();
        this.filettaturaFatturabile = r.getFilettaturaFatturabile();
        this.montaggioReale = r.getMontaggioReale();
        this.montaggioFatturabile = r.getMontaggioFatturabile();
        this.scatolaturaReale = r.getScatolaturaReale();
        this.scatolaturaFatturabile = r.getScatolaturaFatturabile();

        this.dataRisultato = r.getDataRisultato();
        this.prezzo = r.getPrezzo();
        this.quantita = r.getQuantita();
    }
}
