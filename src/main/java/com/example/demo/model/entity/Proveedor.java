package com.example.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(regexp = "^[0-9]{11}$", message = "El RUC debe tener exactamente 11 dígitos numéricos")
    @Column(unique = true, length = 11)
    private String ruc;

    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener exactamente 9 dígitos")
    @Column(length = 9)
    private String telefono;

    @Email(message = "Formato de email inválido")
    @Size(max = 100)
    @Column(length = 100)
    private String email;

    @Size(max = 250)
    @Column(length = 250)
    private String direccion;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // CONSTRUCTORES
    public Proveedor() {}

    public Proveedor(Long id, String nombre, String ruc, String telefono, String email,
                    String direccion, Boolean activo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nombre = nombre;
        this.ruc = ruc;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // GETTERS
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getRuc() { return ruc; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getDireccion() { return direccion; }
    public Boolean getActivo() { return activo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // SETTERS
    public void setId(Long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setEmail(String email) { this.email = email; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}