package com.campus.proyecto_springboot.service.Auditoria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaContextService {

    @PersistenceContext
    private EntityManager entityManager;

    public void setUsuarioActual(Long usuarioId) {
        String value = (usuarioId != null) ? usuarioId.toString() : "";

        // set_config('app.current_user_id', '<id>', true)
        entityManager.createNativeQuery("SELECT set_config('app.current_user_id', :id, true)")
                .setParameter("id", value)
                .getSingleResult();
    }
}
