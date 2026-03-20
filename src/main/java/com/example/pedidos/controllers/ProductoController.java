package com.example.pedidos.controllers;

import com.example.pedidos.models.Producto;
import com.example.pedidos.services.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos()); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id)); // 200 OK
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto, @RequestParam String rol) {
        return new ResponseEntity<>(productoService.guardar(producto, rol), HttpStatus.CREATED); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody Producto detalles,
            @RequestParam String rol) {
        return ResponseEntity.ok(productoService.actualizar(id, detalles, rol)); // 200 OK
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @RequestParam String rol) {
         productoService.eliminar(id, rol);
        // Devuelve 204 No Content sin cuerpo, tal como se indico en clases
        return ResponseEntity.noContent().build();
    }
}