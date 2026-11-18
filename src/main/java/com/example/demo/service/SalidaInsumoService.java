package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.DetalleSalida;
import com.example.demo.model.entity.SalidaInsumo;
import com.example.demo.repository.DetalleSalidaRepository;
import com.example.demo.repository.SalidaInsumoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SalidaInsumoService {

    private final SalidaInsumoRepository salidaRepository;
    private final DetalleSalidaRepository detalleRepository;
    private final InventarioService inventarioService;

    public SalidaInsumoService(SalidaInsumoRepository salidaRepository,
                              DetalleSalidaRepository detalleRepository,
                              InventarioService inventarioService) {
        this.salidaRepository = salidaRepository;
        this.detalleRepository = detalleRepository;
        this.inventarioService = inventarioService;
    }

    public List<SalidaInsumo> listarTodas() {
        return salidaRepository.findAll();
    }

    public SalidaInsumo buscarPorId(Long id) {
        return salidaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salida no encontrada con ID: " + id));
    }

    public SalidaInsumo crearSalida(SalidaInsumo salida, Long usuarioId) {
        SalidaInsumo salidaGuardada = salidaRepository.save(salida);

        for (DetalleSalida detalle : salidaGuardada.getDetalles()) {
            detalle.setSalida(salidaGuardada);
            DetalleSalida detalleGuardado = detalleRepository.save(detalle);
            
            // Restar del inventario
            inventarioService.registrarSalida(
                detalleGuardado.getProducto().getId(),
                detalleGuardado.getCantidad(),
                "SAL-" + salidaGuardada.getId(),
                usuarioId
            );
        }
        
        return salidaGuardada;
    }

    public void eliminarSalida(Long id) {
        salidaRepository.deleteById(id);
    }
}