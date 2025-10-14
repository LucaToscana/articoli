package com.gestione.articoli.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticoloOrdersDto {
    private List<OrdineDto> ordiniComePadre;
    private List<OrdineDto> ordiniComeFiglio;
}
