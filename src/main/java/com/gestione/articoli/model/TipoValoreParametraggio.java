package com.gestione.articoli.model;

/**
 * Indica la natura e la modalità d’uso del valore associato a un parametraggio.
 */
public enum TipoValoreParametraggio {

    /** Prezzo unitario (€/pezzo, €/unità, ecc.) */
    UNITARIO,

    /** Costo o valore espresso per minuto di lavorazione */
    AL_MINUTO,

    /** Percentuale da applicare (es. ricarico, sconto, margine) */
    PERCENTUALE,

    /** Valore costante, non legato a quantità o tempo */
    COSTANTE,

    /** Parametro puramente testuale o informativo */
    TESTO,
    
    ORARIO
    }
