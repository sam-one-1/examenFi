package com.campus.proyecto_springboot.service.Producto;

import com.campus.proyecto_springboot.model.Producto;
import java.util.List;

public interface ProductoService {

    List<Producto> findAll();
    Producto findById(Long id);
    Producto save(Producto producto);
    void deleteById(Long id);
    Producto findByNombre(String nombre);
    List<Producto> findByStockLessThan(Integer limite);
    List<Producto> buscarPorFiltros(String categoria, Double precioMaximo, Long bodegaId);
}
