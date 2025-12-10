// config/JwtFilter.java
package com.campus.proyecto_springboot.config;

import com.campus.proyecto_springboot.service.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String documento = jwtUtil.getDocumento(token);
                String role = jwtUtil.getRole(token);
                List<String> authoritiesFromToken = jwtUtil.getAuthorities(token);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                // Rol como ROLE_ADMIN / ROLE_EMPLEADO
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

                if (authoritiesFromToken != null) {
                    authorities.addAll(
                            authoritiesFromToken.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList())
                    );
                }

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        documento,
                        null,
                        authorities
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                System.out.println("Token inválido: " + e.getMessage());
            }
        }

        chain.doFilter(req, res);
    }
}
