package com.campus.proyecto_springboot.service.Proveedor;

import com.campus.proyecto_springboot.model.Proveedor;

import java.util.List;

public interface ProveedorService {

    List<Proveedor> findAll();
    Proveedor findById(Long id);
    Proveedor save(Proveedor proveedor);
    void deleteById(Long id);
}
