package com.campus.proyecto_springboot.examen;


import com.campus.proyecto_springboot.model.Producto;
import com.campus.proyecto_springboot.model.StockBodega;
import org.springframework.data.jpa.domain.Specification;

public class ProductoSpecification {

    // Filtra por categoría exacta ignorando mayúsculas/minúsculas
    public static Specification<Producto> categoriaIgualA(String categoria) {
        return (root, query, cb) ->
                categoria == null ? null : cb.equal(cb.lower(root.get("categoria")), categoria.toLowerCase());
    }

    // Limita por precio máximo inclusive
    public static Specification<Producto> precioMenorQue(Double precioMaximo) {
        return (root, query, cb) ->
                precioMaximo == null ? null : cb.lessThanOrEqualTo(root.get("precio"), precioMaximo);
    }

    // Verifica que exista stock del producto en la bodega indicada
    public static Specification<Producto> bodegaIgualA(Long bodegaId) {
        return (root, query, cb) -> {
            if (bodegaId == null) {
                return null;
            }
            var subquery = query.subquery(Long.class);
            var stockRoot = subquery.from(StockBodega.class);
            subquery.select(cb.literal(1L))
                    .where(
                            cb.equal(stockRoot.get("producto"), root),
                            cb.equal(stockRoot.get("bodega").get("id"), bodegaId)
                    );
            return cb.exists(subquery);
        };
    }
}