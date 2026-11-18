package com.example.demo.controller;

import com.example.demo.model.entity.Recepcion;
import com.example.demo.model.entity.DetalleRecepcion;
import com.example.demo.model.entity.Producto;
import com.example.demo.model.entity.Proveedor;
import com.example.demo.service.RecepcionService;
import com.example.demo.service.ProductoService;
import com.example.demo.service.ProveedorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Controlador de Recepciones. Gestiona el ingreso de mercancía y sus detalles.
@Controller
@RequestMapping("/recepciones")
// Restringe el acceso exclusivamente a Secretaria y Directora.
@PreAuthorize("hasAnyAuthority('ROLE_SECRETARIA', 'ROLE_DIRECTORA')")
public class RecepcionController {

    private final RecepcionService recepcionService;
    private final ProductoService productoService;
    private final ProveedorService proveedorService;

    public RecepcionController(RecepcionService recepcionService,
                              ProductoService productoService,
                              ProveedorService proveedorService) {
        this.recepcionService = recepcionService;
        this.productoService = productoService;
        this.proveedorService = proveedorService;
    }

    // Lista todas las recepciones registradas.
    @GetMapping
    public String listarRecepciones(Model model) {
        model.addAttribute("recepciones", recepcionService.listarTodas());
        return "recepciones/listar";
    }

    // Muestra el formulario para crear una nueva recepción.
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("proveedores", proveedorService.listarTodos());
        model.addAttribute("productos", productoService.listarTodos());
        return "recepciones/formulario";
    }

    // Guarda la nueva recepción y sus detalles en la base de datos.
    // Nota mantenimiento: El ID de usuario (1L) está fijo, debería obtenerse de la sesión activa.
    @PostMapping("/guardar")
    public String guardarRecepcion(@RequestParam String fecha,
                                  @RequestParam(required = false) String numeroRemision,
                                  @RequestParam Long proveedorId,
                                  @RequestParam(required = false) String observaciones,
                                  @RequestParam("productoIds") Long[] productoIds,
                                  @RequestParam("cantidades") Double[] cantidades,
                                  @RequestParam(value = "lotes", required = false) String[] lotes,
                                  @RequestParam("fechasVencimiento") String[] fechasVencimiento,
                                  RedirectAttributes redirectAttributes) {
        try {
            Recepcion recepcion = new Recepcion();
            recepcion.setFecha(LocalDate.parse(fecha));
            recepcion.setNumeroRemision(numeroRemision);
            recepcion.setObservaciones(observaciones);
            
            Proveedor proveedor = proveedorService.buscarPorId(proveedorId);
            recepcion.setProveedor(proveedor);
            
            List<DetalleRecepcion> detalles = new ArrayList<>();
            for (int i = 0; i < productoIds.length; i++) {
                if (productoIds[i] != null && cantidades[i] != null) {
                    DetalleRecepcion detalle = new DetalleRecepcion();
                    Producto producto = productoService.buscarPorId(productoIds[i]);
                    detalle.setProducto(producto);
                    detalle.setCantidad(java.math.BigDecimal.valueOf(cantidades[i]));
                    detalle.setLote(lotes != null && lotes.length > i ? lotes[i] : null);
                    detalle.setFechaVencimiento(LocalDate.parse(fechasVencimiento[i]));
                    detalle.setRecepcion(recepcion);
                    detalles.add(detalle);
                }
            }
            
            recepcion.setDetalles(detalles);
            recepcionService.crearRecepcion(recepcion, 1L); 
            
            redirectAttributes.addFlashAttribute("success", "Recepción registrada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/recepciones";
    }

    // Muestra el detalle de una recepción específica.
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        model.addAttribute("recepcion", recepcionService.buscarPorId(id));
        return "recepciones/detalle";
    }
}