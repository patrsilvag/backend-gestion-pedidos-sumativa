package com.example.pedidos.controllers;

import com.example.pedidos.models.Producto;
import com.example.pedidos.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@SuppressWarnings("null") // <--- COLÓCALO AQUÍ para silenciar todos los avisos de la clase
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Producto obtenerPorId(@PathVariable Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el ID: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crearProducto(@RequestBody Producto producto, @RequestParam String rol) {
        validarAdmin(rol);
        return productoRepository.save(producto);
    }

    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto detalles,
            @RequestParam String rol) {
        validarAdmin(rol);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede actualizar: Producto no encontrado"));
        producto.setNombre(detalles.getNombre());
        producto.setPrecio(detalles.getPrecio());
        producto.setStock(detalles.getStock());
        return productoRepository.save(producto);
    }

    @DeleteMapping("/{id}")
    public String eliminarProducto(@PathVariable Long id, @RequestParam String rol) {
        validarAdmin(rol);
        if (!productoRepository.existsById(id)) { // <-- Aquí estaba tu línea 64
            throw new RuntimeException("No se puede eliminar: El ID " + id + " no existe.");
        }
        productoRepository.deleteById(id);
        return "Producto eliminado correctamente";
    }

    private void validarAdmin(String rol) {
        if (!"ADMIN".equalsIgnoreCase(rol)) {
            throw new RuntimeException("Acceso denegado: Se requiere rol de ADMINISTRADOR");
        }
    }
}