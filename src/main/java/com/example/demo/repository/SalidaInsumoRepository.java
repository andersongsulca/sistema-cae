package com.example.demo.repository;

import com.example.demo.model.entity.SalidaInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalidaInsumoRepository extends JpaRepository<SalidaInsumo, Long> {
    
    // Buscar por fecha
    List<SalidaInsumo> findByFecha(LocalDate fecha);
    
    // Buscar por rango de fechas
    List<SalidaInsumo> findByFechaBetween(LocalDate inicio, LocalDate fin);
    
    // Buscar por destino
    List<SalidaInsumo> findByDestino(String destino);
    
    // Buscar por usuario
    List<SalidaInsumo> findByUsuarioId(Long usuarioId);
}