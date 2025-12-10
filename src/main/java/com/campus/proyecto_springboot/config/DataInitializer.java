package com.campus.proyecto_springboot.config;

import com.campus.proyecto_springboot.model.Role;
import com.campus.proyecto_springboot.model.Usuario;
import com.campus.proyecto_springboot.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Value("${app.root.user.documento:root}")
    private String rootDocumento;
    
    @Value("${app.root.user.password:admin123}")
    private String rootPassword;
    
    @Value("${app.root.user.nombre:Administrador Root}")
    private String rootNombre;

    public DataInitializer(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) throws Exception {
        initializeRootUser();
    }

    private void initializeRootUser() {
        // Verificar si existe algún usuario ADMIN
        boolean existsAdmin = usuarioRepository.existsByRole(Role.ADMIN);
        
        if (!existsAdmin) {
            logger.info("No se encontró ningún usuario ADMIN. Creando usuario root...");
            
            // Verificar si el usuario root ya existe por documento
            Usuario existingRoot = usuarioRepository.findByDocumento(rootDocumento);
            
            if (existingRoot == null) {
                // Crear usuario root
                Usuario rootUser = new Usuario();
                rootUser.setDocumento(rootDocumento);
                rootUser.setNombre(rootNombre);
                rootUser.setPassword(passwordEncoder.encode(rootPassword));
                rootUser.setRole(Role.ADMIN);
                
                usuarioRepository.save(rootUser);
                
                logger.info("✅ Usuario root creado exitosamente:");
                logger.info("   Documento: {}", rootDocumento);
                logger.info("   Nombre: {}", rootNombre);
                logger.info("   Password: {} (cambiar después del primer login)", rootPassword);
                logger.info("   Rol: ADMIN");
                logger.warn("⚠️  IMPORTANTE: Cambiar la contraseña del usuario root después del primer acceso");
            } else {
                // Si existe pero no es ADMIN, promocionarlo
                if (existingRoot.getRole() != Role.ADMIN) {
                    logger.info("Usuario root existe pero no es ADMIN. Promocionando a ADMIN...");
                    existingRoot.setRole(Role.ADMIN);
                    usuarioRepository.save(existingRoot);
                    logger.info("✅ Usuario root promocionado a ADMIN exitosamente");
                } else {
                    logger.info("Usuario root ya existe y es ADMIN. No se requiere acción.");
                }
            }
        } else {
            logger.info("✅ Ya existe al menos un usuario ADMIN. No se requiere crear usuario root.");
        }
    }
}

