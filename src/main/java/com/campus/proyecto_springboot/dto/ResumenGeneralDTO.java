package com.campus.proyecto_springboot.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResumenGeneralDTO {

    private List<StockPorBodegaDTO> stockPorBodega;
    private List<ProductoMasMovidoDTO> productosMasMovidos;
}
