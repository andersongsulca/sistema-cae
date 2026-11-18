package com.example.demo.controller;

import com.example.demo.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

// =========================================================================
// CONTROLADOR DEL DASHBOARD PRINCIPAL
// Centraliza la vista de estadísticas y métricas generales del sistema.
// =========================================================================
@Controller
@RequestMapping("/dashboard")

// =========================================================================
// 🛡️ SEGURIDAD: CONTROL DE ACCESO BASADO EN ROLES (RBAC)
// - Defensa en profundidad: @PreAuthorize actúa como barrera a nivel de método/clase.
// - Restringe el acceso exclusivamente a DIRECTORA y SECRETARIA, evitando que 
//   roles con menores privilegios (ej. COCINERA) puedan acceder a métricas sensibles 
//   o globales del sistema, mitigando riesgos de fuga de información.
// =========================================================================
@PreAuthorize("hasAnyAuthority('ROLE_DIRECTORA', 'ROLE_SECRETARIA')")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public String mostrarDashboard(Model model) {
        // Obtiene y agrega las estadísticas generales, de inventario y de solicitudes
        Map<String, Object> estadisticas = dashboardService.obtenerEstadisticasGenerales();
        Map<String, Object> inventarioStats = dashboardService.obtenerEstadisticasInventario();
        Map<String, Object> solicitudesStats = dashboardService.obtenerEstadisticasSolicitudes();
        
        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("inventarioStats", inventarioStats);
        model.addAttribute("solicitudesStats", solicitudesStats);
        
        return "dashboard/index";
    }
}