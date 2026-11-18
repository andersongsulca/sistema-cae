package com.example.demo.model.entity;

import com.example.demo.model.enums.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String apellido;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Email(message = "Formato de email inválido")
    @Size(max = 100)
    @Column(length = 100)
    private String email;

    @Pattern(regexp = "^9\\d{8}$", message = "El celular debe tener 9 dígitos y empezar con 9")
    @Column(length = 9)
    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // =========================================================================
    // CAMPOS PARA AUTENTICACIÓN DE DOBLE PASO (2FA / TOTP)
    // =========================================================================

    @Column(name = "secret_key_2fa", length = 100)
    private String secretKey2fa;

    @Column(name = "is_2fa_enabled")
    private Boolean is2faEnabled = false;

    // CONSTRUCTORES
    public Usuario() {
    }

    public Usuario(Long id, String username, String password, String nombre, String apellido, 
                   Rol rol, String email, String telefono, Boolean activo, 
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.email = email;
        this.telefono = telefono;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Getters y Setters para 2FA
    public String getSecretKey2fa() {
        return secretKey2fa;
    }

    public void setSecretKey2fa(String secretKey2fa) {
        this.secretKey2fa = secretKey2fa;
    }

    public Boolean getIs2faEnabled() {
        return is2faEnabled;
    }

    public void setIs2faEnabled(Boolean is2faEnabled) {
        this.is2faEnabled = is2faEnabled;
    }

    // MÉTODOS ADICIONALES
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}