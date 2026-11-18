package com.example.demo.controller;

import com.example.demo.model.entity.SalidaInsumo;
import com.example.demo.model.entity.DetalleSalida;
import com.example.demo.model.entity.Producto;
import com.example.demo.model.entity.Usuario;
import com.example.demo.service.SalidaInsumoService;
import com.example.demo.service.ProductoService;
import com.example.demo.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/salidas")
@PreAuthorize("hasAnyAuthority('ROLE_SECRETARIA', 'ROLE_DIRECTORA')")
public class SalidaInsumoController {

    private final SalidaInsumoService salidaService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public SalidaInsumoController(SalidaInsumoService salidaService,
                                 ProductoService productoService,
                                 UsuarioService usuarioService) {
        this.salidaService = salidaService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarSalidas(Model model) {
        model.addAttribute("salidas", salidaService.listarTodas());
        return "salidas/listar";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "salidas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarSalida(@RequestParam String fecha,
                               @RequestParam String destino,
                               @RequestParam(required = false) String observaciones,
                               @RequestParam("productoIds") Long[] productoIds,
                               @RequestParam("cantidades") Double[] cantidades,
                               @RequestParam(value = "lotes", required = false) String[] lotes,
                               @RequestParam(value = "fechasVencimiento", required = false) String[] fechasVencimiento,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            // Obtener usuario actual (secretaria)
            String username = authentication.getName();
            Usuario usuario = usuarioService.buscarPorUsername(username);
            
            SalidaInsumo salida = new SalidaInsumo();
            salida.setFecha(LocalDate.parse(fecha));
            salida.setDestino(destino);
            salida.setObservaciones(observaciones);
            salida.setUsuario(usuario);  // ← IMPORTANTE: Setear el usuario
            
            // Crear detalles
            List<DetalleSalida> detalles = new ArrayList<>();
            for (int i = 0; i < productoIds.length; i++) {
                if (productoIds[i] != null && cantidades[i] != null && cantidades[i] > 0) {
                    DetalleSalida detalle = new DetalleSalida();
                    Producto producto = productoService.buscarPorId(productoIds[i]);
                    
                    // Validar stock
                    if (producto.getStockActual().doubleValue() < cantidades[i]) {
                        redirectAttributes.addFlashAttribute("error", 
                            "Stock insuficiente para: " + producto.getNombre());
                        return "redirect:/salidas/nueva";
                    }
                    
                    detalle.setProducto(producto);
                    detalle.setCantidad(java.math.BigDecimal.valueOf(cantidades[i]));
                    detalle.setLote(lotes != null && lotes.length > i ? lotes[i] : null);
                    if (fechasVencimiento != null && fechasVencimiento.length > i && 
                        fechasVencimiento[i] != null && !fechasVencimiento[i].isEmpty()) {
                        detalle.setFechaVencimiento(LocalDate.parse(fechasVencimiento[i]));
                    }
                    detalle.setSalida(salida);
                    detalles.add(detalle);
                }
            }
            
            salida.setDetalles(detalles);
            salidaService.crearSalida(salida, usuario.getId());
            
            redirectAttributes.addFlashAttribute("success", "Salida registrada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al registrar salida: " + e.getMessage());
        }
        return "redirect:/salidas";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        model.addAttribute("salida", salidaService.buscarPorId(id));
        return "salidas/detalle";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarSalida(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            salidaService.eliminarSalida(id);
            redirectAttributes.addFlashAttribute("success", "Salida eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/salidas";
    }
}