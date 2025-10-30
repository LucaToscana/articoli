package com.gestione.articoli.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class StatisticsDto {

    // --- Periodo di riferimento ---
    private LocalDate dataInizio;
    private LocalDate dataFine;

    // --- Statistiche base ordini ---
    private long totaleOrdini;
    private long ordiniCompletati;
    private long ordiniAnnullati;
    private long ordiniInCorso;

    // --- Statistiche economiche ---
    private BigDecimal totaleRicavoLordo;     
    private BigDecimal totaleRicavoNetto;     
    private BigDecimal totaleIva;             
    private BigDecimal totaleCosti;           
    private BigDecimal utileNetto;            

    // --- Costi dettagliati aggiuntivi ---
    private BigDecimal costoPersonale;        // Salari, stipendi, benefit
    private BigDecimal costoFisso;            // Affitto, utenze, manutenzione
    private BigDecimal costoVariabile;        // Opzionale: costo legato alla produzione o vendita

    // --- Statistiche temporali ---
    private BigDecimal totaleOre;             
    private BigDecimal totaleMinuti;          
    private BigDecimal tempoMedioPerOrdine;  
    private BigDecimal tempoTotaleAttivo;     

    // --- Tempi di lavorazione per tipo ---
    private BigDecimal totaleMolatura;        
    private BigDecimal totaleLucidatura;      
    private BigDecimal totaleSaldatura;       
    private BigDecimal totaleForatura;        
    private BigDecimal totaleFilettatura;     
    private BigDecimal totaleMontaggio;       
    private BigDecimal totaleScatolatura;     

    //  Nuove lavorazioni aggiunte
    private BigDecimal totaleStaccare;        
    private BigDecimal totaleLavare;          
    private BigDecimal totaleAnnerire;        
    private BigDecimal totaleSatinare;        

    // --- Indicatori di efficienza ---
    private BigDecimal produttivitaOraria;    
    private BigDecimal mediaOrdiniOra;        
    private BigDecimal mediaArticoliOra;      
    private BigDecimal mediaArticoliOrdine;   

    // --- Indicatori di performance ---
    private BigDecimal tassoAnnullamento;     
    private BigDecimal tassoCompletamento;    
    private BigDecimal marginePercentuale;    
    private BigDecimal crescitaPercentuale;   
    private BigDecimal tassoOccupazione;      

    // --- Indicatori cliente / vendita ---
    private BigDecimal valoreMedioOrdine;     
    private BigDecimal valoreMedioArticolo;   
    private long totaleClientiUnici;          
    private BigDecimal ricavoMedioCliente;    

    // --- Altri indicatori opzionali ---
    private long totaleArticoliVenduti;
    private BigDecimal costoMedioArticolo;
    private BigDecimal utileMedioArticolo;
    private BigDecimal utileMedioOrdine;

    // --- IVA dettagliata opzionale ---
    private BigDecimal iva10Percent;          // IVA 10%
    private BigDecimal iva22Percent;          // IVA 22%
}
