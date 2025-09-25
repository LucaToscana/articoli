package com.gestione.articoli.model;


// Enum per il tipo di visibilità di un lavoro
public enum WorkVisibilityType {
    PUBLIC("Pubblico"),
    PRIVATE("Privato"),
    INTERNAL("Interno"),
    TEAM("Team");

    private final String label;

    WorkVisibilityType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}