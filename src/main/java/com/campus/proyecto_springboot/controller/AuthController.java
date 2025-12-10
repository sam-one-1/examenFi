package com.campus.proyecto_springboot.controller;


import com.campus.proyecto_springboot.dto.LoginRequest;
import com.campus.proyecto_springboot.dto.RegisterRequest;
import com.campus.proyecto_springboot.model.Usuario;
import com.campus.proyecto_springboot.service.JwtUtil;
import com.campus.proyecto_springboot.service.Usuario.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioServiceImpl usuarioService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Usuario register(@RequestBody RegisterRequest request){
        // Solo se pueden registrar usuarios con rol USER
        // Los ADMIN solo pueden ser creados o promovidos por otros ADMIN
        return usuarioService.register(
                request.getNombre(),
                request.getPassword(),
                request.getDocumento(),
                com.campus.proyecto_springboot.model.Role.USER
        );
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request){
        Usuario u = usuarioService.findByDocumento(request.getDocumento());

        if (u == null || !usuarioService.checkPassword(request.getPassword(), u.getPassword())){
            throw new RuntimeException("Credenciales invalidas");
        }

        return jwtUtil.generateToken(u.getDocumento(), u.getRole());
    }
}