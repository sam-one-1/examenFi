package com.campus.proyecto_springboot.security;
import com.campus.proyecto_springboot.model.Usuario;
import com.campus.proyecto_springboot.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UsuarioRepository usuarioRepository;

    public CurrentUserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        // Esto asume que en tu UserDetails el "username" es el documento
        String documento = auth.getName(); // el subject del JWT / username

        Usuario usuario = usuarioRepository.findByDocumento(documento);

        return usuario != null ? usuario.getId() : null;
    }
}
