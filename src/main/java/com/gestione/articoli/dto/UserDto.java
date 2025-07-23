package com.gestione.articoli.dto;

import lombok.*;
import java.util.Set;
import com.gestione.articoli.model.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter 
@Setter
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private Set<Role> roles;

}
