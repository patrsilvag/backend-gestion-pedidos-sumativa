package com.example.pedidos.services;

import com.example.pedidos.exceptions.AccesoDenegadoException;
import com.example.pedidos.exceptions.DuplicateResourceException;
import com.example.pedidos.exceptions.ResourceNotFoundException;
import com.example.pedidos.models.Producto;
import com.example.pedidos.repositories.ProductoRepository;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@Slf4j
@Service
@Validated // Activa la validación de parámetros en el Service
@SuppressWarnings({ "null", "all" }) // Limpia avisos del IDE y error 1102
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarTodos() {
        log.info("Service: Iniciando listado de productos");
        return productoRepository.findAll();
    }

    public Producto buscarPorId(@NonNull Long id) { //
        log.info("Service: Buscando producto ID: {}", id);
        return productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Service: Producto ID {} no encontrado", id);
                    return new ResourceNotFoundException("Producto no encontrado con el ID: " + id);
                });
    }

    public Producto guardar(@Valid Producto producto, String rol) { //
        validarAdmin(rol);

        if (productoRepository.existsByNombre(producto.getNombre())) {
            log.warn("Service: Intento de duplicar producto con nombre '{}'", producto.getNombre());
            throw new DuplicateResourceException("El producto '" + producto.getNombre() + "' ya existe.");
        }

        log.info("Service: Guardando nuevo producto '{}'", producto.getNombre());
        return productoRepository.save(producto);
    }

    public Producto actualizar(@NonNull Long id, @Valid Producto detalles, String rol) { //
        validarAdmin(rol);
        Producto producto = buscarPorId(id);

        producto.setNombre(detalles.getNombre());
        producto.setPrecio(detalles.getPrecio());
        producto.setStock(detalles.getStock());

        log.info("Service: Actualizando producto ID: {}", id);
        return productoRepository.save(producto);
    }

    public void eliminar(@NonNull Long id, String rol) { //
        log.info("Service: Ejecutando eliminación de producto ID: {}", id);
        validarAdmin(rol);

        if (!productoRepository.existsById(id)) {
            log.warn("Service: Error al eliminar, ID {} no existe", id);
            throw new ResourceNotFoundException("ID " + id + " no existe");
        }

        productoRepository.deleteById(id);
        log.info("Service: Producto ID {} eliminado correctamente", id);
    }

    private void validarAdmin(String rol) {
        if (!"ADMIN".equalsIgnoreCase(rol)) {
            log.warn("Service: Intento de acceso no autorizado con rol: {}", rol);
            throw new AccesoDenegadoException("Acceso denegado: Se requieren permisos de ADMINISTRADOR.");
        }
    }

    public void descontarStock(Long id, Integer cantidad) {
        Producto producto = buscarPorId(id);
        if (producto.getStock() < cantidad) {
            throw new DuplicateResourceException("Stock insuficiente");
        }
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }
   
}