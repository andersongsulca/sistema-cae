package com.example.demo.service;

import com.example.demo.model.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario crearUsuario(Usuario usuario) {
        // Validaciones antes de crear
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new RuntimeException("El nombre de usuario es obligatorio");
        }

        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }

        if (usuario.getApellido() == null || usuario.getApellido().trim().isEmpty()) {
            throw new RuntimeException("El apellido es obligatorio");
        }

        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }

        if (usuario.getRol() == null) {
            throw new RuntimeException("El rol es obligatorio");
        }

        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El usuario '" + usuario.getUsername() + "' ya existe");
        }

        if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new RuntimeException("El email '" + usuario.getEmail() + "' ya está registrado");
            }
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Usuario usuario) {
        if (usuario.getId() == null) {
            throw new RuntimeException("El ID del usuario es obligatorio para actualizar");
        }

        if (!usuarioRepository.existsById(usuario.getId())) {
            throw new RuntimeException("Usuario no encontrado con ID: " + usuario.getId());
        }

        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        if (id == null) {
            throw new RuntimeException("El ID es obligatorio para eliminar");
        }

        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }

        usuarioRepository.deleteById(id);
    }
}