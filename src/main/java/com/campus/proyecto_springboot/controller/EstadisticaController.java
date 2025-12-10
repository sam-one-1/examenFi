package com.campus.proyecto_springboot.controller;

import com.campus.proyecto_springboot.dto.ValorInventarioBodegaDTO;
import com.campus.proyecto_springboot.model.Producto;
import com.campus.proyecto_springboot.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaController {

    @Autowired
    private ProductoRepository productoRepository;

    // Valor total de inventario agrupado por bodega
    @GetMapping("/valor-inventario")
    public ResponseEntity<List<ValorInventarioBodegaDTO>> valorInventario() {
        List<ValorInventarioBodegaDTO> valores = productoRepository.calcularValorInventarioPorBodega();
        return ResponseEntity.ok(valores);
    }

    // Top 5 productos con mayor precio unitario
    @GetMapping("/top-caros")
    public ResponseEntity<List<Producto>> topCaros() {
        List<Producto> caros = productoRepository.findTop5ByOrderByPrecioDesc();
        return ResponseEntity.ok(caros);
    }
}
