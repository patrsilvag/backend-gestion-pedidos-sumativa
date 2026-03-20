package com.example.pedidos.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        // 1. Maneja "Recurso no encontrado" (404) - REQUISITO PUNTO 10
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorDetails> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
                // Logueamos como WARN según pauta (Punto 11)
                log.warn("Recurso no encontrado: {} - URL: {}", ex.getMessage(), request.getDescription(false));

                ErrorDetails error = new ErrorDetails(
                                LocalDateTime.now(),
                                ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        // 2. Maneja errores de validación @Valid (400) - REQUISITO PUNTOS 6 Y 8
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex,
                        WebRequest request) {
                String errores = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .collect(Collectors.joining(" | "));

                log.warn("Error de validación en la petición: {}", errores);

                ErrorDetails error = new ErrorDetails(
                                LocalDateTime.now(),
                                "Error de validación: " + errores,
                                request.getDescription(false));
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        // 3. Maneja errores generales del servidor (500) - REQUISITO PUNTO 8
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
                // Logueamos como ERROR crítico (Punto 11)
                log.error("ERROR INTERNO DEL SISTEMA: {} - Detalle: {}", ex.getMessage(),
                                request.getDescription(false));

                ErrorDetails error = new ErrorDetails(
                                LocalDateTime.now(),
                                "Ocurrió un error inesperado en el servidor",
                                request.getDescription(false));
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // manejador para errores de seguridad (403)
        @ExceptionHandler(AccesoDenegadoException.class)
        public ResponseEntity<ErrorDetails> handleAccesoDenegado(AccesoDenegadoException ex, WebRequest request) {
                log.warn("SEGURIDAD: Intento de acceso no autorizado - {}", ex.getMessage());

                ErrorDetails error = new ErrorDetails(
                                LocalDateTime.now(),
                                ex.getMessage(), // Aquí dirá "No tienes permisos de Administrador"
                                request.getDescription(false));
                return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ErrorDetails> handleDuplicateResource(DuplicateResourceException ex, WebRequest request) {
                log.warn("CONFLICTO: Intento de crear producto duplicado - {}", ex.getMessage());

                ErrorDetails error = new ErrorDetails(
                                LocalDateTime.now(),
                                ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
}