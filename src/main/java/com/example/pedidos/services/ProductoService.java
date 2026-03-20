package com.example.pedidos.services;

import com.example.pedidos.exceptions.AccesoDenegadoException;
import com.example.pedidos.exceptions.DuplicateResourceException;
import com.example.pedidos.exceptions.ResourceNotFoundException;
import com.example.pedidos.models.Producto;
import com.example.pedidos.repositories.ProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarTodos() {
        log.info("Service: Iniciando listado de productos");
        return productoRepository.findAll();
    }

    @SuppressWarnings("null")
    public Producto buscarPorId(Long id) {
        log.info("Service: Buscando producto ID: {}", id);
        return productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Service: Producto ID {} no encontrado", id);
                    return new ResourceNotFoundException("Producto no encontrado con el ID: " + id);
                });
    }

    @SuppressWarnings("null")
    public Producto guardar(Producto producto, String rol) {
    // 1. Primero validamos si tiene permiso (Seguridad)
    validarAdmin(rol);
    
    // 2. Luego validamos que el nombre no exista ya (Regla de Negocio)
    if (productoRepository.existsByNombre(producto.getNombre())) {
        log.warn("Service: Intento de duplicar producto con nombre '{}'", producto.getNombre());
        throw new DuplicateResourceException("El producto '" + producto.getNombre() + "' ya existe en el sistema.");
    }

    // 3. Si todo está bien, guardamos
    log.info("Service: Guardando nuevo producto '{}'", producto.getNombre());
    return productoRepository.save(producto);
}

    @SuppressWarnings("null")
    public Producto actualizar(Long id, Producto detalles, String rol) {
        validarAdmin(rol);
        Producto producto = buscarPorId(id);

        producto.setNombre(detalles.getNombre());
        producto.setPrecio(detalles.getPrecio());
        producto.setStock(detalles.getStock());

        log.info("Service: Actualizando producto ID: {}", id);
        return productoRepository.save(producto);
    }

    @SuppressWarnings("null")
    public void eliminar(Long id, String rol) {
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
        // Usamos la personalizada para que el Handler sepa qué código HTTP devolver
        throw new AccesoDenegadoException("Acceso denegado: Se requieren permisos de ADMINISTRADOR para esta acción.");
    }
}
}