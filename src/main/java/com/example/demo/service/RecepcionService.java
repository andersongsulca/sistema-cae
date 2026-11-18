package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.DetalleRecepcion;
import com.example.demo.model.entity.Producto;
import com.example.demo.model.entity.Recepcion;
import com.example.demo.model.enums.EstadoRecepcion;
import com.example.demo.repository.DetalleRecepcionRepository;
import com.example.demo.repository.RecepcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RecepcionService {

    private final RecepcionRepository recepcionRepository;
    private final DetalleRecepcionRepository detalleRepository;
    private final InventarioService inventarioService;
    private final AlertaService alertaService;

    public RecepcionService(RecepcionRepository recepcionRepository,
                           DetalleRecepcionRepository detalleRepository,
                           InventarioService inventarioService,
                           AlertaService alertaService) {
        this.recepcionRepository = recepcionRepository;
        this.detalleRepository = detalleRepository;
        this.inventarioService = inventarioService;
        this.alertaService = alertaService;
    }

    @Transactional(readOnly = true)
    public List<Recepcion> listarTodas() {
        return recepcionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Recepcion buscarPorId(Long id) {
        return recepcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recepción no encontrada con ID: " + id));
    }

    public Recepcion crearRecepcion(Recepcion recepcion, Long usuarioId) {
        recepcion.setEstado(EstadoRecepcion.REGISTRADA);
        Recepcion recepcionGuardada = recepcionRepository.save(recepcion);

        for (DetalleRecepcion detalle : recepcionGuardada.getDetalles()) {
            detalle.setRecepcion(recepcionGuardada);
            DetalleRecepcion detalleGuardado = detalleRepository.save(detalle);
            
            // Actualizar inventario automáticamente
            inventarioService.registrarIngreso(
                detalleGuardado.getProducto().getId(),
                detalleGuardado.getCantidad(),
                "REC-" + recepcionGuardada.getId(),
                usuarioId
            );
            
            // Verificar stock y crear alerta si es necesario
            Producto productoActualizado = detalleGuardado.getProducto();
            alertaService.verificarStockYCrearAlerta(productoActualizado);
        }
        
        return recepcionGuardada;
    }
}