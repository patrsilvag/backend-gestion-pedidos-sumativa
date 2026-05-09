package com.example.productos.exceptions;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDetails { // Nombre exacto de la pauta
    private LocalDateTime timestamp; // Fecha/hora
    private String message; // Mensaje
    private String details; // Detalle (URL u otro)
}
