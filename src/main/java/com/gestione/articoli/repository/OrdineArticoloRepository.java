package com.gestione.articoli.repository;

import com.gestione.articoli.model.OrdineArticolo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrdineArticoloRepository extends JpaRepository<OrdineArticolo, Long> {

    // Trova tutti gli articoli di un ordine specifico
    List<OrdineArticolo> findByOrdineId(Long ordineId);
    Optional<OrdineArticolo> findByOrdineIdAndArticoloId(Long ordineId, Long articoloId);
	void deleteByOrdineId(Long id);

}
