package com.example.productos.config;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void testCorsConfigurationSource() {
        var source = securityConfig.corsConfigurationSource();
        assertNotNull(source);

        CorsConfiguration config =
                ((UrlBasedCorsConfigurationSource) source).getCorsConfigurations().get("/**");

        assertNotNull(config, "La configuración CORS no debería ser nula");

        List<String> origins = config.getAllowedOrigins();
        assertNotNull(origins, "La lista de orígenes no debería ser nula");

        assertTrue(origins.contains("http://mi-app-docker"));
        assertTrue(origins.contains("http://localhost:4200"));

        // ✅ Opción recomendada: Compara el objeto sin necesidad de unboxing
        assertEquals(Boolean.TRUE, config.getAllowCredentials(), "Debe permitir credenciales");
    }
}
