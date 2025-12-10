package com.campus.proyecto_springboot.repository;

import com.campus.proyecto_springboot.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.campus.proyecto_springboot.dto.ValorInventarioBodegaDTO;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    Producto findByNombre(String nombre);

    List<Producto> findByStockLessThan(Integer stock);

    // Valor total del inventario por bodega (suma de stock*bodega)
    @Query("""
            SELECT new com.campus.proyecto_springboot.dto.ValorInventarioBodegaDTO(
                sb.bodega.nombre,
                SUM(sb.cantidad * p.precio)
            )
            FROM StockBodega sb
            JOIN sb.producto p
            GROUP BY sb.bodega.id, sb.bodega.nombre
            """)
    List<ValorInventarioBodegaDTO> calcularValorInventarioPorBodega();

    // Top 5 productos más caros por precio unitario
    List<Producto> findTop5ByOrderByPrecioDesc();
}
