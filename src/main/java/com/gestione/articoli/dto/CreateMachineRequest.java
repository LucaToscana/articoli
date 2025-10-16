package com.gestione.articoli.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateMachineRequest {
    private String name;
    private String password;
    private List<Long> lavorazioniIds;
}
