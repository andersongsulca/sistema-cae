package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.MovimientoInventario;
import com.example.demo.model.enums.TipoMovimiento;
import com.example.demo.repository.MovimientoInventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoRepository;

    public MovimientoInventarioService(MovimientoInventarioRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    // RB-006 y RB-010: Consulta del historial de movimientos para auditoría y control
    public List<MovimientoInventario> listarTodos() {
        return movimientoRepository.findAll();
    }

    public MovimientoInventario buscarPorId(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento de inventario no encontrado con ID: " + id));
    }

    // Consultar movimientos específicos de un producto (Trazabilidad)
    public List<MovimientoInventario> buscarPorProducto(Long productoId) {
        return movimientoRepository.findByProductoId(productoId);
    }

    // Filtrar por tipo (INGRESO, SALIDA, AJUSTE)
    public List<MovimientoInventario> buscarPorTipo(TipoMovimiento tipo) {
        return movimientoRepository.findByTipo(tipo);
    }

    // Reportes por rango de fechas (RB-040)
    public List<MovimientoInventario> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByFechaMovimientoBetween(inicio, fin);
    }

    // Para el Dashboard de la Directora (CU-016)
    public List<MovimientoInventario> buscarMovimientosRecientes() {
        return movimientoRepository.findByFechaMovimientoOrderByFechaMovimientoDesc(LocalDateTime.now());
    }
}