package com.example.demo.repository;

import com.example.demo.model.entity.DetalleSolicitudInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleSolicitudInsumoRepository extends JpaRepository<DetalleSolicitudInsumo, Long> {
}