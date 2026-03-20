package com.example.pedidos.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "PRODUCTOS")
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRODUCTO")
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 200, message = "La descripción debe tener entre 10 y 200 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio debe ser al menos 1")
    private Long precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
}