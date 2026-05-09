package com.example.productos.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import com.example.productos.dto.ProductRequest;
import com.example.productos.dto.ProductResponse;
import com.example.productos.exceptions.AccesoDenegadoException;
import com.example.productos.exceptions.DuplicateResourceException;
import com.example.productos.exceptions.ResourceNotFoundException;
import com.example.productos.models.Producto;
import com.example.productos.repositories.ProductoRepository;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Validated
@SuppressWarnings({"null", "all"})
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Inyección por constructor: Elimina @Autowired de campo para Rating A
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductResponse> listarTodos() {
        log.info("Service: Iniciando listado de productos");
        return productoRepository.findAll().stream().map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse buscarPorId(@NonNull Long id) {
        log.info("Service: Buscando producto ID: {}", id);
        Producto producto = productoRepository.findById(id).orElseThrow(() -> {
            log.warn("Service: Producto ID {} no encontrado", id);
            return new ResourceNotFoundException("Producto no encontrado con el ID: " + id);
        });
        return convertToResponse(producto);
    }

    @Transactional
    public ProductResponse guardar(@Valid ProductRequest request, String rol) {
        validarAdmin(rol);

        if (productoRepository.existsByNombre(request.getNombre())) {
            log.warn("Service: Intento de duplicar producto con nombre '{}'", request.getNombre());
            throw new DuplicateResourceException(
                    "El producto '" + request.getNombre() + "' ya existe.");
        }

        log.info("Service: Guardando nuevo producto '{}'", request.getNombre());
        Producto producto = new Producto();
        actualizarEntidad(producto, request);

        return convertToResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductResponse actualizar(@NonNull Long id, @Valid ProductRequest detalles,
            String rol) {
        validarAdmin(rol);

        // Buscamos la entidad original para actualizarla
        Producto producto = productoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));

        actualizarEntidad(producto, detalles);

        log.info("Service: Actualizando producto ID: {}", id);
        return convertToResponse(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(@NonNull Long id, String rol) {
        log.info("Service: Ejecutando eliminación de producto ID: {}", id);
        validarAdmin(rol);

        if (!productoRepository.existsById(id)) {
            log.warn("Service: Error al eliminar, ID {} no existe", id);
            throw new ResourceNotFoundException("ID " + id + " no existe");
        }

        productoRepository.deleteById(id);
        log.info("Service: Producto ID {} eliminado correctamente", id);
    }

    @Transactional
    public void descontarStock(Long id, Integer cantidad) {
        // Usamos el repositorio directamente para obtener la entidad
        Producto producto = productoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));

        if (producto.getStock() < cantidad) {
            log.warn("Service: Stock insuficiente para producto ID: {}", id);
            throw new DuplicateResourceException("Stock insuficiente");
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
        log.info("Service: Stock actualizado para producto ID: {}. Restante: {}", id,
                producto.getStock());
    }

    // --- Métodos de Mapeo y Validación (Mantienen el código limpio para los tests) ---

    private void validarAdmin(String rol) {
        if (!"ADMIN".equalsIgnoreCase(rol)) {
            log.warn("Service: Intento de acceso no autorizado con rol: {}", rol);
            throw new AccesoDenegadoException(
                    "Acceso denegado: Se requieren permisos de ADMINISTRADOR.");
        }
    }

    private ProductResponse convertToResponse(Producto entity) {
        return new ProductResponse(entity.getId(), entity.getNombre(), entity.getDescripcion(), // Verifica
                                                                                                // que
                                                                                                // coincida
                                                                                                // con
                                                                                                // Producto.java
                entity.getPrecio(), entity.getStock());
    }

    private void actualizarEntidad(Producto entity, ProductRequest request) {
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion()); // <--- Línea 127 corregida
        entity.setPrecio(request.getPrecio());
        entity.setStock(request.getStock());
    }
}
