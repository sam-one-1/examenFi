package com.campus.proyecto_springboot.service.StockBodega;

import com.campus.proyecto_springboot.model.StockBodega;

import java.util.List;
import java.util.Optional;

public interface StockBodegaService {
    
    /**
     * Obtiene o crea el stock de un producto en una bodega
     */
    StockBodega obtenerOCrearStock(Long productoId, Long bodegaId);
    
    /**
     * Obtiene el stock de un producto en una bodega específica
     */
    Optional<StockBodega> obtenerStock(Long productoId, Long bodegaId);
    
    /**
     * Actualiza el stock de un producto en una bodega
     */
    StockBodega actualizarStock(Long productoId, Long bodegaId, Integer cantidad);
    
    /**
     * Incrementa el stock de un producto en una bodega
     */
    StockBodega incrementarStock(Long productoId, Long bodegaId, Integer cantidad);
    
    /**
     * Decrementa el stock de un producto en una bodega
     */
    StockBodega decrementarStock(Long productoId, Long bodegaId, Integer cantidad);
    
    /**
     * Obtiene todo el stock de un producto en todas las bodegas
     */
    List<StockBodega> obtenerStockPorProducto(Long productoId);
    
    /**
     * Obtiene todo el stock de una bodega
     */
    List<StockBodega> obtenerStockPorBodega(Long bodegaId);
    
    /**
     * Calcula el stock total de una bodega
     */
    Integer calcularStockTotalBodega(Long bodegaId);
    
    /**
     * Valida si hay stock suficiente en una bodega
     */
    boolean validarStockDisponible(Long productoId, Long bodegaId, Integer cantidadRequerida);
    
    /**
     * Valida si la bodega tiene capacidad suficiente
     */
    boolean validarCapacidadBodega(Long bodegaId, Integer cantidadAAgregar);
}

