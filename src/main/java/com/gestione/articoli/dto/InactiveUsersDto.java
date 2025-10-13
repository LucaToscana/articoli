package com.gestione.articoli.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InactiveUsersDto {
    private List<AdminDto> admins;
    private List<OperatorDto> operators;
    private List<MachineDto> machines;
}
