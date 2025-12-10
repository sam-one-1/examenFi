package com.campus.proyecto_springboot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bodegas")
public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String ubicacion;

    private Integer capacidad;

    @ManyToOne
    @JoinColumn(name = "encargado_id")
    private Usuario encargado;

    @Override
    public int hashCode() {
        return (id == null)? 0 :id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bodega)) return false;
        Bodega other = (Bodega) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public String toString() {
        return "Bodega{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", encargado=" + encargado +
                '}';
    }
}
