package com.gestione.articoli.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkDto {

    private Long id;

    // Lo stato come String (nome dell'enum WorkStatus)
    private String status;       

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // ID dell'ordine-articolo a cui appartiene il lavoro
    private Long orderArticleId;
}
