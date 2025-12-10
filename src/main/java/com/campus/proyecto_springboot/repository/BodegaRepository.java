package com.campus.proyecto_springboot.repository;

import com.campus.proyecto_springboot.model.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BodegaRepository extends JpaRepository<Bodega, Long> {

    Bodega findByNombre(String nombre);

}
