package com.campus.proyecto_springboot.controller;

import com.campus.proyecto_springboot.dto.MovimientoInventarioDTO;
import com.campus.proyecto_springboot.dto.TransferenciaRequest;
import com.campus.proyecto_springboot.exception.ResourceNotFoundException;
import com.campus.proyecto_springboot.model.MovimientoInventario;
import com.campus.proyecto_springboot.service.MovimientoInventario.MovimientoInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/movimientoInventario", "/api/movimientos"})
public class MovimientoInventarioController {

    @Autowired
    private MovimientoInventarioService movimientoInventarioService;

    // Listar todos los movimientos
    @GetMapping
    public List<MovimientoInventario> listarMovimientos() {
        return movimientoInventarioService.findAll();  // <-- Se serializa a JSON automáticamente
    }

    // Buscar movimiento por ID
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventario> buscarPorId(@PathVariable Long id) {
        MovimientoInventario movimiento = movimientoInventarioService.findById(id);

        if (movimiento == null) {
            throw new ResourceNotFoundException("Movimiento de inventario no encontrado con id: " + id);
        }

        return ResponseEntity.ok(movimiento);
    }

    // Registrar un nuevo movimiento de inventario (aplica lógica de stock)
    @PostMapping
    public ResponseEntity<MovimientoInventario> crearMovimiento(
            @RequestBody MovimientoInventario movimiento) {

        MovimientoInventario creado = movimientoInventarioService.save(movimiento);
        return ResponseEntity.ok(creado);  // <-- Spring/Jackson lo serializa a JSON válido
    }

    /**
     * Actualización "ligera" de un movimiento existente.
     * - No se cambia tipoMovimiento ni detalles (para no desbalancear stock).
     * - Útil si necesitas corregir la fecha o las bodegas.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MovimientoInventario> actualizar(
            @PathVariable Long id,
            @RequestBody MovimientoInventario movimientoInventario) {

        MovimientoInventario existente = movimientoInventarioService.findById(id);

        if (existente == null) {
            throw new ResourceNotFoundException("Movimiento de inventario no encontrado con id: " + id);
        }

        // Forzamos el id para que el service sepa que es actualización
        movimientoInventario.setId(id);
        MovimientoInventario actualizado = movimientoInventarioService.save(movimientoInventario);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar movimiento por ID (no recalcula stock)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        MovimientoInventario existente = movimientoInventarioService.findById(id);

        if (existente == null) {
            throw new ResourceNotFoundException("Movimiento de inventario no encontrado con id: " + id);
        }

        movimientoInventarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Movimientos por rango de fechas
    @GetMapping("/por-fecha")
    public ResponseEntity<List<MovimientoInventario>> listarPorRango(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime desde,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime hasta) {

        List<MovimientoInventario> movimientos =
                movimientoInventarioService.findByFechaBetween(desde, hasta);
        return ResponseEntity.ok(movimientos);
    }

    // Transferencia atómica entre bodegas
    @PostMapping("/transferir")
    public ResponseEntity<Map<String, Object>> transferir(@RequestBody TransferenciaRequest request) {
        movimientoInventarioService.realizarTransferencia(
                request.getBodegaOrigenId(),
                request.getBodegaDestinoId(),
                request.getProductoId(),
                request.getCantidad()
        );

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("mensaje", "Transferencia realizada correctamente");
        return ResponseEntity.ok(res);
    }


    // ==========================================
    // 🔹 MAPPER → convertir entidad → DTO
    // ==========================================
    private MovimientoInventarioDTO mapToDto(MovimientoInventario mov) {

        MovimientoInventarioDTO dto = new MovimientoInventarioDTO();

        dto.setId(mov.getId());
        dto.setTipoMovimiento(mov.getTipoMovimiento().name());
        dto.setFecha(mov.getFecha());

        if (mov.getUsuarioResponsable() != null) {
            dto.setUsuarioId(mov.getUsuarioResponsable().getId());
        }

        // Sumar cantidades desde los detalles
        int total = mov.getDetalles()
                .stream()
                .mapToInt(d -> d.getCantidad())
                .sum();

        dto.setCantidadTotal(total);

        return dto;
    }

    // ==========================================
    // 🔹 ENDPOINT: rendimiento por empleado
    // ==========================================
    @GetMapping("/rendimiento-empleado")
    public ResponseEntity<?> getRendimientoEmpleado(
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam(value = "fechaInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(value = "fechaFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        try {
            List<MovimientoInventario> movimientos =
                    movimientoInventarioService.findByUsuarioResponsableIdAndFechaBetween(
                            usuarioId, fechaInicio, fechaFin);

            List<MovimientoInventarioDTO> movimientosDTO =
                    movimientos.stream().map(this::mapToDto).toList();

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("usuarioId", usuarioId);
            res.put("totalMovimientos", movimientosDTO.size());
            res.put("movimientos", movimientosDTO);

            if (fechaInicio != null) res.put("fechaInicio", fechaInicio);
            if (fechaFin != null) res.put("fechaFin", fechaFin);

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", e.getMessage());
            return ResponseEntity.status(500).body(err);
        }
    }


}
