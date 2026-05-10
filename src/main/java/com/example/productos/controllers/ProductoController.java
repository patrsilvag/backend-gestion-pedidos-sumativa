package com.example.productos.controllers;

import com.example.productos.dto.ProductRequest;
import com.example.productos.dto.ProductResponse;
import com.example.productos.services.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
// ✅ Se añaden los orígenes de Docker y local
@CrossOrigin(origins = {"http://mi-app-docker", "http://localhost:4200", "http://localhost"})
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // ✅ Se permite tanto "/api/productos" como "/api/productos/"
    @GetMapping({"", "/"})
    public ResponseEntity<List<ProductResponse>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> crear(@Valid @RequestBody ProductRequest request,
            @RequestParam String rol) {
        return new ResponseEntity<>(productoService.guardar(request, rol), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> actualizar(@PathVariable Long id,
            @Valid @RequestBody ProductRequest detalles, @RequestParam String rol) {
        return ResponseEntity.ok(productoService.actualizar(id, detalles, rol));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @RequestParam String rol) {
        productoService.eliminar(id, rol);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/descontar-stock")
    public ResponseEntity<Void> descontarStock(@PathVariable Long id,
            @RequestParam Integer cantidad) {
        productoService.descontarStock(id, cantidad);
        return ResponseEntity.ok().build();
    }
}
