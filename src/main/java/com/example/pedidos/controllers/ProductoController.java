package com.example.pedidos.controllers;

import com.example.pedidos.models.Producto;
import com.example.pedidos.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // 1. Obtener todos los productos (Acceso para todos)
    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // 2. Crear un producto (Simulando validación de Rol ADMIN)
    @PostMapping
    @SuppressWarnings("null")
    public ResponseEntity<?> crearProducto(@RequestBody Producto producto, @RequestParam String rol) {
        if (!"ADMIN".equalsIgnoreCase(rol)) {
            return new ResponseEntity<>("No tienes permisos para agregar productos", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(productoRepository.save(producto), HttpStatus.CREATED);
    }

    // 3. Eliminar un producto
    @DeleteMapping("/{id}")
    @SuppressWarnings("null")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id, @RequestParam String rol) {
        if (!"ADMIN".equalsIgnoreCase(rol)) {
            return new ResponseEntity<>("Acceso denegado", HttpStatus.FORBIDDEN);
        }
        productoRepository.deleteById(id);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }
}