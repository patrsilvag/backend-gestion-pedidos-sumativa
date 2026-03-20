package com.example.pedidos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN) // Esto asegura el código 403
public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String message) {
        super(message);
    }
}