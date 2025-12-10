package com.campus.proyecto_springboot.service.Usuario;

import com.campus.proyecto_springboot.model.Usuario;

import java.util.List;


public interface UsuarioService {
    List<Usuario> findAll();
    Usuario findById(Long id);
    Usuario save(Usuario usuario);
    void deleteById(Long id);
    Usuario findByDocumento(String documento);
}
