package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.Proveedor;
import com.example.demo.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    public Proveedor buscarPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
    }

    public Proveedor crearProveedor(Proveedor proveedor) {
        if (proveedorRepository.existsByRuc(proveedor.getRuc())) {
            throw new RuntimeException("Ya existe un proveedor con el RUC: " + proveedor.getRuc());
        }
        proveedor.setActivo(true);
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizarProveedor(Long id, Proveedor proveedorActualizado) {
        Proveedor proveedor = buscarPorId(id);
        proveedor.setNombre(proveedorActualizado.getNombre());
        proveedor.setRuc(proveedorActualizado.getRuc());
        proveedor.setTelefono(proveedorActualizado.getTelefono());
        proveedor.setEmail(proveedorActualizado.getEmail());
        proveedor.setDireccion(proveedorActualizado.getDireccion());
        return proveedorRepository.save(proveedor);
    }
}