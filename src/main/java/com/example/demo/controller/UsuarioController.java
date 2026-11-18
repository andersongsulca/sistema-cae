package com.example.demo.controller;

import com.example.demo.model.entity.Usuario;
import com.example.demo.model.enums.Rol;
import com.example.demo.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasAuthority('ROLE_DIRECTORA')")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    // LISTAR USUARIOS
    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/listar";
    }

    // MOSTRAR FORMULARIO DE NUEVO USUARIO
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", Rol.values());
        return "usuarios/formulario";
    }

    // MOSTRAR FORMULARIO DE EDICIÓN
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", Rol.values());
        return "usuarios/formulario";
    }

    // GUARDAR NUEVO USUARIO - CON VALIDACIONES
    @PostMapping("/guardar")
    public String guardarUsuario(@Valid @ModelAttribute Usuario usuario,
                                 BindingResult result,
                                 @RequestParam String passwordConfirm,
                                 RedirectAttributes redirectAttributes) {
        
        // Validaciones del formulario
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos en el formulario");
            return "redirect:/usuarios/nuevo";
        }

        // Validar que las contraseñas coincidan
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La contraseña es obligatoria");
            return "redirect:/usuarios/nuevo";
        }

        if (!usuario.getPassword().equals(passwordConfirm)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/usuarios/nuevo";
        }

        // Validar longitud de contraseña
        if (usuario.getPassword().length() < 6) {
            redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
            return "redirect:/usuarios/nuevo";
        }

        try {
            // Validar que el usuario no exista
            if (usuarioService.existsByUsername(usuario.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "El nombre de usuario '" + usuario.getUsername() + "' ya existe");
                return "redirect:/usuarios/nuevo";
            }

            // Validar que el email no exista (si se proporcionó)
            if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
                if (usuarioService.existsByEmail(usuario.getEmail())) {
                    redirectAttributes.addFlashAttribute("error", "El email '" + usuario.getEmail() + "' ya está registrado");
                    return "redirect:/usuarios/nuevo";
                }
            }

            // Validar formato de email
            if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
                if (!usuario.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    redirectAttributes.addFlashAttribute("error", "El formato del email no es válido");
                    return "redirect:/usuarios/nuevo";
                }
            }

            // Encriptar contraseña
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuario.setActivo(true);
            
            usuarioService.crearUsuario(usuario);
            redirectAttributes.addFlashAttribute("success", "Usuario '" + usuario.getUsername() + "' creado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    // ACTUALIZAR USUARIO - CON VALIDACIONES
    @PostMapping("/actualizar/{id}")
    public String actualizarUsuario(@PathVariable Long id,
                                    @Valid @ModelAttribute Usuario usuario,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos en el formulario");
            return "redirect:/usuarios/editar/" + id;
        }

        try {
            Usuario usuarioExistente = usuarioService.buscarPorId(id);
            
            // Actualizar campos
            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setApellido(usuario.getApellido());
            usuarioExistente.setEmail(usuario.getEmail());
            usuarioExistente.setTelefono(usuario.getTelefono());
            usuarioExistente.setRol(usuario.getRol());
            usuarioExistente.setActivo(usuario.getActivo());
            
            // Actualizar contraseña solo si se proporcionó una nueva
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                if (usuario.getPassword().length() < 6) {
                    redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                    return "redirect:/usuarios/editar/" + id;
                }
                usuarioExistente.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
            
            usuarioService.actualizarUsuario(usuarioExistente);
            redirectAttributes.addFlashAttribute("success", "Usuario '" + usuarioExistente.getUsername() + "' actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    // ELIMINAR USUARIO
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }
}