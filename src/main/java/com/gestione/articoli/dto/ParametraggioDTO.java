package com.gestione.articoli.dto;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.gestione.articoli.model.CategoriaParametraggio;
import com.gestione.articoli.model.TipoValoreParametraggio;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametraggioDTO {

    private Long id;
    private String nome;
    private CategoriaParametraggio categoria;
    private TipoValoreParametraggio tipoValore;
    private BigDecimal valoreNumerico;
    private String valoreTestuale;
    private String descrizione;
    private boolean attivo;
    private LocalDateTime dataUltimaModifica;
}
