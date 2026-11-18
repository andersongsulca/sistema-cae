package com.example.demo.repository;

import com.example.demo.model.entity.MenuSemanal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuSemanalRepository extends JpaRepository<MenuSemanal, Long> {
    
    // CORREGIDO: Ahora recibe 2 parámetros
    Optional<MenuSemanal> findBySemanaInicioLessThanEqualAndSemanaFinGreaterThanEqual(LocalDate inicio, LocalDate fin);
    
    List<MenuSemanal> findByUsuarioId(Long usuarioId);
    
    // CORREGIDO: Ahora recibe 2 parámetros
    boolean existsBySemanaInicioLessThanEqualAndSemanaFinGreaterThanEqual(LocalDate inicio, LocalDate fin);
}