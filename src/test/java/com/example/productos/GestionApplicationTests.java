package com.example.productos;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GestionApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring cargue sin errores
    }

    @Test
    void testMain() {
        // Al usar assertDoesNotThrow, cubres el punto de entrada de la aplicación
        assertDoesNotThrow(() -> {
            GestionApplication.main(new String[] {});
        });
    }
}
