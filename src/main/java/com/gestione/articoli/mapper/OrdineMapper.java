package com.gestione.articoli.mapper;

import com.gestione.articoli.dto.OrdineDto;
import com.gestione.articoli.model.Azienda;
import com.gestione.articoli.model.Ordine;
import com.gestione.articoli.model.OrdineArticolo;
import com.gestione.articoli.model.WorkStatus;
import com.gestione.articoli.repository.ArticoloRepository;
import com.gestione.articoli.repository.AziendaRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
		Ordine ordine = Ordine.builder()
		        .id(dto.getId())
		        .dataOrdine(dto.getDataOrdine())
		        .dataFattura(dto.getDataFattura() != null ? dto.getDataFattura() : LocalDateTime.now())
		        .numeroFattura(dto.getNumeroFattura())
		        .costoOrario(dto.getCostoOrario() != null ? dto.getCostoOrario() : BigDecimal.ZERO)
		        .costoPersonaleMedio(dto.getCostoPersonaleMedio() != null ? dto.getCostoPersonaleMedio() : BigDecimal.ZERO)
		        .iva(dto.getIva() != null ? dto.getIva() : BigDecimal.valueOf(22.00))
		        .ricaricoBase(dto.getRicaricoBase() != null ? dto.getRicaricoBase() : BigDecimal.valueOf(200.00))
		        .totaleNetto(dto.getTotaleNetto() != null ? dto.getTotaleNetto() : BigDecimal.ZERO)
		        .totaleIva(dto.getTotaleIva() != null ? dto.getTotaleIva() : BigDecimal.ZERO)
		        .totaleLordo(dto.getTotaleLordo() != null ? dto.getTotaleLordo() : BigDecimal.ZERO)
		        .totaleMinutiLavorazioni(dto.getTotaleMinutiLavorazioni())
		        .azienda(azienda)
		        .hasDdt(dto.isHasDdt())
		        .workStatus(dto.getWorkStatus() != null ? dto.getWorkStatus() : WorkStatus.PAUSED)
		        .nomeDocumento(dto.getNomeDocumento())
		        .build();

		if (dto.getArticoli() != null) {
			dto.getArticoli().forEach(aDto -> {
				OrdineArticolo oa = OrdineArticolo.builder().ordine(ordine)
						.articolo(articoloRepository.findById(aDto.getArticoloId()).orElseThrow(
								() -> new RuntimeException("Articolo non trovato: " + aDto.getArticoloId())))
						.prezzo(aDto.getPrezzo())
						.prezzoLordo(aDto.getPrezzoLordo())
						.iva(aDto.getIva())
						.quantita(aDto.getQuantita()).build();
				ordine.getArticoli().add(oa);
			});
		}

		return ordine;
	}

	public OrdineDto toDto(Ordine ordine) {
		if (ordine == null)
			return null;

		return OrdineDto.builder()
			    .id(ordine.getId())
			    .dataOrdine(ordine.getDataOrdine())
			    .dataFattura(ordine.getDataFattura())
			    .numeroFattura(ordine.getNumeroFattura())
			    .costoOrario(ordine.getCostoOrario())
			    .costoPersonaleMedio(ordine.getCostoPersonaleMedio())
			    .iva(ordine.getIva())
			    .ricaricoBase(ordine.getRicaricoBase())
			    .totaleNetto(ordine.getTotaleNetto())
			    .totaleIva(ordine.getTotaleIva())
			    .totaleLordo(ordine.getTotaleLordo())
			    .totaleMinutiLavorazioni(ordine.getTotaleMinutiLavorazioni())
			    .aziendaId(ordine.getAzienda().getId())
			    .nomeAzienda(ordine.getAzienda().getNome())
			    .workStatus(ordine.getWorkStatus())
			    .hasDdt(ordine.isHasDdt())
			    .nomeDocumento(ordine.getNomeDocumento())
				.articoli(ordine.getArticoli().stream().map(oa -> {
					var articolo = oa.getArticolo();
					var dto = new com.gestione.articoli.dto.OrdineArticoloDto();
					dto.setId(oa.getId());
					dto.setOrdineId(ordine.getId());
					dto.setArticoloId(articolo.getId());
					dto.setQuantita(oa.getQuantita());
					dto.setPrezzo(oa.getPrezzo());
					dto.setPrezzoLordo(oa.getPrezzoLordo());
					dto.setIva(oa.getIva());
					dto.setArticoloCodice(articolo.getCodice());
					dto.setArticoloCodiceComponente(articolo.getCodiceComponente());
					dto.setArticoloDescrizione(articolo.getDescrizione());
					dto.setTotaleMinutiLavorazioni(oa.getTotaleMinutiLavorazioni());
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
