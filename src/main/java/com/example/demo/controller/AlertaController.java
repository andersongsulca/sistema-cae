package com.example.demo.controller;

import com.example.demo.service.AlertaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Controlador para la gestión de alertas del sistema
@Controller
@RequestMapping("/alertas")

// =========================================================================
// 🛡️ SEGURIDAD: CONTROL DE ACCESO Y MANEJO DE ERRORES
// - Defensa en profundidad: @PreAuthorize asegura el RBAC a nivel de método,
//   actuando como una segunda barrera de seguridad además del SecurityFilterChain.
// - ⚠️ Observación: Exponer `e.getMessage()` en el flash attribute puede revelar 
//   detalles internos del sistema (Information Disclosure). Se recomienda usar 
//   mensajes genéricos de error para el usuario y registrar el detalle real en logs.
// =========================================================================
@PreAuthorize("hasAnyAuthority('ROLE_SECRETARIA', 'ROLE_DIRECTORA')")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    public String listarAlertas(Model model) {
        model.addAttribute("alertas", alertaService.listarTodas());
        return "alertas/listar";
    }

    @PostMapping("/marcar-leida/{id}")
    public String marcarComoLeida(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            alertaService.marcarComoLeida(id);
            redirectAttributes.addFlashAttribute("success", "Alerta marcada como leída");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage()); // Ver observación de seguridad
        }
        return "redirect:/alertas";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarAlerta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            alertaService.eliminarAlerta(id);
            redirectAttributes.addFlashAttribute("success", "Alerta eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage()); // Ver observación de seguridad
        }
        return "redirect:/alertas";
    }

    @PostMapping("/eliminar-todas")
    public String eliminarTodasLasAlertas(RedirectAttributes redirectAttributes) {
        try {
            alertaService.eliminarTodas();
            redirectAttributes.addFlashAttribute("success", "Todas las alertas fueron eliminadas");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage()); // Ver observación de seguridad
        }
        return "redirect:/alertas";
    }
}