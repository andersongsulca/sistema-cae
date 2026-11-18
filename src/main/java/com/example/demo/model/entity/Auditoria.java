package com.example.demo.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 100)
    private String usuario;

    @Column(nullable = false, length = 200)
    private String accion;

    @Column(length = 100)
    private String tabla;

    @Column(length = 50)
    private String ip;

    @Column(length = 500)
    private String detalles;

    public Auditoria() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getTabla() { return tabla; }
    public void setTabla(String tabla) { this.tabla = tabla; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }
}