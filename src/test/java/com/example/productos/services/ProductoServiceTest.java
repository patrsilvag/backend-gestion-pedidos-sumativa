package com.example.productos.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections; // <--- USADO en testListarTodos
import java.util.List; // <--- USADO en testListarTodos
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.productos.dto.ProductRequest;
import com.example.productos.dto.ProductResponse;
import com.example.productos.exceptions.AccesoDenegadoException; // <--- USADO en
                                                                 // testGuardar_SinPermisos
import com.example.productos.exceptions.DuplicateResourceException; // <--- USADO en
                                                                    // testGuardar_NombreDuplicado
import com.example.productos.exceptions.ResourceNotFoundException; // <--- USADO en
                                                                   // testBuscarPorId_NoEncontrado
import com.example.productos.models.Producto;
import com.example.productos.repositories.ProductoRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null") // Elimina avisos de seguridad de nulos en Java 21
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
        // Uso de List y Collections para cubrir las importaciones
        when(productoRepository.findAll()).thenReturn(Collections.singletonList(producto));

        List<ProductResponse> resultado = productoService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Teclado", resultado.get(0).getNombre());
    }

    @Test
    void testBuscarPorId_Exitoso() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductResponse resultado = productoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testBuscarPorId_NoEncontrado() {
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Uso de ResourceNotFoundException
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
    void testGuardar_NombreDuplicado() {
        when(productoRepository.existsByNombre(anyString())).thenReturn(true);

        // Uso de DuplicateResourceException
        assertThrows(DuplicateResourceException.class,
                () -> productoService.guardar(request, "ADMIN"));
    }

    @Test
    void testGuardar_SinPermisos() {
        // Uso de AccesoDenegadoException
        assertThrows(AccesoDenegadoException.class, () -> productoService.guardar(request, "USER"));
    }

    @Test
    void testActualizar_Exitoso() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductResponse resultado = productoService.actualizar(1L, request, "ADMIN");

        assertNotNull(resultado);
        assertEquals("Teclado", resultado.getNombre());
    }

    @Test
    void testEliminar_Exitoso() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        productoService.eliminar(1L, "ADMIN");

        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDescontarStock_Exitoso() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        productoService.descontarStock(1L, 5);

        assertEquals(5, producto.getStock());
        verify(productoRepository).save(producto);
    }

    @Test
    void testDescontarStock_Insuficiente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Se reutiliza la lógica de excepción de stock
        assertThrows(DuplicateResourceException.class,
                () -> productoService.descontarStock(1L, 20));
    }
    
}
