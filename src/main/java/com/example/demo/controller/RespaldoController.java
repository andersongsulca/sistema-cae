package com.example.demo.controller;

import com.example.demo.service.RespaldoService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;

// Controlador de respaldos. Gestión exclusiva de la Directora.
@Controller
@RequestMapping("/respaldos")

// 🛡️ SEGURIDAD: Restringe todas las operaciones de respaldo solo a la Directora.
@PreAuthorize("hasAuthority('ROLE_DIRECTORA')")
public class RespaldoController {

    private final RespaldoService respaldoService;

    public RespaldoController(RespaldoService respaldoService) {
        this.respaldoService = respaldoService;
    }

    // Lista los archivos de respaldo disponibles.
    @GetMapping
    public String listarRespaldos(Model model) {
        model.addAttribute("respaldos", respaldoService.listarRespaldos());
        return "respaldos/listar";
    }

    // Genera un respaldo manual de la base de datos.
    @PostMapping("/generar")
    public String generarRespaldoManual(RedirectAttributes redirectAttributes) {
        try {
            String archivo = respaldoService.generarRespaldoManual();
            redirectAttributes.addFlashAttribute("success", "Respaldo generado correctamente: " + archivo);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al generar respaldo: " + e.getMessage());
        }
        return "redirect:/respaldos";
    }

    // Descarga un archivo de respaldo.
    // ⚠️ SEGURIDAD/MANTENIMIENTO: Validar en el servicio que 'nombreArchivo' no contenga "../" 
    // para evitar vulnerabilidades de Path Traversal (lectura de archivos fuera de la carpeta).
    @GetMapping("/descargar/{nombreArchivo}")
    public ResponseEntity<Resource> descargarRespaldo(@PathVariable String nombreArchivo) {
        try {
            Path path = Paths.get("respaldos/" + nombreArchivo);
            Resource resource = new UrlResource(path.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/json"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Elimina un archivo de respaldo específico.
    // ⚠️ SEGURIDAD/MANTENIMIENTO: Igual que arriba, asegurar saneamiento del nombre de archivo.
    @PostMapping("/eliminar/{nombreArchivo}")
    public String eliminarRespaldo(@PathVariable String nombreArchivo, 
                                   RedirectAttributes redirectAttributes) {
        try {
            respaldoService.eliminarRespaldo(nombreArchivo);
            redirectAttributes.addFlashAttribute("success", "Respaldo eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/respaldos";
    }
}