package com.campus.proyecto_springboot.repository;

import com.campus.proyecto_springboot.model.StockBodega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockBodegaRepository extends JpaRepository<StockBodega, Long> {

    /**
     * Busca el stock de un producto en una bodega específica
     */
    Optional<StockBodega> findByProductoIdAndBodegaId(Long productoId, Long bodegaId);

    /**
     * Obtiene todo el stock de un producto en todas las bodegas
     */
    List<StockBodega> findByProductoId(Long productoId);

    /**
     * Obtiene todo el stock de una bodega
     */
    List<StockBodega> findByBodegaId(Long bodegaId);

    /**
     * Calcula el stock total de una bodega (suma de todas las cantidades)
     */
    @Query("SELECT COALESCE(SUM(s.cantidad), 0) FROM StockBodega s WHERE s.bodega.id = :bodegaId")
    Integer calcularStockTotalBodega(@Param("bodegaId") Long bodegaId);
}

