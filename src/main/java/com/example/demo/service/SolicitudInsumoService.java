package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.DetalleSolicitudInsumo;
import com.example.demo.model.entity.SolicitudInsumo;
import com.example.demo.model.enums.EstadoSolicitud;
import com.example.demo.model.enums.TipoAlerta;
import com.example.demo.repository.DetalleSolicitudInsumoRepository;
import com.example.demo.repository.SolicitudInsumoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SolicitudInsumoService {

    private final SolicitudInsumoRepository solicitudRepository;
    private final DetalleSolicitudInsumoRepository detalleRepository;
    private final AlertaService alertaService;

    public SolicitudInsumoService(SolicitudInsumoRepository solicitudRepository,
                                 DetalleSolicitudInsumoRepository detalleRepository,
                                 AlertaService alertaService) {
        this.solicitudRepository = solicitudRepository;
        this.detalleRepository = detalleRepository;
        this.alertaService = alertaService;
    }

    @Transactional(readOnly = true)
    public List<SolicitudInsumo> listarTodas() {
        return solicitudRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<SolicitudInsumo> listarPorCocineraId(Long cocineraId) {
        return solicitudRepository.findByCocineraId(cocineraId);
    }

    @Transactional(readOnly = true)
    public SolicitudInsumo buscarPorId(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + id));
    }

    public SolicitudInsumo crearSolicitud(SolicitudInsumo solicitud) {
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        SolicitudInsumo guardada = solicitudRepository.save(solicitud);

        for (DetalleSolicitudInsumo detalle : guardada.getDetalles()) {
            detalle.setSolicitud(guardada);
            detalleRepository.save(detalle);
        }
        
        // Crear alerta de nueva solicitud
        alertaService.crearAlerta(
            "Nueva solicitud de insumos pendiente de aprobación. " +
            "Solicitante: " + solicitud.getCocineraUsername() + 
            ". Productos: " + guardada.getDetalles().size(),
            TipoAlerta.SOLICITUD_PENDIENTE,
            2L
        );
        
        return guardada;
    }

    public void aprobarSolicitud(Long id) {
        SolicitudInsumo solicitud = buscarPorId(id);
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitudRepository.save(solicitud);
    }

    public void rechazarSolicitud(Long id) {
        SolicitudInsumo solicitud = buscarPorId(id);
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitudRepository.save(solicitud);
    }
}