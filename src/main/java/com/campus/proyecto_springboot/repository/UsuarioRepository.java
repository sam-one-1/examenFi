package com.campus.proyecto_springboot.repository;

import com.campus.proyecto_springboot.model.Role;
import com.campus.proyecto_springboot.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByDocumento(String documento);
    Usuario findByNombre(String nombre);
    List<Usuario> findByRole(Role role);
    boolean existsByRole(Role role);
}
