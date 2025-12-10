package com.campus.proyecto_springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CrearProductoRequest {
    private String nombre;
    private String categoria;
    private Integer stock;
    private Double precio;
    private Long bodegaId; // ID de la bodega donde se asignará el stock inicial
    private Long proveedorId; // ID del proveedor principal
}

