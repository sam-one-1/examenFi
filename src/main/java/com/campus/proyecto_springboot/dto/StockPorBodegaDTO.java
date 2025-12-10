package com.campus.proyecto_springboot.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockPorBodegaDTO {
    private Long bodegaId;
    private String bodegaNombre;
    private Long stockTotal; // puede ser negativo si hay más salidas que entradas
}
