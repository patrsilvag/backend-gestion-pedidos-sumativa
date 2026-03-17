package com.example.pedidos.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PRODUCTOS")
@Data // Esto genera getters y setters automáticos si instalaste Lombok
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 0)
    private Long precio; // Usamos Long porque definimos NUMBER(10,0)

    @Column(nullable = false)
    private Integer stock;

    // Si NO usas Lombok, recuerda insertar aquí los Getters y Setters
    // (Clic derecho -> Source Action -> Generate Getters and Setters)
}