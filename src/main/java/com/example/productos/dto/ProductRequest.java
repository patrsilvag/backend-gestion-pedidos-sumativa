package com.example.productos.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 200)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 200)
    private String descripcion; // <--- Sin tilde para evitar errores de compilación

    @NotNull(message = "El precio es obligatorio")
    @Min(1)
    private Long precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(0)
    private Integer stock;
}
