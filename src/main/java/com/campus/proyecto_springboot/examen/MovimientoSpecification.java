package com.campus.proyecto_springboot.examen;

import com.campus.proyecto_springboot.model.MovimientoInventario;
import com.campus.proyecto_springboot.model.TipoMovimiento;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MovimientoSpecification {

    public static Specification<MovimientoInventario> tipoEs(TipoMovimiento tipo) {
        return (root, query, cb) ->
                (tipo == null) ? null : cb.equal(root.get("tipoMovimiento"), tipo);
    }

    public static Specification<MovimientoInventario> fechaEntre(LocalDateTime desde, LocalDateTime hasta) {
        return (root, query, cb) -> {
            if (desde == null && hasta == null) return null;

            if (desde != null && hasta != null)
                return cb.between(root.get("fecha"), desde, hasta);

            if (desde != null)
                return cb.greaterThanOrEqualTo(root.get("fecha"), desde);

            return cb.lessThanOrEqualTo(root.get("fecha"), hasta);
        };
    }

    public static Specification<MovimientoInventario> productoEs(Long productoId) {
        return (root, query, cb) ->
                (productoId == null) ? null : cb.equal(root.get("producto").get("id"), productoId);
    }

}
