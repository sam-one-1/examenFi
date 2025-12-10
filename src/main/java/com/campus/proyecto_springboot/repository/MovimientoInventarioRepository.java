package com.campus.proyecto_springboot.repository;

import com.campus.proyecto_springboot.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);

    List<MovimientoInventario> findByUsuarioResponsableIdAndFechaBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);
}
