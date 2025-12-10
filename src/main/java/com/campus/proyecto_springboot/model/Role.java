package com.campus.proyecto_springboot.model;

import java.util.Set;
import java.util.stream.Collectors;

public enum Role {

    // Admin tiene todos los permisos
    ADMIN(Set.of(Permission.READ_USERS,
            Permission.CREATE_USERS,
            Permission.DELETE_USERS,
            Permission.READ_MOVIMIENTO_INVENTARIO,
            Permission.CREATE_MOVIMIENTO_INVENTARIO,
            Permission.DELETE_MOVIMIENTO_INVENTARIO)),

    // Empleado solo tiene permisos sobre movimiento de inventario
    USER(Set.of(Permission.READ_MOVIMIENTO_INVENTARIO,
            Permission.CREATE_MOVIMIENTO_INVENTARIO,
            Permission.DELETE_MOVIMIENTO_INVENTARIO));


    public final Set<Permission> permissions;



    Role(Set<Permission> permissions){
        this.permissions = permissions;
    }

    public Set<String> getAuthorities() {
        // permisos
        Set<String> authorities = permissions.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        // rol (Spring requiere ROLE_)
        authorities.add("ROLE_" + this.name());

        return authorities;
    }

}
