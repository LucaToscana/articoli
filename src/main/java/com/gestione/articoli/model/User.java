package com.gestione.articoli.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private boolean activeInCompany = true;
    @Column(nullable = false)
    @Builder.Default
    private boolean machineUser = false;
    
    
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal retribuzioneOraria =  BigDecimal.ZERO;
    


    private String machineName;
    
}
