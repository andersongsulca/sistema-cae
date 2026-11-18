package com.example.demo.repository;

import com.example.demo.model.entity.MovimientoInventario;
import com.example.demo.model.enums.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findByProductoId(Long productoId);
    List<MovimientoInventario> findByTipo(TipoMovimiento tipo);
    List<MovimientoInventario> findByFechaMovimientoBetween(LocalDateTime inicio, LocalDateTime fin);
    List<MovimientoInventario> findByFechaMovimientoOrderByFechaMovimientoDesc(LocalDateTime fecha);
}