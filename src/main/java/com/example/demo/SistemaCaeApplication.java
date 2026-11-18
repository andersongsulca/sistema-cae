package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada de la app. @SpringBootApplication habilita
// autoconfiguración y component scan. Aquí no hay lógica de negocio
// ni medidas de seguridad (eso se maneja en otras clases).
@SpringBootApplication
public class SistemaCaeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaCaeApplication.class, args);
    }

}