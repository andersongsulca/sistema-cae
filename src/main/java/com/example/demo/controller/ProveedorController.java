package com.example.demo.controller;

import com.example.demo.model.entity.Proveedor;
import com.example.demo.service.ProveedorService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// =========================================================================
// CONTROLADOR DE GESTIÓN DE PROVEEDORES
// Maneja el alta, consulta y actualización de los proveedores del sistema.
// =========================================================================
@Controller
@RequestMapping("/proveedores")

// =========================================================================
// 🛡️ SEGURIDAD: CONTROL DE ACCESO (RBAC) E INTEGRIDAD DE DATOS
// - Defensa en profundidad: @PreAuthorize restringe la gestión de proveedores
//   exclusivamente a SECRETARIA y DIRECTORA, protegiendo la base de datos 
//   de terceros contra modificaciones no autorizadas.
// - Validación de Entrada: El uso de @Valid y BindingResult asegura la 
//   integridad de los datos antes de procesarlos, mitigando riesgos de 
//   inyección de datos malformados o errores de lógica por campos inválidos.
// - ⚠️ OBSERVACIÓN: Exponer `e.getMessage()` en los flash attributes puede 
//   revelar detalles internos del sistema (Information Disclosure). Se 
//   recomienda mostrar un mensaje genérico al usuario y registrar el detalle 
//   real en los logs del servidor.
// =========================================================================
@PreAuthorize("hasAnyAuthority('ROLE_SECRETARIA', 'ROLE_DIRECTORA')")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public String listarProveedores(Model model) {
        model.addAttribute("proveedores", proveedorService.listarTodos());
        return "proveedores/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "proveedores/formulario";
    }

    @PostMapping("/guardar")
    public String guardarProveedor(@Valid @ModelAttribute Proveedor proveedor,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos. Verifique los campos.");
            return "redirect:/proveedores/nuevo";
        }
        try {
            proveedorService.crearProveedor(proveedor);
            redirectAttributes.addFlashAttribute("success", "Proveedor registrado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage()); // Ver observación de seguridad
        }
        return "redirect:/proveedores";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("proveedor", proveedorService.buscarPorId(id));
        return "proveedores/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarProveedor(@PathVariable Long id,
                                     @Valid @ModelAttribute Proveedor proveedor,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos. Verifique los campos.");
            return "redirect:/proveedores/editar/" + id;
        }
        try {
            proveedorService.actualizarProveedor(id, proveedor);
            redirectAttributes.addFlashAttribute("success", "Proveedor actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage()); // Ver observación de seguridad
        }
        return "redirect:/proveedores";
    }
}