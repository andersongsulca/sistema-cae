package com.example.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "detalles_menu")
public class DetalleMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private MenuSemanal menu;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @NotBlank(message = "El tipo de comida es obligatorio")
    @Size(max = 30)
    @Column(name = "tipo_comida", nullable = false, length = 30)
    private String tipoComida;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 300)
    @Column(nullable = false, length = 300)
    private String descripcion;

    @Size(max = 500)
    @Column(length = 500)
    private String ingredientes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public DetalleMenu() {}

    public DetalleMenu(Long id, MenuSemanal menu, LocalDate fecha, String tipoComida, String descripcion, String ingredientes, LocalDateTime createdAt) {
        this.id = id; this.menu = menu; this.fecha = fecha; this.tipoComida = tipoComida; this.descripcion = descripcion; this.ingredientes = ingredientes; this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public MenuSemanal getMenu() { return menu; }
    public void setMenu(MenuSemanal menu) { this.menu = menu; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getTipoComida() { return tipoComida; }
    public void setTipoComida(String tipoComida) { this.tipoComida = tipoComida; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getIngredientes() { return ingredientes; }
    public void setIngredientes(String ingredientes) { this.ingredientes = ingredientes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}