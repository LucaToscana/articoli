package com.gestione.articoli.dto;

import java.math.BigDecimal;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorDto {

    private Long id;
    private String username;
    private Boolean activeInCompany;
    private Boolean machineUser;
    private BigDecimal retribuzioneOraria ;


}
