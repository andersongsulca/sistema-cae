package com.example.demo.controller;

import com.example.demo.service.InventarioService;
import com.example.demo.service.ProductoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

// =========================================================================
// CONTROLADOR DE GESTIÓN DE INVENTARIO
// Maneja la consulta, ingreso y salida de productos del almacén.
// =========================================================================
@Controller
@RequestMapping("/inventario")

// =========================================================================
// 🛡️ SEGURIDAD: CONTROL DE ACCESO (RBAC)
// - Defensa en profundidad: @PreAuthorize restringe el acceso a roles 
//   autorizados, protegiendo la integridad del inventario contra 
//   modificaciones o consultas no autorizadas.
// =========================================================================
@PreAuthorize("hasAnyAuthority('ROLE_SECRETARIA', 'ROLE_DIRECTORA', 'ROLE_COCINERA')")
public class InventarioController {

    private final ProductoService productoService;
    private final InventarioService inventarioService;

    public InventarioController(ProductoService productoService, InventarioService inventarioService) {
        this.productoService = productoService;
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public String consultarInventario(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("productosCriticos", productoService.obtenerProductosCriticos());
        return "inventario/consultar";
    }

    // =========================================================================
    // 🛡️ SEGURIDAD: INTEGRIDAD DE TRANSACCIONES Y NO REPUDIO
    // - ⚠️ OBSERVACIÓN CRÍTICA: El valor '1L' pasado como último parámetro 
    //   (probablemente ID de usuario) está hardcodeado. Para garantizar la 
    //   trazabilidad y el no repudio, este dato debe obtenerse de la sesión 
    //   activa del usuario autenticado (ej. SecurityContextHolder), nunca de 
    //   un valor fijo o enviado desde el cliente.
    // - Manejo de Errores: Exponer `e.getMessage()` en el flash attribute 
    //   puede revelar detalles internos del sistema (Information Disclosure).
    // =========================================================================
    @PostMapping("/ingresar/{productoId}")
    public String registrarIngreso(@PathVariable Long productoId,
                                   @RequestParam BigDecimal cantidad,
                                   @RequestParam String referencia,
                                   RedirectAttributes redirectAttributes) {
        try {
            inventarioService.registrarIngreso(productoId, cantidad, referencia, 1L);
            redirectAttributes.addFlashAttribute("success", "Ingreso registrado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/inventario";
    }

    @PostMapping("/salir/{productoId}")
    public String registrarSalida(@PathVariable Long productoId,
                                  @RequestParam BigDecimal cantidad,
                                  @RequestParam String referencia,
                                  RedirectAttributes redirectAttributes) {
        try {
            inventarioService.registrarSalida(productoId, cantidad, referencia, 1L);
            redirectAttributes.addFlashAttribute("success", "Salida registrada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/inventario";
    }
}