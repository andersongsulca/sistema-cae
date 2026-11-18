package com.example.demo.config;

import com.example.demo.security.CustomAuthenticationSuccessHandler;
import com.example.demo.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomAuthenticationSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // =========================================================================
    // 🛡️ SEGURIDAD: Configuración central de Spring Security
    // - 2FA: Rutas /login-2fa y /verificar-2fa permitidas para el flujo.
    // - RBAC: Acceso estricto por roles (hasAuthority) para evitar escalada.
    // - Logout: Invalida la sesión y borra la cookie JSESSIONID (anti-hijacking).
    // - Hashing: BCrypt para proteger las contraseñas en la base de datos.
    // =========================================================================
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/imagenes/**", "/webjars/**", "/imagen/**").permitAll()
                .requestMatchers("/login", "/login/**", "/login-2fa", "/verificar-2fa").permitAll()
                
                .requestMatchers("/dashboard/**", "/usuarios/**", "/auditoria/**", "/respaldos/**", "/inventario/monitoreo/**").hasAuthority("ROLE_DIRECTORA")
                .requestMatchers("/reportes/**").hasAnyAuthority("ROLE_DIRECTORA", "ROLE_SECRETARIA")
                .requestMatchers("/recepciones/**", "/productos/**", "/proveedores/**", "/salidas/**", "/alertas/**", "/movimientos/**").hasAnyAuthority("ROLE_SECRETARIA", "ROLE_DIRECTORA")
                .requestMatchers("/inventario/**", "/solicitudes/**").hasAnyAuthority("ROLE_SECRETARIA", "ROLE_DIRECTORA", "ROLE_COCINERA")
                .requestMatchers("/menu/**", "/solicitudes/mis/**").hasAnyAuthority("ROLE_COCINERA", "ROLE_SECRETARIA", "ROLE_DIRECTORA")
                
                .requestMatchers("/home", "/home/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/home?error=acceso-denegado")
            );

        return http.build();
    }
}