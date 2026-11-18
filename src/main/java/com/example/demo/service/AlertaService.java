package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.Alerta;
import com.example.demo.model.entity.Producto;
import com.example.demo.model.enums.TipoAlerta;
import com.example.demo.repository.AlertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @Transactional(readOnly = true)
    public List<Alerta> listarTodas() {
        return alertaRepository.findAllByOrderByFechaDesc();
    }

    @Transactional(readOnly = true)
    public List<Alerta> listarNoLeidas() {
        return alertaRepository.findByLeidaFalseOrderByFechaDesc();
    }

    @Transactional(readOnly = true)
    public Alerta buscarPorId(Long id) {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada con ID: " + id));
    }

    public Alerta crearAlerta(String mensaje, TipoAlerta tipo, Long prioridad) {
        Alerta alerta = new Alerta();
        alerta.setMensaje(mensaje);
        alerta.setTipo(tipo);
        alerta.setPrioridad(prioridad);
        alerta.setLeida(false);
        alerta.setEstado("ACTIVA");
        alerta.setFecha(LocalDateTime.now());
        return alertaRepository.save(alerta);
    }

    public void marcarComoLeida(Long id) {
        Alerta alerta = buscarPorId(id);
        alerta.setLeida(true);
        alertaRepository.save(alerta);
    }

    public void marcarTodasComoLeidas() {
        List<Alerta> alertasNoLeidas = listarNoLeidas();
        for (Alerta alerta : alertasNoLeidas) {
            alerta.setLeida(true);
            alertaRepository.save(alerta);
        }
    }

    public void eliminarAlerta(Long id) {
        alertaRepository.deleteById(id);
    }

    public void eliminarTodas() {
        alertaRepository.deleteAll();
    }

    public void verificarStockYCrearAlerta(Producto producto) {
        if (producto == null || producto.getStockActual() == null) {
            return;
        }

        BigDecimal stockActual = producto.getStockActual();
        
        // Si stock es 0 → Producto agotado
        if (stockActual.compareTo(BigDecimal.ZERO) == 0) {
            crearAlerta(
                "PRODUCTO AGOTADO: " + producto.getNombre() + 
                " (Código: " + producto.getCodigo() + ")",
                TipoAlerta.PRODUCTO_AGOTADO,
                1L
            );
            return;
        }
        
        // Si stock mínimo existe y stock actual <= stock mínimo
        if (producto.getStockMinimo() != null) {
            BigDecimal stockMinimo = new BigDecimal(producto.getStockMinimo().toString());
            
            if (stockActual.compareTo(stockMinimo) <= 0) {
                crearAlerta(
                    "Stock bajo: " + producto.getNombre() + 
                    ". Stock actual: " + stockActual + 
                    ". Stock mínimo: " + producto.getStockMinimo(),
                    TipoAlerta.STOCK_BAJO,
                    2L
                );
            }
        }
    }
}