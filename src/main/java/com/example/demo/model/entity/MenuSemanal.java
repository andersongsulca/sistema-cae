package com.example.demo.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_semanal")
public class MenuSemanal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "semana_inicio", nullable = false)
    private LocalDate semanaInicio;

    @Column(name = "semana_fin", nullable = false)
    private LocalDate semanaFin;

    @Column(name = "menu_json", length = 5000)
    private String menuJson;  // JSON con los platos de cada día

    @Column(length = 500)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // CONSTRUCTORES
    public MenuSemanal() {}

    // GETTERS Y SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getSemanaInicio() { return semanaInicio; }
    public void setSemanaInicio(LocalDate semanaInicio) { this.semanaInicio = semanaInicio; }
    public LocalDate getSemanaFin() { return semanaFin; }
    public void setSemanaFin(LocalDate semanaFin) { this.semanaFin = semanaFin; }
    public String getMenuJson() { return menuJson; }
    public void setMenuJson(String menuJson) { this.menuJson = menuJson; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}