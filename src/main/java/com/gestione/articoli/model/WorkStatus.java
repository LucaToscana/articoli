package com.gestione.articoli.model;

public enum WorkStatus {
    SCHEDULED,   // Pianificato, il lavoro è programmato ma non ancora iniziato
    IN_PROGRESS, // In corso, il lavoro è attualmente in esecuzione
    PAUSED,      // Pausa, il lavoro è temporaneamente sospeso
    ON_HOLD,     // Sospeso, il lavoro è fermo per motivi esterni o ufficiali
    COMPLETED,   // Completato, il lavoro è terminato con successo
    REVIEW,      // In revisione, il lavoro è completato ma in attesa di verifica
    FAILED,      // Fallito, il lavoro è terminato con errori o problemi
    CANCELLED    // Annullato, il lavoro è stato cancellato
}
