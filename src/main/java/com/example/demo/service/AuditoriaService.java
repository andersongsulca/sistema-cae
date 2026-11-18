package com.example.demo.service;

import com.example.demo.model.entity.Auditoria;
import com.example.demo.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Auditoria> listarTodos() {
        return auditoriaRepository.findAllByOrderByFechaDesc();
    }

    public Auditoria registrarAuditoria(String usuario, String accion, String tabla, String ip) {
        Auditoria auditoria = new Auditoria();
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setUsuario(usuario);
        auditoria.setAccion(accion);
        auditoria.setTabla(tabla);
        auditoria.setIp(ip);
        return auditoriaRepository.save(auditoria);
    }

    public List<Auditoria> buscarPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return auditoriaRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    public List<Auditoria> buscarPorUsuario(String usuario) {
        return auditoriaRepository.findByUsuario(usuario);
    }

    public List<Auditoria> buscarPorAccion(String accion) {
        return auditoriaRepository.findByAccionContaining(accion);
    }
}