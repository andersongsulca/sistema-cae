package com.example.demo.security;

import com.example.demo.model.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

// =========================================================================
// MANEJADOR DE ÉXITO DE AUTENTICACIÓN CON SOPORTE 2FA
// Se ejecuta DESPUÉS de que Spring Security valida usuario y contraseña.
// Si el usuario tiene 2FA activado, lo redirige a la pantalla del código.
// Si no, lo deja pasar al home normalmente.
// =========================================================================
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;

    public CustomAuthenticationSuccessHandler(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        String username = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        // Si el usuario existe y tiene 2FA activado, guardamos su ID en la sesión
        // y lo redirigimos a la pantalla de verificación de 2FA
        if (usuarioOpt.isPresent() && Boolean.TRUE.equals(usuarioOpt.get().getIs2faEnabled())) {
            HttpSession session = request.getSession();
            session.setAttribute("usuario_pendiente_2fa", usuarioOpt.get().getId());
            response.sendRedirect("/login-2fa");
            return;
        }

        // Si NO tiene 2FA activado, lo mandamos al home normalmente
        response.sendRedirect("/home");
    }
}