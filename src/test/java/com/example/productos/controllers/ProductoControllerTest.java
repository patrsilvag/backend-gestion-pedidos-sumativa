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

import com.example.productos.exceptions.ResourceNotFoundException;
import com.example.productos.services.ProductoService;
import com.example.productos.exceptions.AccesoDenegadoException;
import com.example.productos.exceptions.DuplicateResourceException;
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

    // ✅ Nuevo Test: Verifica la ruta con barra final (soluciona error 500 en Docker)
    @Test
    void testListar_ConBarraFinal_Exitoso() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Laptop", "Laptop Gamer", 1500L, 10);
        when(productoService.listarTodos()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/productos/")).andExpect(status().isOk())
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

        mockMvc.perform(get("/api/productos/99")).andExpect(status().isNotFound());
    }

    @Test
    @SuppressWarnings("null")
    void testCrear_Exitoso() throws Exception {
        // Definimos el objeto
        ProductRequest request = new ProductRequest("Teclado", "Teclado Mecánico", 80L, 20);
        ProductResponse response = new ProductResponse(1L, "Teclado", "Teclado Mecánico", 80L, 20);

        // ✅ Usamos 'eq(request)' para que la variable se considere "usada"
        when(productoService.guardar(eq(request), eq("ADMIN"))).thenReturn(response);

        String json =
                "{\"nombre\":\"Teclado\",\"descripcion\":\"Teclado Mecánico\",\"precio\":80,\"stock\":20}";

        mockMvc.perform(post("/api/productos").param("rol", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void testEliminar_Exitoso() throws Exception {
        mockMvc.perform(delete("/api/productos/1").param("rol", "ADMIN"))
                .andExpect(status().isNoContent());

        verify(productoService, times(1)).eliminar(1L, "ADMIN");
    }

    @Test
    void testDescontarStock_Exitoso() throws Exception {
        mockMvc.perform(post("/api/productos/1/descontar-stock").param("cantidad", "5"))
                .andExpect(status().isOk());

        verify(productoService, times(1)).descontarStock(1L, 5);
    }

    @Test
    void testErrorGenerico() throws Exception {
        when(productoService.listarTodos()).thenThrow(new RuntimeException("Error inesperado"));
        mockMvc.perform(get("/api/productos")).andExpect(status().isInternalServerError());
    }

       
    
    @Test
    void testErrorRecursoNoEncontrado() throws Exception {
        when(productoService.buscarPorId(anyLong()))
                .thenThrow(new ResourceNotFoundException("No existe"));

        mockMvc.perform(get("/api/productos/99")).andExpect(status().isNotFound())
                // ✅ Cambiamos $.mensaje por $.message (o el nombre real en ErrorDetails)
                .andExpect(jsonPath("$.message").value("No existe"));
    }

   @Test
@SuppressWarnings("null")
void testCrear_ErrorDuplicado() throws Exception {
    // 1. Mockeamos el servicio para que lance la excepción de duplicado
    when(productoService.guardar(any(), anyString()))
        .thenThrow(new DuplicateResourceException("El nombre ya existe"));

    // 2. IMPORTANTE: El JSON debe tener TODOS los campos obligatorios 
    // para que no falle la validación @Valid del controlador (error 400)
    String jsonValidoParaDto = "{" +
        "\"nombre\":\"Teclado\"," +
        "\"descripcion\":\"Teclado Mecánico\"," +
        "\"precio\":80," +
        "\"stock\":20" +
    "}";

    mockMvc.perform(post("/api/productos")
            .param("rol", "ADMIN")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonValidoParaDto))
            // ✅ Ahora sí llegará al servicio y devolverá 409
            .andExpect(status().isConflict()) 
            .andExpect(jsonPath("$.message").value("El nombre ya existe"));
}

    @Test
    void testEliminar_SinPermisos() throws Exception {
        doThrow(new AccesoDenegadoException("No tienes permisos"))
            .when(productoService).eliminar(anyLong(), eq("USER"));

        mockMvc.perform(delete("/api/productos/1").param("rol", "USER"))
                .andExpect(status().isForbidden()) // 403 Forbidden
                .andExpect(jsonPath("$.message").value("No tienes permisos"));
    }
    
    

}
