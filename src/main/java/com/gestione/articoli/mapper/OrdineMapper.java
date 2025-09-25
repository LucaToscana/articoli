package com.gestione.articoli.mapper;

import com.gestione.articoli.dto.OrdineDto;
import com.gestione.articoli.model.Azienda;
import com.gestione.articoli.model.Ordine;
import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.model.WorkStatus;
import com.gestione.articoli.repository.ArticoloRepository;
import com.gestione.articoli.repository.AziendaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdineMapper {

	private final ArticoloRepository articoloRepository;
	private final AziendaRepository aziendaRepository;

	public Ordine toEntity(OrdineDto dto) {
		if (dto == null)
			return null;

		Azienda azienda = aziendaRepository.findById(dto.getAziendaId())
				.orElseThrow(() -> new RuntimeException("Azienda non trovata: " + dto.getAziendaId()));
		System.out.println(azienda);
		Ordine ordine = Ordine.builder().id(dto.getId())
				.dataOrdine(dto.getDataOrdine())
				.azienda(azienda)
				.hasDdt(dto.isHasDdt())
				.workStatus(dto.getWorkStatus() != null ? dto.getWorkStatus() : WorkStatus.SCHEDULED)
				.nomeDocumento(dto.getNomeDocumento())
				.build();

		if (dto.getArticoli() != null) {
			dto.getArticoli().forEach(aDto -> {
				OrdineArticolo oa = OrdineArticolo.builder().ordine(ordine)
						.articolo(articoloRepository.findById(aDto.getArticoloId()).orElseThrow(
								() -> new RuntimeException("Articolo non trovato: " + aDto.getArticoloId())))
						.quantita(aDto.getQuantita()).build();
				ordine.getArticoli().add(oa);
			});
		}

		return ordine;
	}

	public OrdineDto toDto(Ordine ordine) {
		if (ordine == null)
			return null;

		return OrdineDto.builder().id(ordine.getId()).dataOrdine(ordine.getDataOrdine())
				.aziendaId(ordine.getAzienda().getId()).nomeAzienda(ordine.getAzienda().getNome())
				.workStatus(ordine.getWorkStatus())
				.hasDdt(ordine.isHasDdt()).nomeDocumento(ordine.getNomeDocumento())
				.articoli(ordine.getArticoli().stream().map(oa -> {
					var articolo = oa.getArticolo();
					var dto = new com.gestione.articoli.dto.OrdineArticoloDto();
					dto.setId(oa.getId());
					dto.setOrdineId(ordine.getId());
					dto.setArticoloId(articolo.getId());
					dto.setQuantita(oa.getQuantita());
					dto.setArticoloCodice(articolo.getCodice());
					dto.setArticoloCodiceComponente(articolo.getCodiceComponente());
					dto.setArticoloDescrizione(articolo.getDescrizione());
					

					if (articolo.getImmagine() != null) {
						dto.setArticoloImmagineBase64(
								java.util.Base64.getEncoder().encodeToString(articolo.getImmagine()));
					} else {
						dto.setArticoloImmagineBase64(null);
					}

					dto.setAziendaNome(articolo.getAzienda() != null 
                            ? articolo.getAzienda().getNome() 
                            : "");					
					return dto;
				}).collect(java.util.stream.Collectors.toSet())).build();
	}

}
