package com.campus.proyecto_springboot.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MovimientoInventarioDTO {
    private Long id;
    private String tipoMovimiento;
    private LocalDateTime fecha;
    private Long usuarioId;

    private int cantidadTotal; // suma de detalles
}
