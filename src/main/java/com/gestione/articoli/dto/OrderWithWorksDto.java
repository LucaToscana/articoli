package com.gestione.articoli.dto;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderWithWorksDto {
    private Long id;
    private LocalDateTime dataOrdine;
    private boolean hasDdt;
    private String nomeDocumento;
    private String workStatus;

    private Long aziendaId;
    private String aziendaNome;

    private List<OrderArticleWithWorks> articoli;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderArticleWithWorks {
        private Long id;
        private Long articoloId;
        private String codice;
        private String descrizione;
        private int quantita;

        private List<WorkDto> works;
    }
}
