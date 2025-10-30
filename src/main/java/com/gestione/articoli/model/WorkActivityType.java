package com.gestione.articoli.model;

public enum WorkActivityType {
    DISPONIBILITA_LOTTO("Disponibilità lotto"),  
    DISPONIBILITA_LAVORAZIONE("Disponibilità alla lavorazione"),
    MOLATURA("Molatura"),
    LUCIDATURA("Lucidatura"),
    SALDATURA("Saldatura"),
    FORATURA("Foratura"),
    FILETTATURA("Filettatura"),
    MONTAGGIO("Montaggio"),
    SCATOLATURA("Scatolatura"),
    STACCARE("Staccare"),
    LAVARE("Lavare"),
    ANNERIRE("Annerire"),
    SATINARE("Satinare");
    private final String label;

    WorkActivityType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
