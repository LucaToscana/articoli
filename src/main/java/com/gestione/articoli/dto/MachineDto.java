package com.gestione.articoli.dto;

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
}
