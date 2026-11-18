package com.example.demo.model.entity;

import com.example.demo.model.enums.TipoMovimiento;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El tipo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipo;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "stock_anterior", precision = 10, scale = 2)
    private BigDecimal stockAnterior;

    @Column(name = "stock_nuevo", precision = 10, scale = 2)
    private BigDecimal stockNuevo;

    @Size(max = 100)
    @Column(length = 100)
    private String referencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public MovimientoInventario() {}

    public MovimientoInventario(Long id, TipoMovimiento tipo, Producto producto, BigDecimal cantidad, BigDecimal stockAnterior, BigDecimal stockNuevo, String referencia, Usuario usuario, LocalDateTime fechaMovimiento, LocalDateTime createdAt) {
        this.id = id; this.tipo = tipo; this.producto = producto; this.cantidad = cantidad; this.stockAnterior = stockAnterior; this.stockNuevo = stockNuevo; this.referencia = referencia; this.usuario = usuario; this.fechaMovimiento = fechaMovimiento; this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    public BigDecimal getStockAnterior() { return stockAnterior; }
    public void setStockAnterior(BigDecimal stockAnterior) { this.stockAnterior = stockAnterior; }
    public BigDecimal getStockNuevo() { return stockNuevo; }
    public void setStockNuevo(BigDecimal stockNuevo) { this.stockNuevo = stockNuevo; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(LocalDateTime fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}