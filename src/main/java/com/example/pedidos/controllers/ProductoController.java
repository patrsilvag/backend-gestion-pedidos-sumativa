package com.example.pedidos.controllers;

import com.example.pedidos.models.Producto;
import com.example.pedidos.repositories.ProductoRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/productos")
@SuppressWarnings("null") 
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // 1. Listar todos los productos
    @GetMapping
    public List<Producto> listarProductos() {
        // 1. Log de inicio: Registramos que se solicitó el catálogo
        log.info("Solicitud de listado completo de productos iniciada.");

        List<Producto> productos = productoRepository.findAll();

        // 2. Log de información: Es muy útil saber cuántos registros devolvió la nube
        log.info("Consulta exitosa: Se encontraron {} productos en Oracle Cloud.", productos.size());

        return productos;
    }

    @GetMapping("/{id}")
    public Producto obtenerPorId(@PathVariable Long id) {
        // 1. Log de inicio: Registramos que alguien está consultando este ID
        log.info("Consulta de detalles para el producto ID: {}", id);

        return productoRepository.findById(id)
                .map(producto -> {
                    // 2. Log de éxito: Confirmamos que el producto existe
                    log.info("Producto encontrado: '{}'", producto.getNombre());
                    return producto;
                })
                .orElseThrow(() -> {
                    // 3. Log de error: Registramos el fallo antes de lanzar la excepción
                    log.error("Búsqueda fallida: El producto con ID {} no existe en la base de datos", id);
                    return new RuntimeException("Producto no encontrado con el ID: " + id);
                });
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crearProducto(@RequestBody Producto producto, @RequestParam String rol) {
        log.info("Iniciando creación de producto: {} - Usuario Rol: {}", producto.getNombre(), rol);

        validarAdmin(rol);

        // Guardamos primero
        Producto productoGuardado = productoRepository.save(producto);

        // Si llegamos aquí, es que no hubo error en la base de datos
        log.info("Producto creado exitosamente con ID: {}", productoGuardado.getId());

        return productoGuardado;
    }

    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto detalles,
            @RequestParam String rol) {
        // 1. Registramos el inicio de la operación
        log.info("Solicitud de actualización para producto ID: {} - Iniciada por rol: {}", id, rol);

        validarAdmin(rol);

        // 2. Buscamos el producto. Si falla, el orElseThrow saltará al
        // GlobalExceptionHandler
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error en actualización: El producto con ID {} no fue encontrado", id);
                    return new RuntimeException("No se puede actualizar: Producto no encontrado");
                });

        // 3. Seteamos los nuevos valores
        producto.setNombre(detalles.getNombre());
        producto.setPrecio(detalles.getPrecio());
        producto.setStock(detalles.getStock());

        // 4. Guardamos y capturamos el resultado
        Producto productoActualizado = productoRepository.save(producto);

        // 5. Confirmamos el éxito en el log
        log.info("Producto ID: {} actualizado exitosamente en Oracle Cloud", id);

        return productoActualizado;
    }

    @DeleteMapping("/{id}")
    public String eliminarProducto(@PathVariable Long id, @RequestParam String rol) {
        // 1. Log de inicio: Fundamental saber quién solicitó el borrado
        log.info("Solicitud de ELIMINACIÓN para producto ID: {} - Iniciada por rol: {}", id, rol);

        validarAdmin(rol);

        // 2. Verificación de existencia
        if (!productoRepository.existsById(id)) {
            // Log de error: Ayuda a identificar si el frontend está mandando IDs viejos
            log.error("Fallo al eliminar: El producto con ID {} no existe en Oracle Cloud", id);
            throw new RuntimeException("No se puede eliminar: El ID " + id + " no existe.");
        }

        // 3. Acción de borrado
        productoRepository.deleteById(id);

        // 4. Confirmación final
        log.info("Producto ID: {} eliminado exitosamente por el administrador", id);

        return "Producto con ID " + id + " eliminado correctamente";
    }

    private void validarAdmin(String rol) {
        if (!"ADMIN".equalsIgnoreCase(rol)) {
            log.warn("INTENTO DE ACCESO NO AUTORIZADO: Rol '{}' intentó realizar una acción de ADMIN", rol);
            throw new RuntimeException("Acceso denegado: Se requiere rol de ADMINISTRADOR");
        }
    }
}