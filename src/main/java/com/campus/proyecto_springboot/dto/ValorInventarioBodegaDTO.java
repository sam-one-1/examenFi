package com.campus.proyecto_springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Respuesta resumida de valor total de inventario por bodega.
 */
@Getter
@Setter
@AllArgsConstructor
public class ValorInventarioBodegaDTO {
    private String bodega;
    private Double valorTotal;
}
