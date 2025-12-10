package com.campus.proyecto_springboot.controller;

import com.campus.proyecto_springboot.dto.CambiarRolRequest;
import com.campus.proyecto_springboot.exception.ResourceNotFoundException;
import com.campus.proyecto_springboot.model.Usuario;
import com.campus.proyecto_springboot.service.Usuario.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioServiceImpl usuarioService;

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> getAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    // Obtener usuario por documento
    @GetMapping("/documento/{documento}")
    public ResponseEntity<Usuario> getByDocumento(@PathVariable String documento) {
        Usuario usuario = usuarioService.findByDocumento(documento);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    // Crear usuario
    @PostMapping
    public ResponseEntity<Usuario> create(@RequestBody Usuario usuario) {
        Usuario saved = usuarioService.save(usuario);
        return ResponseEntity.ok(saved);
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable Long id, @RequestBody Usuario usuario) {
        Usuario existing = usuarioService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setNombre(usuario.getNombre());
        existing.setDocumento(usuario.getDocumento());
        existing.setRole(usuario.getRole());
        // Si luego agregas correo, también actualizar aquí.

        Usuario updated = usuarioService.save(existing);
        return ResponseEntity.ok(updated);
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Usuario existing = usuarioService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Cambiar rol de usuario (solo ADMIN puede hacer esto)
    @PatchMapping("/{id}/rol")
    public ResponseEntity<Usuario> cambiarRol(
            @PathVariable Long id,
            @RequestBody CambiarRolRequest request) {
        Usuario existing = usuarioService.findById(id);
        
        if (existing == null) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        
        if (request.getNuevoRol() == null) {
            throw new IllegalArgumentException("El nuevo rol es obligatorio");
        }
        
        existing.setRole(request.getNuevoRol());
        Usuario updated = usuarioService.save(existing);
        return ResponseEntity.ok(updated);
    }
}
