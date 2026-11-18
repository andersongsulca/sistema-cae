package com.example.demo.repository;

import com.example.demo.model.entity.Respaldo;
import com.example.demo.model.enums.EstadoRespaldo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RespaldoRepository extends JpaRepository<Respaldo, Long> {
    List<Respaldo> findByEstado(EstadoRespaldo estado);
    List<Respaldo> findByTipoRespaldo(String tipoRespaldo);
    List<Respaldo> findByFechaRespaldoOrderByFechaRespaldoDesc(LocalDateTime fecha);
}