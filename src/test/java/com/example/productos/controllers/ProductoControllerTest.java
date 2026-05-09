package com.example.productos.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.productos.dto.ProductRequest;
import com.example.productos.dto.ProductResponse;
import com.example.productos.exceptions.AccesoDenegadoException;
import com.example.productos.exceptions.ResourceNotFoundException;
import com.example.productos.services.ProductoService;
import com.example.productos.exceptions.GlobalExceptionHandler;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    @BeforeEach
    void setUp() {
        // Inyección por constructor manual para mayor fiabilidad en los tests
        productoController = new ProductoController(productoService);
        mockMvc = MockMvcBuilders.standaloneSetup(productoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @Test
    void testListar_Exitoso() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Laptop", "Laptop Gamer", 1500L, 10);
        when(productoService.listarTodos()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/productos")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Laptop"));
    }

    @Test
    void testObtenerPorId_Exitoso() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Mouse", "Mouse óptico", 25L, 50);
        when(productoService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/productos/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Mouse"));
    }

    @Test
    void testObtenerPorId_NoEncontrado() throws Exception {
        when(productoService.buscarPorId(anyLong()))
                .thenThrow(new ResourceNotFoundException("No existe"));

        mockMvc.perform(get("/api/productos/99")).andExpect(status().isNotFound()); // Cubre el 404
                                                                                    // del Handler
    }

    @Test
    @SuppressWarnings("null")
    void testCrear_Exitoso() throws Exception {
        // La variable 'request' ahora se usa directamente, eliminando el aviso de "unused"
        ProductRequest request = new ProductRequest("Teclado", "Teclado Mecánico", 80L, 20);
        ProductResponse response = new ProductResponse(1L, "Teclado", "Teclado Mecánico", 80L, 20);

        // Pasamos los valores directamente sin envolverlos en eq()
        when(productoService.guardar(request, "ADMIN")).thenReturn(response);

        String json =
                "{\"nombre\":\"Teclado\",\"descripcion\":\"Teclado Mecánico\",\"precio\":80,\"stock\":20}";

        mockMvc.perform(post("/api/productos").param("rol", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @SuppressWarnings("null")
    void testActualizar_Exitoso() throws Exception {
        ProductRequest request = new ProductRequest("Monitor", "Monitor 4K Ultra", 300L, 5);
        ProductResponse response = new ProductResponse(1L, "Monitor", "Monitor 4K Ultra", 300L, 5);

        // Eliminamos eq() en todos los argumentos para mayor legibilidad
        when(productoService.actualizar(1L, request, "ADMIN")).thenReturn(response);

        String json =
                "{\"nombre\":\"Monitor\",\"descripcion\":\"Monitor 4K Ultra\",\"precio\":300,\"stock\":5}";

        mockMvc.perform(put("/api/productos/1").param("rol", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
    }

    @Test
    void testEliminar_Exitoso() throws Exception {
        mockMvc.perform(delete("/api/productos/1").param("rol", "ADMIN"))
                .andExpect(status().isNoContent());

        verify(productoService, times(1)).eliminar(1L, "ADMIN");
    }

    @Test
    void testEliminar_AccesoDenegado() throws Exception {
        doThrow(new AccesoDenegadoException("Denegado")).when(productoService).eliminar(anyLong(),
                eq("USER"));

        mockMvc.perform(delete("/api/productos/1").param("rol", "USER"))
                .andExpect(status().isForbidden()); // Cubre el 403 del Handler
    }

    @Test
    void testDescontarStock_Exitoso() throws Exception {
        mockMvc.perform(post("/api/productos/1/descontar-stock").param("cantidad", "5"))
                .andExpect(status().isOk());

        verify(productoService, times(1)).descontarStock(1L, 5);
    }

    @Test
    @SuppressWarnings("null") // Elimina el aviso de seguridad de nulos en la línea 144
    void testCrear_ErrorValidacion() throws Exception {
        String jsonInvalido = "{\"nombre\":\"\",\"precio\":-1}";

        mockMvc.perform(
                post("/api/productos").param("rol", "ADMIN").contentType(MediaType.APPLICATION_JSON) // Aquí
                                                                                                     // es
                                                                                                     // donde
                                                                                                     // el
                                                                                                     // IDE
                                                                                                     // marcaba
                                                                                                     // el
                                                                                                     // aviso
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testErrorGenerico() throws Exception {
        when(productoService.listarTodos()).thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(get("/api/productos")).andExpect(status().isInternalServerError());
    }
}
