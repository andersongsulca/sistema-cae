package com.example.demo.model.entity;

import com.example.demo.model.enums.EstadoProducto;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 30)
    @Column(unique = true, nullable = false, length = 30)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nombre;

    @Size(max = 80)
    @Column(length = 80)
    private String categoria;

    @Size(max = 50)
    @Column(name = "unidad_medida", length = 50)
    private String unidadMedida;

    @NotNull(message = "El stock actual es obligatorio")
    @DecimalMin(value = "0.0", message = "El stock no puede ser negativo")
    @Column(name = "stock_actual", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockActual = BigDecimal.ZERO;

    @NotNull(message = "El stock mínimo es obligatorio")
    @DecimalMin(value = "0.0", message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_minimo", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockMinimo = BigDecimal.ZERO;

    @Size(max = 300)
    @Column(length = 300)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProducto estado = EstadoProducto.ACTIVO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // CONSTRUCTORES
    public Producto() {}

    public Producto(Long id, String codigo, String nombre, String categoria, String unidadMedida,
                   BigDecimal stockActual, BigDecimal stockMinimo, String descripcion, EstadoProducto estado,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.unidadMedida = unidadMedida;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // GETTERS
    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getUnidadMedida() { return unidadMedida; }
    public BigDecimal getStockActual() { return stockActual; }
    public BigDecimal getStockMinimo() { return stockMinimo; }
    public String getDescripcion() { return descripcion; }
    public EstadoProducto getEstado() { return estado; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // SETTERS
    public void setId(Long id) { this.id = id; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public void setStockActual(BigDecimal stockActual) { this.stockActual = stockActual; }
    public void setStockMinimo(BigDecimal stockMinimo) { this.stockMinimo = stockMinimo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setEstado(EstadoProducto estado) { this.estado = estado; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean tieneStockBajo() {
        return stockActual.compareTo(stockMinimo) <= 0;
    }
}