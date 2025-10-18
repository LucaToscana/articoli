package com.gestione.articoli.model;

public enum CategoriaParametraggio {

    /** Parametri relativi alle lavorazioni e ai cicli produttivi */
    LAVORAZIONI,

    /** Parametri generali dell’applicazione, validi a livello globale */
    GENERALE,

    /** Parametri economici, contabili o di calcolo del prezzo (margini, IVA, sconti, ecc.) */
    ECONOMICI,

    /** Parametri relativi alla logistica, magazzino o gestione articoli */
    LOGISTICA,

    /** Parametri relativi alla produzione, impianti e risorse strutturali */
    PRODUZIONE,

    /** Parametri amministrativi e fiscali */
    AMMINISTRATIVI,

    /** Parametri strutturali o di sistema (setup, costi fissi, configurazioni interne) */
    STRUTTURALI,

    /** Categoria generica per elementi non classificati */
    ALTRO
}