package com.campus.proyecto_springboot.dto;

import com.campus.proyecto_springboot.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CambiarRolRequest {
    private Role nuevoRol;
}

