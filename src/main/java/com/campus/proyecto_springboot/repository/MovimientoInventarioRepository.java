package com.campus.proyecto_springboot.repository;

import com.campus.proyecto_springboot.model.MovimientoInventario;
import com.campus.proyecto_springboot.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long>, JpaSpecificationExecutor<MovimientoInventario> {

    List<MovimientoInventario> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);

    List<MovimientoInventario> findByUsuarioResponsableIdAndFechaBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);
}
