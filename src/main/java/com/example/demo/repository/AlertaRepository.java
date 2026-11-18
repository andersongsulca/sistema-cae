package com.example.demo.repository;

import com.example.demo.model.entity.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    
    List<Alerta> findByLeidaFalseOrderByFechaDesc();
    
    List<Alerta> findAllByOrderByFechaDesc();
    
    List<Alerta> findByTipoOrderByFechaDesc(com.example.demo.model.enums.TipoAlerta tipo);
}