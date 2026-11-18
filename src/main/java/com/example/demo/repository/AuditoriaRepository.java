package com.example.demo.repository;

import com.example.demo.model.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    
    List<Auditoria> findAllByOrderByFechaDesc();
    
    List<Auditoria> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    List<Auditoria> findByUsuario(String usuario);
    
    List<Auditoria> findByAccionContaining(String accion);
    
    List<Auditoria> findByTabla(String tabla);
}