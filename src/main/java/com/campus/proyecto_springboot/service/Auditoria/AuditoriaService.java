package com.campus.proyecto_springboot.service.Auditoria;

import com.campus.proyecto_springboot.model.Auditoria;
import com.campus.proyecto_springboot.model.Bodega;
import com.campus.proyecto_springboot.model.TipoOperacion;

import java.util.List;

public interface AuditoriaService {

    List<Auditoria> findAll();
    Auditoria findById(Long id);
    Auditoria save(Auditoria auditoria);
    void deleteById(Long id);
    List<Auditoria> findByUsuarioDocumento(String documento);

    List<Auditoria> findByTipoOperacion(TipoOperacion tipoOperacion);
}
