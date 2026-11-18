package com.example.demo.repository;

import com.example.demo.model.entity.SolicitudInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudInsumoRepository extends JpaRepository<SolicitudInsumo, Long> {
    
    List<SolicitudInsumo> findByCocineraId(Long cocineraId);
    
    List<SolicitudInsumo> findByEstado(com.example.demo.model.enums.EstadoSolicitud estado);
    
    List<SolicitudInsumo> findByFechaSolicitud(java.time.LocalDate fecha);
}