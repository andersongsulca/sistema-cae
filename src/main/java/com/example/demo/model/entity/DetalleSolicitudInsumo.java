package com.example.demo.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "detalles_solicitud_insumo")
public class DetalleSolicitudInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private SolicitudInsumo solicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad_solicitada", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadSolicitada;

    @Column(name = "cantidad_aprobada", precision = 10, scale = 2)
    private BigDecimal cantidadAprobada;

    @Column(length = 50)
    private String unidad;

    @Column(length = 200)
    private String observaciones;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public DetalleSolicitudInsumo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SolicitudInsumo getSolicitud() { return solicitud; }
    public void setSolicitud(SolicitudInsumo solicitud) { this.solicitud = solicitud; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public BigDecimal getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(BigDecimal cantidadSolicitada) { this.cantidadSolicitada = cantidadSolicitada; }
    public BigDecimal getCantidadAprobada() { return cantidadAprobada; }
    public void setCantidadAprobada(BigDecimal cantidadAprobada) { this.cantidadAprobada = cantidadAprobada; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}