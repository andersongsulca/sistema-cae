package com.example.demo.controller;

import com.example.demo.model.entity.MenuSemanal;
import com.example.demo.service.MenuSemanalService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/menu")
@PreAuthorize("hasAuthority('ROLE_COCINERA')")
public class MenuSemanalController {

    private final MenuSemanalService menuService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MenuSemanalController(MenuSemanalService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public String listarMenus(Model model) {
        model.addAttribute("menus", menuService.listarTodos());
        return "menu/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        return "menu/formulario";
    }

    @PostMapping("/guardar")
    public String guardarMenu(@RequestParam String semanaInicio,
                             @RequestParam String semanaFin,
                             @RequestParam(required = false) String observaciones,
                             @RequestParam(required = false) String lunesDesayuno,
                             @RequestParam(required = false) String lunesAlmuerzo,
                             @RequestParam(required = false) String lunesMerienda,
                             @RequestParam(required = false) String martesDesayuno,
                             @RequestParam(required = false) String martesAlmuerzo,
                             @RequestParam(required = false) String martesMerienda,
                             @RequestParam(required = false) String miercolesDesayuno,
                             @RequestParam(required = false) String miercolesAlmuerzo,
                             @RequestParam(required = false) String miercolesMerienda,
                             @RequestParam(required = false) String juevesDesayuno,
                             @RequestParam(required = false) String juevesAlmuerzo,
                             @RequestParam(required = false) String juevesMerienda,
                             @RequestParam(required = false) String viernesDesayuno,
                             @RequestParam(required = false) String viernesAlmuerzo,
                             @RequestParam(required = false) String viernesMerienda,
                             RedirectAttributes redirectAttributes) {
        try {
            MenuSemanal menu = new MenuSemanal();
            menu.setSemanaInicio(LocalDate.parse(semanaInicio));
            menu.setSemanaFin(LocalDate.parse(semanaFin));
            menu.setObservaciones(observaciones);
            
            // Crear JSON con el menú (solo Lunes a Viernes)
            String menuJson = "{"
                + "\"lunes\":{\"desayuno\":\"" + getValue(lunesDesayuno) + "\",\"almuerzo\":\"" + getValue(lunesAlmuerzo) + "\",\"merienda\":\"" + getValue(lunesMerienda) + "\"},"
                + "\"martes\":{\"desayuno\":\"" + getValue(martesDesayuno) + "\",\"almuerzo\":\"" + getValue(martesAlmuerzo) + "\",\"merienda\":\"" + getValue(martesMerienda) + "\"},"
                + "\"miercoles\":{\"desayuno\":\"" + getValue(miercolesDesayuno) + "\",\"almuerzo\":\"" + getValue(miercolesAlmuerzo) + "\",\"merienda\":\"" + getValue(miercolesMerienda) + "\"},"
                + "\"jueves\":{\"desayuno\":\"" + getValue(juevesDesayuno) + "\",\"almuerzo\":\"" + getValue(juevesAlmuerzo) + "\",\"merienda\":\"" + getValue(juevesMerienda) + "\"},"
                + "\"viernes\":{\"desayuno\":\"" + getValue(viernesDesayuno) + "\",\"almuerzo\":\"" + getValue(viernesAlmuerzo) + "\",\"merienda\":\"" + getValue(viernesMerienda) + "\"}"
            + "}";
            
            menu.setMenuJson(menuJson);
            menuService.crearMenu(menu);
            
            redirectAttributes.addFlashAttribute("success", "Menú semanal registrado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/menu";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        MenuSemanal menu = menuService.buscarPorId(id);
        model.addAttribute("menu", menu);
        
        // Parsear el JSON para mostrarlo en la vista
        if (menu.getMenuJson() != null && !menu.getMenuJson().isEmpty()) {
            try {
                JsonNode json = objectMapper.readTree(menu.getMenuJson());
                
                // Lunes
                model.addAttribute("lunesDesayuno", getValor(json, "lunes", "desayuno"));
                model.addAttribute("lunesAlmuerzo", getValor(json, "lunes", "almuerzo"));
                model.addAttribute("lunesMerienda", getValor(json, "lunes", "merienda"));
                
                // Martes
                model.addAttribute("martesDesayuno", getValor(json, "martes", "desayuno"));
                model.addAttribute("martesAlmuerzo", getValor(json, "martes", "almuerzo"));
                model.addAttribute("martesMerienda", getValor(json, "martes", "merienda"));
                
                // Miércoles
                model.addAttribute("miercolesDesayuno", getValor(json, "miercoles", "desayuno"));
                model.addAttribute("miercolesAlmuerzo", getValor(json, "miercoles", "almuerzo"));
                model.addAttribute("miercolesMerienda", getValor(json, "miercoles", "merienda"));
                
                // Jueves
                model.addAttribute("juevesDesayuno", getValor(json, "jueves", "desayuno"));
                model.addAttribute("juevesAlmuerzo", getValor(json, "jueves", "almuerzo"));
                model.addAttribute("juevesMerienda", getValor(json, "jueves", "merienda"));
                
                // Viernes
                model.addAttribute("viernesDesayuno", getValor(json, "viernes", "desayuno"));
                model.addAttribute("viernesAlmuerzo", getValor(json, "viernes", "almuerzo"));
                model.addAttribute("viernesMerienda", getValor(json, "viernes", "merienda"));
                
            } catch (Exception e) {
                e.printStackTrace();
                // Si hay error al parsear, poner valores por defecto
                model.addAttribute("lunesDesayuno", "Error al cargar");
                model.addAttribute("lunesAlmuerzo", "Error al cargar");
                model.addAttribute("lunesMerienda", "Error al cargar");
                model.addAttribute("martesDesayuno", "Error al cargar");
                model.addAttribute("martesAlmuerzo", "Error al cargar");
                model.addAttribute("martesMerienda", "Error al cargar");
                model.addAttribute("miercolesDesayuno", "Error al cargar");
                model.addAttribute("miercolesAlmuerzo", "Error al cargar");
                model.addAttribute("miercolesMerienda", "Error al cargar");
                model.addAttribute("juevesDesayuno", "Error al cargar");
                model.addAttribute("juevesAlmuerzo", "Error al cargar");
                model.addAttribute("juevesMerienda", "Error al cargar");
                model.addAttribute("viernesDesayuno", "Error al cargar");
                model.addAttribute("viernesAlmuerzo", "Error al cargar");
                model.addAttribute("viernesMerienda", "Error al cargar");
            }
        } else {
            // Si no hay JSON, poner "No registrado"
            model.addAttribute("lunesDesayuno", "No registrado");
            model.addAttribute("lunesAlmuerzo", "No registrado");
            model.addAttribute("lunesMerienda", "No registrado");
            model.addAttribute("martesDesayuno", "No registrado");
            model.addAttribute("martesAlmuerzo", "No registrado");
            model.addAttribute("martesMerienda", "No registrado");
            model.addAttribute("miercolesDesayuno", "No registrado");
            model.addAttribute("miercolesAlmuerzo", "No registrado");
            model.addAttribute("miercolesMerienda", "No registrado");
            model.addAttribute("juevesDesayuno", "No registrado");
            model.addAttribute("juevesAlmuerzo", "No registrado");
            model.addAttribute("juevesMerienda", "No registrado");
            model.addAttribute("viernesDesayuno", "No registrado");
            model.addAttribute("viernesAlmuerzo", "No registrado");
            model.addAttribute("viernesMerienda", "No registrado");
        }
        
        return "menu/detalle";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarMenu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            menuService.eliminarMenu(id);
            redirectAttributes.addFlashAttribute("success", "Menú eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/menu";
    }

    // Método helper para obtener valores del JSON
    private String getValor(JsonNode json, String dia, String comida) {
        try {
            JsonNode diaNode = json.get(dia);
            if (diaNode != null && diaNode.has(comida)) {
                String valor = diaNode.get(comida).asText();
                if (valor != null && !valor.isEmpty()) {
                    return valor;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No registrado";
    }

    // Método helper para evitar valores nulos en el JSON
    private String getValue(String value) {
        return value != null ? value : "";
    }
}