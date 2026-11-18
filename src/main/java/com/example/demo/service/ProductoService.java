package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.Producto;
import com.example.demo.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final AlertaService alertaService;

    public ProductoService(ProductoRepository productoRepository,
                          AlertaService alertaService) {
        this.productoRepository = productoRepository;
        this.alertaService = alertaService;
    }

    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }

    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        Producto producto = buscarPorId(id);
        
        producto.setCodigo(productoActualizado.getCodigo());
        producto.setNombre(productoActualizado.getNombre());
        producto.setCategoria(productoActualizado.getCategoria());
        producto.setStockActual(productoActualizado.getStockActual());
        producto.setStockMinimo(productoActualizado.getStockMinimo());
        producto.setUnidadMedida(productoActualizado.getUnidadMedida());
        
        Producto productoGuardado = productoRepository.save(producto);
        
        // Verificar stock y crear alerta
        alertaService.verificarStockYCrearAlerta(productoGuardado);
        
        return productoGuardado;
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    // ✅ MÉTODO NUEVO - Agrega esto SIN BORRAR NADA
    public List<Producto> obtenerProductosCriticos() {
        return productoRepository.findAll().stream()
            .filter(p -> p.getStockActual() != null)
            .filter(p -> {
                if (p.getStockActual().compareTo(BigDecimal.ZERO) == 0) {
                    return true; // Stock agotado
                }
                if (p.getStockMinimo() != null) {
                    BigDecimal stockMinimo = new BigDecimal(p.getStockMinimo().toString());
                    return p.getStockActual().compareTo(stockMinimo) <= 0;
                }
                return false;
            })
            .collect(Collectors.toList());
    }
}