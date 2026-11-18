package com.example.demo.repository;

import com.example.demo.model.entity.Producto;
import com.example.demo.model.enums.EstadoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByEstado(EstadoProducto estado);
    List<Producto> findByStockActualLessThanEqual(Double stockMinimo);
}