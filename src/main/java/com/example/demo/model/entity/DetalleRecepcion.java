package com.example.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "detalles_recepcion")
public class DetalleRecepcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recepcion_id", nullable = false)
    private Recepcion recepcion;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Size(max = 50)
    @Column(length = 50)
    private String lote;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Size(max = 200)
    @Column(length = 200)
    private String observaciones;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public DetalleRecepcion() {}

    public DetalleRecepcion(Long id, Recepcion recepcion, Producto producto, BigDecimal cantidad, String lote, LocalDate fechaVencimiento, String observaciones, LocalDateTime createdAt) {
        this.id = id; this.recepcion = recepcion; this.producto = producto; this.cantidad = cantidad; this.lote = lote; this.fechaVencimiento = fechaVencimiento; this.observaciones = observaciones; this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Recepcion getRecepcion() { return recepcion; }
    public void setRecepcion(Recepcion recepcion) { this.recepcion = recepcion; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}