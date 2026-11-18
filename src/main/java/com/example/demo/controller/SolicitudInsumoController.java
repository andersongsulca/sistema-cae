package com.example.demo.controller;

import com.example.demo.model.entity.SolicitudInsumo;
import com.example.demo.model.entity.DetalleSolicitudInsumo;
import com.example.demo.model.entity.Producto;
import com.example.demo.model.entity.Usuario;
import com.example.demo.service.SolicitudInsumoService;
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

// Controlador de Solicitudes de Insumos. Maneja el flujo de pedido y aprobación.
@Controller
@RequestMapping("/solicitudes")
public class SolicitudInsumoController {

    private final SolicitudInsumoService solicitudService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public SolicitudInsumoController(SolicitudInsumoService solicitudService,
                                    ProductoService productoService,
                                    UsuarioService usuarioService) {
        this.solicitudService = solicitudService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    // 🛡️ SEGURIDAD: Solo Secretaria y Directora pueden ver todas las solicitudes.
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SECRETARIA', 'ROLE_DIRECTORA')")
    public String listarSolicitudes(Model model) {
        model.addAttribute("solicitudes", solicitudService.listarTodas());
        return "solicitudes/listar";
    }

    // 🛡️ SEGURIDAD: Solo la Cocinera puede acceder al formulario de nueva solicitud.
    @GetMapping("/nueva")
    @PreAuthorize("hasAuthority('ROLE_COCINERA')")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "solicitudes/formulario";
    }

    // Guarda la solicitud. 
    // ✅ BUENA PRÁCTICA: Obtiene el usuario desde `Authentication`, evitando manipulación del ID en el formulario.
    // ⚠️ MANTENIMIENTO: Evitar `e.printStackTrace()` y `e.getMessage()` en producción. Usar un Logger y mensajes genéricos.
    @PostMapping("/guardar")
    @PreAuthorize("hasAuthority('ROLE_COCINERA')")
    public String guardarSolicitud(@RequestParam String fechaSolicitud,
                                  @RequestParam(required = false) String observaciones,
                                  @RequestParam(required = false) String prioridad,
                                  @RequestParam("productoIds") Long[] productoIds,
                                  @RequestParam("cantidades") Double[] cantidades,
                                  @RequestParam(value = "unidades", required = false) String[] unidades,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            Usuario usuario = usuarioService.buscarPorUsername(username);
            
            SolicitudInsumo solicitud = new SolicitudInsumo();
            solicitud.setFechaSolicitud(LocalDate.parse(fechaSolicitud));
            solicitud.setObservaciones(observaciones);
            solicitud.setPrioridad(prioridad != null ? prioridad : "NORMAL");
            solicitud.setCocineraId(usuario.getId());
            solicitud.setCocineraUsername(username);
            
            List<DetalleSolicitudInsumo> detalles = new ArrayList<>();
            for (int i = 0; i < productoIds.length; i++) {
                if (productoIds[i] != null && cantidades[i] != null && cantidades[i] > 0) {
                    DetalleSolicitudInsumo detalle = new DetalleSolicitudInsumo();
                    Producto producto = productoService.buscarPorId(productoIds[i]);
                    detalle.setProducto(producto);
                    detalle.setCantidadSolicitada(java.math.BigDecimal.valueOf(cantidades[i]));
                    detalle.setUnidad(unidades != null && unidades.length > i ? unidades[i] : producto.getUnidadMedida());
                    detalle.setSolicitud(solicitud);
                    detalles.add(detalle);
                }
            }
            
            solicitud.setDetalles(detalles);
            solicitudService.crearSolicitud(solicitud);
            
            redirectAttributes.addFlashAttribute("success", "Solicitud enviada correctamente");
        } catch (Exception e) {
            e.printStackTrace(); // ⚠️ Reemplazar por log.error("Error al guardar", e);
            redirectAttributes.addFlashAttribute("error", "Error al procesar la solicitud."); // Mensaje genérico
        }
        return "redirect:/solicitudes/mis-solicitudes";
    }

    // 🛡️ SEGURIDAD: La Cocinera solo puede ver sus propias solicitudes.
    @GetMapping("/mis-solicitudes")
    @PreAuthorize("hasAuthority('ROLE_COCINERA')")
    public String misSolicitudes(Model model, Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username);
        model.addAttribute("solicitudes", solicitudService.listarPorCocineraId(usuario.getId()));
        return "solicitudes/mis-solicitudes";
    }

    // 🛡️ SEGURIDAD: Solo Secretaria y Directora pueden ver el detalle de cualquier solicitud.
    @GetMapping("/{id}/ver")
    @PreAuthorize("hasAnyAuthority('ROLE_SECRETARIA', 'ROLE_DIRECTORA')")
    public String verDetalleSolicitud(@PathVariable Long id, Model model) {
        SolicitudInsumo solicitud = solicitudService.buscarPorId(id);
        model.addAttribute("solicitud", solicitud);
        return "solicitudes/detalle";
    }

    // 🛡️ SEGURIDAD: Solo Secretaria y Directora pueden aprobar solicitudes.
    @PostMapping("/aprobar/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SECRETARIA', 'ROLE_DIRECTORA')")
    public String aprobarSolicitud(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            solicitudService.aprobarSolicitud(id);
            redirectAttributes.addFlashAttribute("success", "Solicitud aprobada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al aprobar la solicitud.");
        }
        return "redirect:/solicitudes";
    }

    // 🛡️ SEGURIDAD: Solo Secretaria y Directora pueden rechazar solicitudes.
    @PostMapping("/rechazar/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SECRETARIA', 'ROLE_DIRECTORA')")
    public String rechazarSolicitud(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            solicitudService.rechazarSolicitud(id);
            redirectAttributes.addFlashAttribute("success", "Solicitud rechazada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al rechazar la solicitud.");
        }
        return "redirect:/solicitudes";
    }
}