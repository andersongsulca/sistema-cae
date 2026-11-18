package com.example.demo.model.entity;

import com.example.demo.model.enums.EstadoRecepcion;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recepciones")
public class Recepcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @Size(max = 50)
    @Column(name = "numero_remision", length = 50)
    private String numeroRemision;

    @NotNull(message = "El proveedor es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Size(max = 300)
    @Column(length = 300)
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRecepcion estado = EstadoRecepcion.REGISTRADA;

    @OneToMany(mappedBy = "recepcion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleRecepcion> detalles = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Recepcion() {}

    public Recepcion(Long id, LocalDate fecha, String numeroRemision, Proveedor proveedor, Usuario usuario, String observaciones, EstadoRecepcion estado, List<DetalleRecepcion> detalles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id; this.fecha = fecha; this.numeroRemision = numeroRemision; this.proveedor = proveedor; this.usuario = usuario; this.observaciones = observaciones; this.estado = estado; this.detalles = detalles; this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getNumeroRemision() { return numeroRemision; }
    public void setNumeroRemision(String numeroRemision) { this.numeroRemision = numeroRemision; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public EstadoRecepcion getEstado() { return estado; }
    public void setEstado(EstadoRecepcion estado) { this.estado = estado; }
    public List<DetalleRecepcion> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleRecepcion> detalles) { this.detalles = detalles; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}