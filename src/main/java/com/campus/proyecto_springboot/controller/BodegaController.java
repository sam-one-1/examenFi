package com.campus.proyecto_springboot.controller;

import com.campus.proyecto_springboot.model.Bodega;
import com.campus.proyecto_springboot.service.Bodega.BodegaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bodegas")
public class BodegaController {

    @Autowired
    private BodegaService bodegaService;

    // Obtener todas las bodegas
    @GetMapping
    public ResponseEntity<List<Bodega>> getAll() {
        return ResponseEntity.ok(bodegaService.findAll());
    }

    // Obtener bodega por ID
    @GetMapping("/{id}")
    public ResponseEntity<Bodega> getById(@PathVariable Long id) {
        Bodega bodega = bodegaService.findById(id);

        if (bodega == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(bodega);
    }

    // Obtener bodega por nombre
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Bodega> getByNombre(@PathVariable String nombre) {
        Bodega bodega = bodegaService.findByNombre(nombre);

        if (bodega == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(bodega);
    }

    // Crear nueva bodega
    @PostMapping
    public ResponseEntity<Bodega> create(@RequestBody Bodega bodega) {
        Bodega nueva = bodegaService.save(bodega);
        return ResponseEntity.ok(nueva);
    }

    // Actualizar bodega por ID
    @PutMapping("/{id}")
    public ResponseEntity<Bodega> update(
            @PathVariable Long id,
            @RequestBody Bodega datosActualizados
    ) {
        Bodega existente = bodegaService.findById(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        existente.setNombre(datosActualizados.getNombre());
        existente.setUbicacion(datosActualizados.getUbicacion());
        existente.setCapacidad(datosActualizados.getCapacidad());
        existente.setEncargado(datosActualizados.getEncargado());

        Bodega actualizada = bodegaService.save(existente);

        return ResponseEntity.ok(actualizada);
    }

    // Eliminar bodega por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Bodega existente = bodegaService.findById(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        bodegaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
