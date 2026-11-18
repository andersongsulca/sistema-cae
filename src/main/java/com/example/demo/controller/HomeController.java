package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Authentication authentication) {
        // Redireccionar al home si el usuario ya esta autenticado, evitando bucles en la raiz
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails userDetails, 
                      HttpSession session,
                      Model model) {
        // Validacion de seguridad: si no hay usuario autenticado, redirigir al login
        if (userDetails == null) {
            System.out.println("Acceso denegado a /home: Usuario no autenticado");
            return "redirect:/login";
        }
        
        // Registro de auditoria basica para nuevas sesiones
        if (session.isNew()) {
            System.out.println("Nueva sesion creada para el usuario: " + userDetails.getUsername());
        }
        
        // Agregar informacion de sesion a la vista para posible depuracion o muestra
        model.addAttribute("sessionId", session.getId());
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("lastActivity", session.getAttribute("lastActivity"));
        
        // Registro de acceso exitoso en consola
        System.out.println("Usuario '" + userDetails.getUsername() + "' accedio al home. Sesion ID: " + session.getId());
        
        return "home";
    }
    
    // Endpoint auxiliar para verificar el estado de la sesion (util para peticiones AJAX o validaciones frontend)
    @GetMapping("/api/session/status")
    public org.springframework.http.ResponseEntity<?> checkSessionStatus(HttpSession session) {
        if (session == null || session.getAttribute("SPRING_SECURITY_CONTEXT") == null) {
            return org.springframework.http.ResponseEntity.status(401).body("No authenticated");
        }
        return org.springframework.http.ResponseEntity.ok("Authenticated");
    }
}