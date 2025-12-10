package com.campus.proyecto_springboot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "stock_bodega", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"producto_id", "bodega_id"})
})
public class StockBodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodega bodega;

    @Column(nullable = false)
    private Integer cantidad;

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockBodega)) return false;
        StockBodega other = (StockBodega) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public String toString() {
        return "StockBodega{" +
                "id=" + id +
                ", producto=" + (producto != null ? producto.getId() : null) +
                ", bodega=" + (bodega != null ? bodega.getId() : null) +
                ", cantidad=" + cantidad +
                '}';
    }
}

