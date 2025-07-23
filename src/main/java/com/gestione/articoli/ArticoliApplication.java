package com.gestione.articoli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.gestione.articoli")
public class ArticoliApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticoliApplication.class, args);
    }
}


