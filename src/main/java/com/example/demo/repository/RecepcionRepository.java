package com.example.demo.repository;

import com.example.demo.model.entity.Recepcion;
import com.example.demo.model.enums.EstadoRecepcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecepcionRepository extends JpaRepository<Recepcion, Long> {
    List<Recepcion> findByEstado(EstadoRecepcion estado);
    List<Recepcion> findByFechaBetween(LocalDate inicio, LocalDate fin);
    List<Recepcion> findByFechaOrderByFechaDesc(LocalDate fecha);
}