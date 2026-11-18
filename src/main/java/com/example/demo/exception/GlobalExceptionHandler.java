package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Manejo de validaciones de formularios
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("errors", errors);
        response.put("message", "Error de validación");
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Recurso no encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", ex.getMessage());
        response.put("error", "Recurso no encontrado");
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Producto duplicado
    @ExceptionHandler(ProductoDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleProductoDuplicado(
            ProductoDuplicadoException ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", ex.getMessage());
        response.put("error", "Conflicto - Recurso duplicado");
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Stock insuficiente
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleStockInsuficiente(
            StockInsuficienteException ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());
        response.put("error", "Stock insuficiente");
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Acceso denegado
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(
            AccessDeniedException ex, 
            RedirectAttributes redirectAttributes) {
        
        redirectAttributes.addFlashAttribute("error", "No tiene permisos para realizar esta acción");
        return "redirect:/home";
    }

    // Error general
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", "Ocurrió un error inesperado: " + ex.getMessage());
        response.put("error", "Error interno del servidor");
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}