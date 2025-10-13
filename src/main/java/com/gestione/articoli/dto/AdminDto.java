package com.gestione.articoli.dto;


import java.util.Set;

import com.gestione.articoli.model.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDto {

    private Long id;
    private String username;
    private Boolean activeInCompany;
    private Boolean machineUser; // sempre false
    private Set<Role> roles;     // normalmente contiene solo ADMIN
}
