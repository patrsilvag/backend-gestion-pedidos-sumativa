package com.example.pedidos.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorDetails { // Nombre exacto de la pauta
    private LocalDateTime timestamp; // Fecha/hora
    private String message; // Mensaje
    private String details; // Detalle (URL u otro)
}