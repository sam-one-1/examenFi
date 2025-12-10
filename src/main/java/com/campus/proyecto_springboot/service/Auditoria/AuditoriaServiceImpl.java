package com.campus.proyecto_springboot.service.Auditoria;

import com.campus.proyecto_springboot.model.Auditoria;
import com.campus.proyecto_springboot.model.Producto;
import com.campus.proyecto_springboot.model.TipoOperacion;
import com.campus.proyecto_springboot.repository.AuditoriaRepository;
import com.campus.proyecto_springboot.repository.ProductoRepository;
import com.campus.proyecto_springboot.service.Bodega.BodegaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Override
    public List<Auditoria> findAll(){
        return auditoriaRepository.findAll();
    }

    @Override
    public Auditoria findById(Long id){
        return auditoriaRepository.findById(id).orElse(null);
    }

    @Override
    public Auditoria save(Auditoria auditoria){
        return auditoriaRepository.save(auditoria);
    }

    @Override
    public void deleteById(Long id){
        auditoriaRepository.deleteById(id);
    }

    @Override
    public List<Auditoria> findByUsuarioDocumento(String documento) {
        return auditoriaRepository.findByUsuarioResponsable_Documento(documento);
    }

    @Override
    public List<Auditoria> findByTipoOperacion(TipoOperacion tipoOperacion) {
        return auditoriaRepository.findByTipoOperacion(tipoOperacion);
    }
}
