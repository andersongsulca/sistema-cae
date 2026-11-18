package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.StockInsuficienteException;
import com.example.demo.model.entity.MovimientoInventario;
import com.example.demo.model.entity.Producto;
import com.example.demo.model.enums.TipoMovimiento;
import com.example.demo.repository.MovimientoInventarioRepository;
import com.example.demo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public InventarioService(ProductoRepository productoRepository, 
                           MovimientoInventarioRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public List<Producto> obtenerInventarioCompleto() {
        return productoRepository.findAll();
    }

    public Producto buscarProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }

    public void registrarIngreso(Long productoId, BigDecimal cantidad, String referencia, Long usuarioId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));
        
        BigDecimal stockAnterior = producto.getStockActual();
        BigDecimal nuevoStock = stockAnterior.add(cantidad);
        
        producto.setStockActual(nuevoStock);
        productoRepository.save(producto);
        
        guardarMovimiento(TipoMovimiento.INGRESO, producto, cantidad, stockAnterior, nuevoStock, referencia, usuarioId);
    }

    public void registrarSalida(Long productoId, BigDecimal cantidad, String referencia, Long usuarioId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));
        
        BigDecimal stockAnterior = producto.getStockActual();
        if (stockAnterior.compareTo(cantidad) < 0) {
            throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        
        BigDecimal nuevoStock = stockAnterior.subtract(cantidad);
        producto.setStockActual(nuevoStock);
        productoRepository.save(producto);
        
        guardarMovimiento(TipoMovimiento.SALIDA, producto, cantidad, stockAnterior, nuevoStock, referencia, usuarioId);
    }

    private void guardarMovimiento(TipoMovimiento tipo, Producto producto, BigDecimal cantidad, 
                                   BigDecimal stockAnterior, BigDecimal stockNuevo, String referencia, Long usuarioId) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipo(tipo);
        movimiento.setProducto(producto);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockNuevo(stockNuevo);
        movimiento.setReferencia(referencia);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimientoRepository.save(movimiento);
    }

    public List<MovimientoInventario> obtenerHistorialMovimientos() {
        return movimientoRepository.findAll();
    }

    public List<MovimientoInventario> obtenerMovimientosPorProducto(Long productoId) {
        return movimientoRepository.findByProductoId(productoId);
    }
}