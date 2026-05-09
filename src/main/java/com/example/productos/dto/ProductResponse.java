package com.example.productos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String nombre;
    private String descripcion; // <--- Sin tilde
    private Long precio;
    private Integer stock;
}
