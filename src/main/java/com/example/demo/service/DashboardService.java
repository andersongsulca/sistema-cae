package com.example.demo.service;

import com.example.demo.model.entity.Producto;
import com.example.demo.model.entity.SolicitudInsumo;
import com.example.demo.model.enums.EstadoSolicitud;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final ProductoRepository productoRepository;
    private final SolicitudInsumoRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    public DashboardService(ProductoRepository productoRepository,
                           SolicitudInsumoRepository solicitudRepository,
                           UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Map<String, Object> obtenerEstadisticasGenerales() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        estadisticas.put("totalProductos", productoRepository.count());
        estadisticas.put("productosStockBajo", contarProductosConStockBajo());
        estadisticas.put("productosAgotados", contarProductosAgotados());
        estadisticas.put("solicitudesPendientes", contarSolicitudesPendientes());
        estadisticas.put("totalUsuarios", usuarioRepository.count());
        
        return estadisticas;
    }

    public long contarProductosConStockBajo() {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream()
            .filter(p -> p.getStockActual() != null && 
                        p.getStockMinimo() != null &&
                        p.getStockActual().compareTo(p.getStockMinimo()) <= 0)
            .count();
    }

    public long contarProductosAgotados() {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream()
            .filter(p -> p.getStockActual() != null && 
                        p.getStockActual().compareTo(BigDecimal.ZERO) == 0)
            .count();
    }

    public long contarSolicitudesPendientes() {
        List<SolicitudInsumo> solicitudes = solicitudRepository.findAll();
        return solicitudes.stream()
            .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE)
            .count();
    }

    public Map<String, Object> obtenerEstadisticasInventario() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Producto> productos = productoRepository.findAll();
        
        int totalProductos = productos.size();
        long productosConStock = productos.stream()
            .filter(p -> p.getStockActual() != null && 
                        p.getStockActual().compareTo(BigDecimal.ZERO) > 0)
            .count();
        
        stats.put("totalProductos", totalProductos);
        stats.put("productosConStock", productosConStock);
        stats.put("productosSinStock", totalProductos - productosConStock);
        
        return stats;
    }

    public Map<String, Object> obtenerEstadisticasSolicitudes() {
        Map<String, Object> stats = new HashMap<>();
        
        List<SolicitudInsumo> solicitudes = solicitudRepository.findAll();
        
        long pendientes = solicitudes.stream().filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE).count();
        long aprobadas = solicitudes.stream().filter(s -> s.getEstado() == EstadoSolicitud.APROBADA).count();
        long rechazadas = solicitudes.stream().filter(s -> s.getEstado() == EstadoSolicitud.RECHAZADA).count();
        long atendidas = solicitudes.stream().filter(s -> s.getEstado() == EstadoSolicitud.ATENDIDA).count();
        
        stats.put("total", (long) solicitudes.size());
        stats.put("pendientes", pendientes);
        stats.put("aprobadas", aprobadas);
        stats.put("rechazadas", rechazadas);
        stats.put("atendidas", atendidas);
        
        return stats;
    }
}