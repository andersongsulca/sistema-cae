package com.example.demo.service;

import com.example.demo.model.entity.*;
import com.example.demo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RespaldoService {

    private final String directorioRespaldos = "respaldos";
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProveedorRepository proveedorRepository;
    private final RecepcionRepository recepcionRepository;
    private final SalidaInsumoRepository salidaRepository;
    private final SolicitudInsumoRepository solicitudRepository;
    private final MenuSemanalRepository menuRepository;
    private final ObjectMapper objectMapper;

    public RespaldoService(ProductoRepository productoRepository,
                          UsuarioRepository usuarioRepository,
                          ProveedorRepository proveedorRepository,
                          RecepcionRepository recepcionRepository,
                          SalidaInsumoRepository salidaRepository,
                          SolicitudInsumoRepository solicitudRepository,
                          MenuSemanalRepository menuRepository) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.proveedorRepository = proveedorRepository;
        this.recepcionRepository = recepcionRepository;
        this.salidaRepository = salidaRepository;
        this.solicitudRepository = solicitudRepository;
        this.menuRepository = menuRepository;
        
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        Path path = Paths.get(directorioRespaldos);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException("Error al crear directorio de respaldos", e);
            }
        }
    }

    public List<Map<String, Object>> listarRespaldos() {
        List<Map<String, Object>> respaldos = new ArrayList<>();
        
        try {
            Path path = Paths.get(directorioRespaldos);
            
            // Crear directorio si no existe
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                return respaldos; // Retorna lista vacía
            }
            
            // Listar TODOS los archivos .json del directorio
            Files.list(path)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".json"))
                .sorted((p1, p2) -> p2.getFileName().compareTo(p1.getFileName())) // Ordenar por nombre (más reciente primero)
                .forEach(p -> {
                    try {
                        Map<String, Object> respaldo = new HashMap<>();
                        String nombreArchivo = p.getFileName().toString();
                        
                        respaldo.put("nombre", nombreArchivo);
                        respaldo.put("fecha", getFechaModificacion(p));
                        respaldo.put("tamano", Files.size(p));
                        respaldo.put("ruta", p.toString());
                        respaldo.put("tipo", nombreArchivo.contains("_") ? "Manual" : "Automático");
                        respaldo.put("estado", "Completado");
                        
                        respaldos.add(respaldo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Total de respaldos encontrados: " + respaldos.size());
        return respaldos;
    }

    public String generarRespaldoManual() throws IOException {
        String nombreArchivo = "respaldo_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json";
        Path path = Paths.get(directorioRespaldos, nombreArchivo);
        
        // Crear objeto con datos simples (SIN relaciones bidireccionales)
        Map<String, Object> respaldoCompleto = new HashMap<>();
        respaldoCompleto.put("fechaRespaldo", LocalDateTime.now());
        respaldoCompleto.put("version", "1.0");
        
        // Usuarios (solo campos básicos)
        List<Map<String, Object>> usuariosData = usuarioRepository.findAll().stream()
            .map(u -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", u.getId());
                map.put("username", u.getUsername());
                map.put("nombre", u.getNombre());
                map.put("apellido", u.getApellido());
                map.put("email", u.getEmail());
                map.put("rol", u.getRol() != null ? u.getRol().name() : null);
                map.put("activo", u.getActivo());
                return map;
            }).collect(Collectors.toList());
        respaldoCompleto.put("usuarios", usuariosData);
        
        // Productos (solo campos básicos)
        List<Map<String, Object>> productosData = productoRepository.findAll().stream()
            .map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("codigo", p.getCodigo());
                map.put("nombre", p.getNombre());
                map.put("categoria", p.getCategoria());
                map.put("stockActual", p.getStockActual());
                map.put("stockMinimo", p.getStockMinimo());
                map.put("unidadMedida", p.getUnidadMedida());
                return map;
            }).collect(Collectors.toList());
        respaldoCompleto.put("productos", productosData);
        
        // Proveedores
        List<Map<String, Object>> proveedoresData = proveedorRepository.findAll().stream()
            .map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("nombre", p.getNombre());
                map.put("ruc", p.getRuc());
                map.put("telefono", p.getTelefono());
                map.put("email", p.getEmail());
                map.put("direccion", p.getDireccion());
                return map;
            }).collect(Collectors.toList());
        respaldoCompleto.put("proveedores", proveedoresData);
        
        // Solicitudes (sin detalles complejos)
        List<Map<String, Object>> solicitudesData = solicitudRepository.findAll().stream()
            .map(s -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", s.getId());
                map.put("fechaSolicitud", s.getFechaSolicitud());
                map.put("cocineraUsername", s.getCocineraUsername());
                map.put("prioridad", s.getPrioridad());
                map.put("estado", s.getEstado() != null ? s.getEstado().name() : null);
                map.put("observaciones", s.getObservaciones());
                // Contar productos sin incluir la relación completa
                map.put("cantidadProductos", s.getDetalles() != null ? s.getDetalles().size() : 0);
                return map;
            }).collect(Collectors.toList());
        respaldoCompleto.put("solicitudes", solicitudesData);
        
        // Menús semanales
        List<Map<String, Object>> menusData = menuRepository.findAll().stream()
            .map(m -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", m.getId());
                map.put("semanaInicio", m.getSemanaInicio());
                map.put("semanaFin", m.getSemanaFin());
                map.put("menuJson", m.getMenuJson());
                map.put("observaciones", m.getObservaciones());
                return map;
            }).collect(Collectors.toList());
        respaldoCompleto.put("menus", menusData);
        
        // Guardar como JSON
        objectMapper.writeValue(path.toFile(), respaldoCompleto);
        
        return nombreArchivo;
    }

    public void eliminarRespaldo(String nombreArchivo) throws IOException {
        Path path = Paths.get(directorioRespaldos, nombreArchivo);
        Files.deleteIfExists(path);
    }

    private LocalDateTime getFechaModificacion(Path path) throws IOException {
        return LocalDateTime.ofInstant(
            Files.getLastModifiedTime(path).toInstant(),
            java.time.ZoneId.systemDefault()
        );
    }
}