package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.MenuSemanal;
import com.example.demo.repository.MenuSemanalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuSemanalService {

    private final MenuSemanalRepository menuRepository;

    public MenuSemanalService(MenuSemanalRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuSemanal> listarTodos() {
        return menuRepository.findAll();
    }

    public MenuSemanal buscarPorId(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menú no encontrado con ID: " + id));
    }

    public MenuSemanal crearMenu(MenuSemanal menu) {
        // Verificar si ya existe un menú para esas fechas
        if (menuRepository.existsBySemanaInicioLessThanEqualAndSemanaFinGreaterThanEqual(
                menu.getSemanaInicio(), menu.getSemanaFin())) {
            throw new RuntimeException("Ya existe un menú para este rango de fechas");
        }
        
        return menuRepository.save(menu);
    }

    public MenuSemanal actualizarMenu(Long id, MenuSemanal menuActualizado) {
        MenuSemanal menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menú no encontrado con ID: " + id));
        
        menu.setSemanaInicio(menuActualizado.getSemanaInicio());
        menu.setSemanaFin(menuActualizado.getSemanaFin());
        menu.setMenuJson(menuActualizado.getMenuJson());
        menu.setObservaciones(menuActualizado.getObservaciones());
        
        return menuRepository.save(menu);
    }

    public void eliminarMenu(Long id) {
        menuRepository.deleteById(id);
    }
}