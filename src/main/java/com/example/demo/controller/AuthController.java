package com.example.demo.controller;

import com.example.demo.model.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.TwoFactorAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// =========================================================================
// CONTROLADOR DE AUTENTICACIÓN Y DOBLE FACTOR (2FA)
// Maneja el flujo de login en dos pasos: credenciales + código TOTP.
// =========================================================================
@Controller
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final TwoFactorAuthService twoFactorAuthService;

    public AuthController(UsuarioRepository usuarioRepository, 
                          TwoFactorAuthService twoFactorAuthService) {
        this.usuarioRepository = usuarioRepository;
        this.twoFactorAuthService = twoFactorAuthService;
    }

    // =========================================================================
    // VISTA DE LOGIN ESTÁNDAR
    // =========================================================================
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // =========================================================================
    // VISTA DE SOLICITUD DE CÓDIGO 2FA
    // Solo accesible si hay un usuario pendiente de verificar el código.
    // =========================================================================
    @GetMapping("/login-2fa")
    public String mostrarPantalla2fa(HttpSession session) {
        if (session.getAttribute("usuario_pendiente_2fa") == null) {
            return "redirect:/login";
        }
        return "login-2fa";
    }

    // =========================================================================
    // VALIDACIÓN DEL CÓDIGO TOTP Y FINALIZACIÓN DEL LOGIN
    // =========================================================================
    @PostMapping("/verificar-2fa")
    public String verificarCodigo2fa(@RequestParam int codigo,
                                     HttpServletRequest request,
                                     Model model) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("usuario_pendiente_2fa");

        // Si no hay sesión pendiente, redirigir al login inicial
        if (userId == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findById(userId).orElse(null);

        // Validar código contra la clave secreta almacenada
        if (usuario != null && twoFactorAuthService.validarCodigo(usuario, codigo)) {
            // Código correcto: limpiar bandera de pendiente
            session.removeAttribute("usuario_pendiente_2fa");
            // Spring Security ya autenticó al usuario en el primer paso,
            // así que la sesión ya es válida. Solo redirigimos al home.
            return "redirect:/home";
        }

        // Código incorrecto: mostrar error y quedarse en la misma pantalla
        model.addAttribute("error", "El código es incorrecto o ha expirado. Inténtalo de nuevo.");
        return "login-2fa";
    }
    // =========================================================================
    // CANCELAR EL PROCESO DE 2FA Y VOLVER AL LOGIN
    // Limpia la sesión pendiente y redirige al login sin errores.
    // =========================================================================
    @GetMapping("/cancelar-2fa")
    public String cancelar2fa(HttpSession session) {
        session.removeAttribute("usuario_pendiente_2fa");
        session.invalidate(); // Invalida toda la sesión para limpiar cualquier rastro
        return "redirect:/login";
    }
}