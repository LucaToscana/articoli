package com.gestione.articoli.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "password")
@Data
public class PasswordProperties {
    private String operators;
}
