package com.campus.proyecto_springboot.controller;

import com.campus.proyecto_springboot.model.Proveedor;
import com.campus.proyecto_springboot.service.Proveedor.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/proveedores", "/api/proveedores"})
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    // Listar proveedores
    @GetMapping
    public ResponseEntity<List<Proveedor>> getAll() {
        return ResponseEntity.ok(proveedorService.findAll());
    }

    // Obtener proveedor por id
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> getById(@PathVariable Long id) {
        Proveedor proveedor = proveedorService.findById(id);
        if (proveedor == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(proveedor);
    }

    // Crear proveedor
    @PostMapping
    public ResponseEntity<Proveedor> create(@RequestBody Proveedor proveedor) {
        Proveedor nuevo = proveedorService.save(proveedor);
        return ResponseEntity.ok(nuevo);
    }

    // Actualizar proveedor
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> update(@PathVariable Long id, @RequestBody Proveedor datos) {
        Proveedor existente = proveedorService.findById(id);
        if (existente == null) return ResponseEntity.notFound().build();

        // Copiamos campos básicos
        existente.setNombre(datos.getNombre());
        existente.setTelefono(datos.getTelefono());
        existente.setEmail(datos.getEmail());

        Proveedor actualizado = proveedorService.save(existente);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar proveedor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Proveedor existente = proveedorService.findById(id);
        if (existente == null) return ResponseEntity.notFound().build();

        proveedorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
