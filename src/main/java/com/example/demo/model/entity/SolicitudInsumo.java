package com.example.demo.model.entity;

import com.example.demo.model.enums.EstadoSolicitud;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "solicitudes_insumo")
public class SolicitudInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDate fechaSolicitud;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false, length = 20)
    private String prioridad = "NORMAL";

    @Column(name = "cocinera_id")
    private Long cocineraId;

    @Column(name = "cocinera_username", nullable = false)
    private String cocineraUsername;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleSolicitudInsumo> detalles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public SolicitudInsumo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDate fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
    public Long getCocineraId() { return cocineraId; }
    public void setCocineraId(Long cocineraId) { this.cocineraId = cocineraId; }
    public String getCocineraUsername() { return cocineraUsername; }
    public void setCocineraUsername(String cocineraUsername) { this.cocineraUsername = cocineraUsername; }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
    public List<DetalleSolicitudInsumo> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleSolicitudInsumo> detalles) { this.detalles = detalles; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}