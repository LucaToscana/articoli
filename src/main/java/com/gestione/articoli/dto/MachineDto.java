package com.gestione.articoli.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MachineDto {

    private Long id;
    private String username;        // nome della postazione
    private Boolean activeInCompany;
    private Boolean machineUser;    // sempre true
    private List<LavorazioneDto> lavorazioni; // nuove lavorazioni associate
}
