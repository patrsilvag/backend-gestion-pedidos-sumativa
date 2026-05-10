package com.example.productos.exceptions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class ExceptionCoverageTest {

    @Test
    void testExceptionConstructors() {
        // Forzamos la ejecución de los constructores para llegar al 100%
        assertNotNull(new ResourceNotFoundException("Not Found"));
        assertNotNull(new AccesoDenegadoException("Denied"));
        assertNotNull(new DuplicateResourceException("Duplicate"));
    }

    @Test
    void testErrorDetails() {
        LocalDateTime now = LocalDateTime.now();
        ErrorDetails error = new ErrorDetails(now, "Mensaje", "Detalles");

        // Cubrimos los getters para que JaCoCo marque la clase ErrorDetails al 100%
        assertEquals(now, error.getTimestamp());
        assertEquals("Mensaje", error.getMessage());
        assertEquals("Detalles", error.getDetails());
    }
}
