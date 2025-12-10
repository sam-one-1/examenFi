package com.campus.proyecto_springboot.service.Bodega;

import com.campus.proyecto_springboot.model.Bodega;
import com.campus.proyecto_springboot.model.Producto;

import java.util.List;

public interface BodegaService {

    List<Bodega> findAll();
    Bodega findById(Long id);
    Bodega save(Bodega bodega);
    void deleteById(Long id);
    Bodega findByNombre(String nombre);

}
