package com.example.demo.model.entity;

import com.example.demo.model.enums.EstadoRespaldo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "respaldos")
public class Respaldo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "fecha_respaldo", nullable = false)
    private LocalDateTime fechaRespaldo;

    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 30)
    @Column(name = "tipo_respaldo", nullable = false, length = 30)
    private String tipoRespaldo;

    @Size(max = 20)
    @Column(length = 20)
    private String tamano;

    @Size(max = 300)
    @Column(name = "ruta_archivo", length = 300)
    private String rutaArchivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRespaldo estado = EstadoRespaldo.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Size(max = 300)
    @Column(length = 300)
    private String observaciones;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Respaldo() {}

    public Respaldo(Long id, LocalDateTime fechaRespaldo, String tipoRespaldo, String tamano, String rutaArchivo, EstadoRespaldo estado, Usuario usuario, String observaciones, LocalDateTime createdAt) {
        this.id = id; this.fechaRespaldo = fechaRespaldo; this.tipoRespaldo = tipoRespaldo; this.tamano = tamano; this.rutaArchivo = rutaArchivo; this.estado = estado; this.usuario = usuario; this.observaciones = observaciones; this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFechaRespaldo() { return fechaRespaldo; }
    public void setFechaRespaldo(LocalDateTime fechaRespaldo) { this.fechaRespaldo = fechaRespaldo; }
    public String getTipoRespaldo() { return tipoRespaldo; }
    public void setTipoRespaldo(String tipoRespaldo) { this.tipoRespaldo = tipoRespaldo; }
    public String getTamano() { return tamano; }
    public void setTamano(String tamano) { this.tamano = tamano; }
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    public EstadoRespaldo getEstado() { return estado; }
    public void setEstado(EstadoRespaldo estado) { this.estado = estado; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}