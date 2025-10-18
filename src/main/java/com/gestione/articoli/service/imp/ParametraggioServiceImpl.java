package com.gestione.articoli.service.imp;

import com.gestione.articoli.dto.ParametraggioDTO;
import com.gestione.articoli.mapper.ParametraggioMapper;
import com.gestione.articoli.model.CategoriaParametraggio;
import com.gestione.articoli.model.Parametraggio;
import com.gestione.articoli.model.TipoValoreParametraggio;
import com.gestione.articoli.repository.ParametraggioRepository;
import com.gestione.articoli.service.ParametraggioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParametraggioServiceImpl implements ParametraggioService {

	private final ParametraggioRepository repository;

	@Override
	public List<ParametraggioDTO> getAll() {
		return repository.findAllByOrderByNomeAsc().stream().map(ParametraggioMapper::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<ParametraggioDTO> getByCategoria(CategoriaParametraggio categoria) {
		return repository.findByCategoria(categoria).stream().map(ParametraggioMapper::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public ParametraggioDTO getByNome(String nome) {
		return repository.findByNomeIgnoreCase(nome).map(ParametraggioMapper::toDTO)
				.orElseThrow(() -> new RuntimeException("Parametro non trovato: " + nome));
	}

	@Override
	public ParametraggioDTO getById(Long id) {
		return repository.findById(id).map(ParametraggioMapper::toDTO)
				.orElseThrow(() -> new RuntimeException("Parametro non trovato con id: " + id));
	}

	@Override
	public ParametraggioDTO save(ParametraggioDTO dto) {
		Parametraggio entity;

		if (dto.getId() != null && repository.existsById(dto.getId())) {
			// 🔹 Se esiste, recupera l'entità attuale
			entity = repository.findById(dto.getId()).orElseThrow();

			// 🔹 Aggiorna solo i campi non-null dal DTO
			if (dto.getNome() != null)
				entity.setNome(dto.getNome());
			if (dto.getCategoria() != null)
				entity.setCategoria(dto.getCategoria());
			if (dto.getDescrizione() != null)
				entity.setDescrizione(dto.getDescrizione());
			// Aggiorna valori basandosi sul tipo
			if (dto.getTipoValore() != null) {
				entity.setTipoValore(dto.getTipoValore());
			}

			if (entity.getTipoValore() == TipoValoreParametraggio.UNITARIO
					|| entity.getTipoValore() == TipoValoreParametraggio.PERCENTUALE
					|| entity.getTipoValore() == TipoValoreParametraggio.ORARIO
					|| entity.getTipoValore() == TipoValoreParametraggio.AL_MINUTO) {

				// Parametri numerici
				if (dto.getValoreNumerico() != null) {
					entity.setValoreNumerico(dto.getValoreNumerico());
				}
				// Puliamo eventuale valore testuale
				entity.setValoreTestuale(null);

			} else if (entity.getTipoValore() == TipoValoreParametraggio.TESTO
					|| entity.getTipoValore() == TipoValoreParametraggio.COSTANTE) {

				// Parametri testuali
				if (dto.getValoreTestuale() != null) {
					entity.setValoreTestuale(dto.getValoreTestuale());
				}
				// Puliamo eventuale valore numerico
				entity.setValoreNumerico(null);
			}
		} else {
			// 🔹 Se non esiste, è un nuovo inserimento
			entity = ParametraggioMapper.toEntity(dto);
		}

		Parametraggio saved = repository.save(entity);
		return ParametraggioMapper.toDTO(saved);
	}

	@Override
	public void delete(Long id) {
		repository.deleteById(id);
	}

}
