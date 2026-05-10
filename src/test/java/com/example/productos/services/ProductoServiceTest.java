package com.example.productos.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.productos.dto.ProductRequest;
import com.example.productos.dto.ProductResponse;
import com.example.productos.exceptions.AccesoDenegadoException;
import com.example.productos.exceptions.DuplicateResourceException;
import com.example.productos.exceptions.ResourceNotFoundException;
import com.example.productos.models.Producto;
import com.example.productos.repositories.ProductoRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private ProductRequest request;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Teclado");
        producto.setDescripcion("Teclado Mecánico");
        producto.setPrecio(50000L);
        producto.setStock(10);

        request = new ProductRequest("Teclado", "Teclado Mecánico", 50000L, 10);
    }

    @Test
    void testListarTodos() {
        when(productoRepository.findAll()).thenReturn(Collections.singletonList(producto));
        List<ProductResponse> resultado = productoService.listarTodos();
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void testBuscarPorId_Exitoso() {
        // Solo debe existir una copia de este método en todo el archivo
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductResponse resultado = productoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testBuscarPorId_NoEncontrado() {
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productoService.buscarPorId(99L));
    }

    @Test
    void testGuardar_Exitoso() {
        when(productoRepository.existsByNombre(anyString())).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductResponse resultado = productoService.guardar(request, "ADMIN");

        assertNotNull(resultado);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testDescontarStock_Exitoso() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        productoService.descontarStock(1L, 5);
        assertEquals(5, producto.getStock());
        verify(productoRepository).save(producto);
    }
    @Test
void testGuardar_SinPermisos_LanzaExcepcion() {
    // Al ejecutar esto, AccesoDenegadoException sube de 0% a 100%
    assertThrows(AccesoDenegadoException.class, 
        () -> productoService.guardar(request, "USER")); // Solo ADMIN puede guardar
}

@Test
void testGuardar_NombreDuplicado_LanzaExcepcion() {
    when(productoRepository.existsByNombre(anyString())).thenReturn(true);
    // Al ejecutar esto, DuplicateResourceException sube de 0% a 100%
    assertThrows(DuplicateResourceException.class, 
        () -> productoService.guardar(request, "ADMIN"));
}

@Test
void testDescontarStock_Insuficiente_LanzaExcepcion() {
    producto.setStock(5);
    when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
    
    // Prueba la rama del "if (stock < cantidad)"
    assertThrows(DuplicateResourceException.class, 
        () -> productoService.descontarStock(1L, 10));
}

@Test
void testActualizar_ProductoNoEncontrado_LanzaExcepcion() {
    // Cubre lambda$actualizar$1
    when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
            () -> productoService.actualizar(1L, request, "ADMIN"));
}

@Test
void testDescontarStock_ProductoNoEncontrado_LanzaExcepcion() {
    // Cubre lambda$descontarStock$2
    when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> productoService.descontarStock(1L, 5));
}

@Test
void testActualizar_Exitoso() {
    // Aseguramos que pase validarAdmin, encuentre el producto y lo guarde
    when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
    when(productoRepository.save(any(Producto.class))).thenReturn(producto);

    ProductResponse resultado = productoService.actualizar(1L, request, "ADMIN");

    assertNotNull(resultado);
    verify(productoRepository).save(any(Producto.class));
}

@Test
void testEliminar_Exitoso() {
    // Aseguramos que pase validarAdmin y existsById
    when(productoRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> productoService.eliminar(1L, "ADMIN"));
    verify(productoRepository).deleteById(1L);
}

@Test
void testEliminar_ProductoNoEncontrado_LanzaExcepcion() {
    when(productoRepository.existsById(anyLong())).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> productoService.eliminar(99L, "ADMIN"));
}

@Test
void testActualizar_RolNoAdmin_LanzaExcepcion() {
    // Esto cubrirá la rama del "if" de validarAdmin dentro de actualizar
    assertThrows(AccesoDenegadoException.class,
            () -> productoService.actualizar(1L, request, "USER"));
}

@Test
void testEliminar_RolNoAdmin_LanzaExcepcion() {
    // Cubre la rama de seguridad en el método eliminar
    assertThrows(AccesoDenegadoException.class, () -> productoService.eliminar(1L, "USER"));
}
}
