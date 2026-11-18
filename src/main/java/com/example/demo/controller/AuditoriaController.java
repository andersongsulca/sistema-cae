package com.example.demo.controller;

import com.example.demo.service.AuditoriaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auditoria")

// =========================================================================
// 🛡️ SEGURIDAD: CONTROL DE ACCESO ESTRICTO (RBAC)
// - Defensa en profundidad: @PreAuthorize restringe el acceso a este 
//   controlador exclusivamente al rol 'ROLE_DIRECTORA'. 
// - Esto protege la confidencialidad de los registros de auditoría, 
//   evitando que roles con menores privilegios (ej. Secretaria, Cocinera) 
//   puedan visualizar o inferir acciones sensibles del sistema.
// =========================================================================
@PreAuthorize("hasAuthority('ROLE_DIRECTORA')")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String listarAuditoria(Model model) {
        // Carga todos los registros de auditoría para la vista
        model.addAttribute("registros", auditoriaService.listarTodos());
        return "auditoria/listar";
    }
}