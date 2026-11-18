package com.example.demo.repository;

import com.example.demo.model.entity.DetalleMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DetalleMenuRepository extends JpaRepository<DetalleMenu, Long> {
    List<DetalleMenu> findByMenuId(Long menuId);
    List<DetalleMenu> findByFechaBetween(LocalDate inicio, LocalDate fin);
}