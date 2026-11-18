package com.example.demo.controller;

import com.example.demo.service.MovimientoInventarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// =========================================================================
// CONTROLADOR DE MOVIMIENTOS DE INVENTARIO
// Gestiona la visualización de los registros de entradas y salidas de almacén.
// =========================================================================
@Controller
@RequestMapping("/movimientos")

// =========================================================================
// 🛡️ SEGURIDAD: CONTROL DE ACCESO (RBAC)
// - Defensa en profundidad: @PreAuthorize restringe la visualización de 
//   movimientos exclusivamente a SECRETARIA y DIRECTORA.
// - Esto protege la confidencialidad de los datos operativos y financieros, 
//   evitando que roles como COCINERA puedan auditar o inferir información 
//   sensible sobre el flujo de mercancía.
// =========================================================================
@PreAuthorize("hasAnyAuthority('ROLE_SECRETARIA', 'ROLE_DIRECTORA')")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoService;

    public MovimientoInventarioController(MovimientoInventarioService movimientoService) {
        this.movimientoService = movimientoService;
    }

    // Lista todos los movimientos de inventario registrados en el sistema
    @GetMapping
    public String listarMovimientos(Model model) {
        model.addAttribute("movimientos", movimientoService.listarTodos());
        return "movimientos/listar";
    }

    // Muestra un historial filtrado con los movimientos más recientes
    @GetMapping("/historial")
    public String verHistorial(Model model) {
        model.addAttribute("movimientos", movimientoService.buscarMovimientosRecientes());
        return "movimientos/historial";
    }
}