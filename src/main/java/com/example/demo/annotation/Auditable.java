package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Anotación personalizada para registrar auditorías.
// Se aplica a métodos para indicar qué acción se realiza y en qué tabla.
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    String accion(); // Describe la operación (ej. "INSERTAR", "ACTUALIZAR")
    String tabla();  // Indica la entidad o tabla afectada
}