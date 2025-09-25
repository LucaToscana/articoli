package com.gestione.articoli.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.gestione.articoli.dto.WorkDto;
import com.gestione.articoli.model.GranaType;
import com.gestione.articoli.model.PastaColoreType;
import com.gestione.articoli.model.Work;
import com.gestione.articoli.model.WorkPositionType;
import com.gestione.articoli.model.WorkSpecificType;

public class WorkUtils {

	/**
	 * Applica i campi opzionali del DTO alla entity Work.
	 * Converte eventuali stringhe in enumerazioni, se presenti.
	 * 
	 * @param dto  Il WorkDto contenente i valori opzionali.
	 * @param work L'entity Work da aggiornare con i valori del DTO.
	 */
	public static void applyOptionalEnumsToWork(WorkDto dto, Work work) {
	    
	    // 🔹 Specifiche opzionali: se presenti nel DTO, converti la stringa nell'enum WorkSpecificType
	    if (dto.getSpecifiche() != null) {
	        work.setSpecifiche(WorkSpecificType.valueOf(dto.getSpecifiche()));
	    }

	    // 🔹 Grana opzionale: se presente, converti la stringa nell'enum GranaType
	    if (dto.getGrana() != null) {
	        work.setGrana(GranaType.valueOf(dto.getGrana()));
	    }

	    // 🔹 Pasta colore opzionale: se presente, converti la stringa nell'enum PastaColoreType
	    if (dto.getPastaColore() != null) {
	        work.setPastaColore(PastaColoreType.valueOf(dto.getPastaColore()));
	    }

	    // 🔹 Posizioni opzionali: se presenti, converti ogni stringa della lista nell'enum WorkPositionType
	    if (dto.getPosizioni() != null) {
	        List<WorkPositionType> posizioniEnum = dto.getPosizioni()
	                .stream()
	                .map(WorkPositionType::valueOf) // converte ogni stringa nell'enum corrispondente
	                .collect(Collectors.toList());
	        work.setPosizioni(posizioniEnum);
	    }
	}

}
