package com.campus.proyecto_springboot.service.Usuario;

import com.campus.proyecto_springboot.model.Role;
import com.campus.proyecto_springboot.model.Usuario;
import com.campus.proyecto_springboot.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }

    public Usuario register(String nombre, String password, String documento, Role role){
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setPassword(encoder.encode(password));
        u.setDocumento(documento);
        u.setRole(role);

        return usuarioRepository.save(u);
    }

    @Override
    public Usuario findById(Long id){
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario findByDocumento(String documento){
        return usuarioRepository.findByDocumento(documento);
    }

    @Override
    public Usuario save(Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id){
        usuarioRepository.deleteById(id);
    }

    public boolean checkPassword(String raw, String encoded){
        return encoder.matches(raw,encoded);
    }

}
