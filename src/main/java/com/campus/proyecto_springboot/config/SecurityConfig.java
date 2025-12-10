// config/SecurityConfig.java
package com.campus.proyecto_springboot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // públicos
                        .requestMatchers("/auth/**").permitAll()
                        // Swagger/OpenAPI - acceso público
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/swagger-ui/index.html").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/v3/api-docs", "/api-docs/**", "/api-docs").permitAll()

                        .requestMatchers("/swagger-resources/**", "/webjars/**", "/configuration/**").permitAll()

                        // 👉 Movimientos: accesibles a ADMIN o a quienes tengan permisos de movimiento
                        .requestMatchers(HttpMethod.GET, "/movimientoInventario/**")
                        .hasAnyAuthority("READ_MOVIMIENTO_INVENTARIO", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/movimientoInventario/**")
                        .hasAnyAuthority("CREATE_MOVIMIENTO_INVENTARIO", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/movimientoInventario/**")
                        .hasAnyAuthority("DELETE_MOVIMIENTO_INVENTARIO", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/movimientoInventario/**")
                        .hasAnyAuthority("CREATE_MOVIMIENTO_INVENTARIO", "ROLE_ADMIN") // o UPDATE si lo defines

                        // 👉 Bodegas: GET permitido para todos autenticados, POST/PUT/DELETE solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/bodegas/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/bodegas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/bodegas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/bodegas/**").hasRole("ADMIN")
                        
                        // 👉 Productos: GET permitido para todos autenticados, POST/PUT/DELETE solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/productos/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/productos/**").hasRole("ADMIN")
                        
                        // 👉 Auditorías: SOLO ADMIN
                        .requestMatchers("/auditorias/**").hasRole("ADMIN")
                        
                        // 👉 Usuarios: GET permitido para todos autenticados (para cargar lista), POST/PUT/DELETE/PATCH solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/usuarios/**").hasRole("ADMIN")

                        // cualquier otra ruta autenticada
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();

        c.setAllowedOrigins(List.of(
                "http://localhost:5500",
                "http://127.0.0.1:5500"
        ));
        c.setAllowedMethods(List.of("GET","PATCH", "POST", "PUT", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);

        return src;
    }
}
