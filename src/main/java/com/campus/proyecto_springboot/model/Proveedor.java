package com.campus.proyecto_springboot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Proveedor principal que suministra productos.
 */
@Entity
@Table(name = "proveedores")
@Getter
@Setter
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del proveedor
    @Column(nullable = false, length = 80)
    private String nombre;

    // Teléfono de contacto
    @Column(length = 30)
    private String telefono;

    // Correo principal de contacto
    @Column(length = 120)
    private String email;
}
