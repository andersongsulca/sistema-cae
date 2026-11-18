package com.example.demo.service;

import com.example.demo.model.entity.MovimientoInventario;
import com.example.demo.model.entity.Producto;
import com.example.demo.repository.MovimientoInventarioRepository;
import com.example.demo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReporteService {

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public ReporteService(ProductoRepository productoRepository, 
                         MovimientoInventarioRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public Map<String, Object> generarReporteInventario() {
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("totalProductos", productoRepository.count());
        reporte.put("productosActivos", productoRepository.findAll().size());
        return reporte;
    }

    public List<MovimientoInventario> generarReporteMovimientos(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByFechaMovimientoBetween(inicio, fin);
    }

    public List<Producto> obtenerProductosCriticos() {
        return productoRepository.findAll().stream()
                .filter(p -> p.getStockActual().compareTo(p.getStockMinimo()) <= 0)
                .collect(Collectors.toList());
    }
}