package com.example.demo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionSecurityInterceptor implements HandlerInterceptor {

    // ✅ CORRECCIÓN: Cambiado a 60,000 ms (1 minuto) para que coincida con la demo y el application.properties
    private static final long MAX_INACTIVE_TIME = 60000; 

    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // 1. VALIDAR USER-AGENT Y IP (Doble capa contra Session Hijacking)
            String currentUserAgent = request.getHeader("User-Agent");
            String sessionUserAgent = (String) session.getAttribute("userAgent");
            
            // validamos la IP para mayor seguridad
            String currentIp = getClientIp(request);
            String sessionIp = (String) session.getAttribute("userIp");

            if (sessionUserAgent != null && sessionIp != null) {
                if (!sessionUserAgent.equals(currentUserAgent) || !sessionIp.equals(currentIp)) {
                    System.out.println("⚠️ ALERTA DE SEGURIDAD: User-Agent o IP diferentes - Posible Session Hijacking");
                    session.invalidate();
                    // ✅ CORRECCIÓN: URL coincide exactamente con el param.hijacking de tu HTML
                    response.sendRedirect("/login?hijacking=true");
                    return false;
                }
            }
            
            // 2. GUARDAR USER-AGENT E IP SI NO EXISTEN (primer request después del login)
            if (sessionUserAgent == null) {
                session.setAttribute("userAgent", currentUserAgent);
                session.setAttribute("userIp", currentIp); // ✅ NUEVA RECOMENDACIÓN
                session.setAttribute("lastActivity", System.currentTimeMillis());
            }
            
            // 3. VALIDAR TIEMPO DE INACTIVIDAD
            Long lastActivity = (Long) session.getAttribute("lastActivity");
            if (lastActivity != null) {
                long tiempoInactivo = System.currentTimeMillis() - lastActivity;
                if (tiempoInactivo > MAX_INACTIVE_TIME) {
                    System.out.println("⏰ Sesión expirada por inactividad (" + (tiempoInactivo/1000) + " segundos)");
                    session.invalidate();
                    // ✅ CORRECCIÓN: URL coincide exactamente con el param.timeout de tu HTML
                    response.sendRedirect("/login?timeout=true");
                    return false;
                }
            }
            
            // 4. ACTUALIZAR ÚLTIMA ACTIVIDAD
            session.setAttribute("lastActivity", System.currentTimeMillis());
        }
        
        return true;
    }

    // ✅ NUEVA RECOMENDACIÓN: Método auxiliar para obtener la IP real del cliente (soporta proxies)
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}