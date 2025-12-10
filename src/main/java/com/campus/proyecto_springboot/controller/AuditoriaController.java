package com.campus.proyecto_springboot.controller;

import com.campus.proyecto_springboot.model.Auditoria;
import com.campus.proyecto_springboot.model.TipoOperacion;
import com.campus.proyecto_springboot.service.Auditoria.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditorias")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    // obtener todas las auditorías
    @GetMapping
    public ResponseEntity<List<Auditoria>> getAll() {
        return ResponseEntity.ok(auditoriaService.findAll());
    }

    // obtener auditoría por id
    @GetMapping("/{id}")
    public ResponseEntity<Auditoria> getById(@PathVariable Long id) {
        Auditoria auditoria = auditoriaService.findById(id);

        if (auditoria == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(auditoria);
    }

    // crear una auditoría
    @PostMapping
    public ResponseEntity<Auditoria> create(@RequestBody Auditoria auditoria) {
        Auditoria nueva = auditoriaService.save(auditoria);
        return ResponseEntity.ok(nueva);
    }

    // actualizar auditoría
    @PutMapping("/{id}")
    public ResponseEntity<Auditoria> update(
            @PathVariable Long id,
            @RequestBody Auditoria auditoriaActualizada
    ) {
        Auditoria existente = auditoriaService.findById(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        existente.setTipoOperacion(auditoriaActualizada.getTipoOperacion());
        existente.setFechaHora(auditoriaActualizada.getFechaHora());
        existente.setUsuarioResponsable(auditoriaActualizada.getUsuarioResponsable());
        existente.setEntidadAfectada(auditoriaActualizada.getEntidadAfectada());
        existente.setValoresAnteriores(auditoriaActualizada.getValoresAnteriores());
        existente.setValoresNuevos(auditoriaActualizada.getValoresNuevos());

        Auditoria actualizada = auditoriaService.save(existente);
        return ResponseEntity.ok(actualizada);
    }

    // eliminar auditoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Auditoria existente = auditoriaService.findById(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        auditoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Listar todas o filtrar por usuario / tipo
    @GetMapping("/listar")
    public ResponseEntity<List<Auditoria>> listar(
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) TipoOperacion tipoOperacion) {

        if (documento != null) {
            return ResponseEntity.ok(auditoriaService.findByUsuarioDocumento(documento));
        }
        if (tipoOperacion != null) {
            return ResponseEntity.ok(auditoriaService.findByTipoOperacion(tipoOperacion));
        }
        return ResponseEntity.ok(auditoriaService.findAll());
    }
}
