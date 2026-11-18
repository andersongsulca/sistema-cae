package com.example.demo.config;

import com.example.demo.model.entity.Usuario;
import com.example.demo.model.enums.Rol;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// =========================================================================
// INICIALIZADOR DE DATOS DE LA APLICACIÓN
// Crea usuarios por defecto al arrancar la aplicación si no existen en la BD.
// =========================================================================
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        crearUsuariosIniciales();
        System.out.println(">>> DataInitializer: Usuarios iniciales creados correctamente");
    }

    // =========================================================================
    // 🛡️ SEGURIDAD: GESTIÓN DE CREDENCIALES Y HASHING
    // - Hashing: Se usa `passwordEncoder.encode()` para evitar texto plano.
    // - ⚠️ ALERTA CRÍTICA: La contraseña "123456" es extremadamente débil.
    //   RECOMENDACIÓN: En producción, usar contraseñas aleatorias fuertes y 
    //   forzar el cambio de contraseña en el primer inicio de sesión.
    // =========================================================================
    private void crearUsuariosIniciales() {
        if (usuarioRepository.findByUsername("directora").isEmpty()) {
            Usuario directora = new Usuario();
            directora.setUsername("directora");
            directora.setPassword(passwordEncoder.encode("123456"));
            directora.setNombre("Shirley");
            directora.setApellido("Yucra García");
            directora.setEmail("directora@ie11junio.edu.pe");
            directora.setTelefono("987654321");
            directora.setRol(Rol.DIRECTORA);
            directora.setActivo(true);
            usuarioRepository.save(directora);
            System.out.println(">>> Usuario DIRECTORA creado: directora / 123456");
        }

        if (usuarioRepository.findByUsername("secretaria").isEmpty()) {
            Usuario secretaria = new Usuario();
            secretaria.setUsername("secretaria");
            secretaria.setPassword(passwordEncoder.encode("123456"));
            secretaria.setNombre("Martha");
            secretaria.setApellido("Oscco Yaurimucha");
            secretaria.setEmail("secretaria@ie11junio.edu.pe");
            secretaria.setTelefono("987654322");
            secretaria.setRol(Rol.SECRETARIA);
            secretaria.setActivo(true);
            usuarioRepository.save(secretaria);
            System.out.println(">>> Usuario SECRETARIA creado: secretaria / 123456");
        }

        if (usuarioRepository.findByUsername("cocinera").isEmpty()) {
            Usuario cocinera = new Usuario();
            cocinera.setUsername("cocinera");
            cocinera.setPassword(passwordEncoder.encode("123456"));
            cocinera.setNombre("Carmen");
            cocinera.setApellido("Sulca Luna");
            cocinera.setEmail("cocinera@ie11junio.edu.pe");
            cocinera.setTelefono("987654323");
            cocinera.setRol(Rol.COCINERA);
            cocinera.setActivo(true);
            usuarioRepository.save(cocinera);
            System.out.println(">>> Usuario COCINERA creado: cocinera / 123456");
        }
    }
}