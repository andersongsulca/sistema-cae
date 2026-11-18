package com.example.demo.config;

import com.example.demo.security.SessionSecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Configuration
public class webConfig implements WebMvcConfigurer {

    @Autowired
    private SessionSecurityInterceptor sessionSecurityInterceptor;

    @Value("${server.servlet.session.timeout}")
    private String sessionTimeout;

    // Convierte formatos como "15m" o "30s" a segundos enteros
    private int getTimeoutInSeconds() {
        if (sessionTimeout.endsWith("m")) {
            return Integer.parseInt(sessionTimeout.replace("m", "")) * 60;
        } else if (sessionTimeout.endsWith("s")) {
            return Integer.parseInt(sessionTimeout.replace("s", ""));
        } else if (sessionTimeout.endsWith("h")) {
            return Integer.parseInt(sessionTimeout.replace("h", "")) * 3600;
        }
        return 900; // Default 15 minutos
    }

    // =========================================================================
    // 🛡️ SEGURIDAD: GESTIÓN DE SESIONES Y PROTECCIÓN CONTRA INACTIVIDAD
    // - Timeout Dinámico: Fuerza el cierre de sesión tras inactividad (configurable),
    //   mitigando riesgos de acceso no autorizado en pantallas desatendidas.
    // - Interceptor Global: Aplica validación de sesión en todas las rutas protegidas.
    // - Exclusiones Seguras: Excluir "/error" previene bucles infinitos de redirección
    //   si la sesión expira justo durante el manejo de una excepción.
    // =========================================================================
    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> sessionListener() {
        ServletListenerRegistrationBean<HttpSessionListener> registrationBean = 
            new ServletListenerRegistrationBean<>();
            
        registrationBean.setListener(new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {
                int timeout = getTimeoutInSeconds();
                System.out.println("=== NUEVA SESION CREADA ===");
                System.out.println("ID: " + se.getSession().getId());
                System.out.println("Timeout configurado: " + timeout + " segundos (" + (timeout/60) + " minutos)");
                
                se.getSession().setMaxInactiveInterval(timeout);
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
                System.out.println("=== SESION DESTRUIDA ===");
                System.out.println("ID: " + se.getSession().getId());
            }
        });
        
        return registrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionSecurityInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/error", "/css/**", "/js/**", "/imagen/**", "/webjars/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/login");
        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/imagen/**").addResourceLocations("classpath:/static/imagen/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/");
    }
}