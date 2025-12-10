package com.campus.proyecto_springboot.service.Bodega;

import com.campus.proyecto_springboot.model.Bodega;
import com.campus.proyecto_springboot.model.Producto;
import com.campus.proyecto_springboot.repository.BodegaRepository;
import com.campus.proyecto_springboot.repository.ProductoRepository;
import com.campus.proyecto_springboot.security.CurrentUserService;
import com.campus.proyecto_springboot.service.Auditoria.AuditoriaContextService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BodegaServiceImpl implements BodegaService{

    @Autowired
    private BodegaRepository bodegaRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private AuditoriaContextService auditoriaContextService;

    @Override
    public List<Bodega> findAll(){
        return bodegaRepository.findAll();
    }

    @Override
    public Bodega findById(Long id){
        return bodegaRepository.findById(id).orElse(null);
    }

    @Override
    public Bodega findByNombre(String nombre){
        return bodegaRepository.findByNombre(nombre);
    }

    @Override
    @Transactional
    public Bodega save(Bodega bodega){
        Long userId = currentUserService.getCurrentUserId();
        auditoriaContextService.setUsuarioActual(userId);
        return bodegaRepository.save(bodega);
    }

    @Override
    @Transactional
    public void deleteById(Long id){
        Long userId = currentUserService.getCurrentUserId();
        auditoriaContextService.setUsuarioActual(userId);
        bodegaRepository.deleteById(id);
    }

}
