package com.campus.proyecto_springboot.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoMasMovidoDTO {
    private Long productoId;
    private String productoNombre;
    private Long totalMovido;
}
