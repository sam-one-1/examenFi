package com.campus.proyecto_springboot.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Datos mínimos para transferir stock entre bodegas.
 */
@Getter
@Setter
public class TransferenciaRequest {
    private Long bodegaOrigenId;
    private Long bodegaDestinoId;
    private Long productoId;
    private int cantidad;
}
