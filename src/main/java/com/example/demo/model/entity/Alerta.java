package com.example.demo.model.entity;

import com.example.demo.model.enums.TipoAlerta;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAlerta tipo;

    @Column
    private Long prioridad;

    @Column(nullable = false)
    private Boolean leida = false;

    @Column(name = "fecha_alerta")
    private LocalDateTime fecha;

    @Column(length = 50)
    private String estado = "ACTIVA";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Alerta() {}

    // GETTERS Y SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public TipoAlerta getTipo() { return tipo; }
    public void setTipo(TipoAlerta tipo) { this.tipo = tipo; }
    public Long getPrioridad() { return prioridad; }
    public void setPrioridad(Long prioridad) { this.prioridad = prioridad; }
    public Boolean getLeida() { return leida; }
    public void setLeida(Boolean leida) { this.leida = leida; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}