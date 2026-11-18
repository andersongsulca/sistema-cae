package com.example.demo.controller;

import com.example.demo.model.entity.Producto;
import com.example.demo.model.enums.EstadoProducto;
import com.example.demo.service.ProductoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// =========================================================================
// CONTROLADOR DE GESTIÓN DE PRODUCTOS
// Maneja el CRUD (Crear, Leer, Actualizar, Eliminar) de los productos.
// =========================================================================
@Controller
@RequestMapping("/productos")

// =========================================================================
// 🛡️ SEGURIDAD: CONTROL DE ACCESO (RBAC) Y MANEJO DE ERRORES
// - Defensa en profundidad: @PreAuthorize restringe la gestión de productos
//   exclusivamente a SECRETARIA y DIRECTORA, protegiendo la integridad del
//   catálogo de inventario contra modificaciones no autorizadas.
// - ⚠️ OBSERVACIÓN: Exponer `e.getMessage()` en los flash attributes puede
//   revelar detalles internos de la base de datos o framework (Information
//   Disclosure). Se recomienda mostrar un mensaje genérico al usuario y
//   registrar el detalle real en los logs del servidor.
// =========================================================================
@PreAuthorize("hasAnyAuthority('ROLE_SECRETARIA', 'ROLE_DIRECTORA')")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "productos/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("estados", EstadoProducto.values());
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto, RedirectAttributes redirectAttributes) {
        try {
            productoService.crearProducto(producto);
            redirectAttributes.addFlashAttribute("success", "Producto registrado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage()); // Ver observación de seguridad
        }
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id));
        model.addAttribute("estados", EstadoProducto.values());
        return "productos/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarProducto(@PathVariable Long id, @ModelAttribute Producto producto,
                                    RedirectAttributes redirectAttributes) {
        try {
            productoService.actualizarProducto(id, producto);
            redirectAttributes.addFlashAttribute("success", "Producto actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage()); // Ver observación de seguridad
        }
        return "redirect:/productos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminarProducto(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage()); // Ver observación de seguridad
        }
        return "redirect:/productos";
    }
}