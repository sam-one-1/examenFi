package com.campus.proyecto_springboot.repository;

import com.campus.proyecto_springboot.model.Auditoria;
import com.campus.proyecto_springboot.model.Producto;
import com.campus.proyecto_springboot.model.TipoOperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long>, JpaSpecificationExecutor<Auditoria> {
    @Query("SELECT DISTINCT a FROM Auditoria a LEFT JOIN FETCH a.usuarioResponsable ORDER BY a.fechaHora DESC")
    List<Auditoria> findAll();

    @Query("SELECT DISTINCT a FROM Auditoria a LEFT JOIN FETCH a.usuarioResponsable WHERE a.usuarioResponsable.documento = :documento ORDER BY a.fechaHora DESC")
    List<Auditoria> findByUsuarioResponsable_Documento(@Param("documento") String documento);

    @Query("SELECT DISTINCT a FROM Auditoria a LEFT JOIN FETCH a.usuarioResponsable WHERE a.tipoOperacion = :tipoOperacion ORDER BY a.fechaHora DESC")
    List<Auditoria> findByTipoOperacion(@Param("tipoOperacion") TipoOperacion tipoOperacion);
}
