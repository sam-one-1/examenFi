package com.campus.proyecto_springboot.service.MovimientoInventario;

import com.campus.proyecto_springboot.examen.MovimientoSpecification;
import com.campus.proyecto_springboot.exception.InvalidInputException;
import com.campus.proyecto_springboot.exception.ResourceNotFoundException;
import com.campus.proyecto_springboot.model.*;
import com.campus.proyecto_springboot.repository.MovimientoInventarioRepository;
import com.campus.proyecto_springboot.repository.ProductoRepository;
import com.campus.proyecto_springboot.repository.UsuarioRepository;
import com.campus.proyecto_springboot.security.CurrentUserService;
import com.campus.proyecto_springboot.service.Auditoria.AuditoriaContextService;
import com.campus.proyecto_springboot.service.StockBodega.StockBodegaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    @Autowired
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private AuditoriaContextService auditoriaContextService;

    @Autowired
    private StockBodegaService stockBodegaService;

    @Override
    public List<MovimientoInventario> findAll() {
        return movimientoInventarioRepository.findAll();
    }

    @Override
    public MovimientoInventario findById(Long id) {
        return movimientoInventarioRepository.findById(id).orElse(null);
    }

    /**
     * Registra un nuevo movimiento de inventario y actualiza el stock de los productos.
     * IMPORTANTE: pensado para ALTAS (movimientos nuevos). Si llamas a este método con un id != null,
     * no se recalcula el stock (para evitar inconsistencias).
     */
    @Override
    @Transactional
    public MovimientoInventario save(MovimientoInventario movimientoInventario) {

        Long userId = currentUserService.getCurrentUserId();
        auditoriaContextService.setUsuarioActual(userId);
        // Solo aplicamos lógica de stock cuando es un movimiento nuevo
        if (movimientoInventario.getId() != null) {
            // Si quieres, aquí puedes lanzar excepción para prohibir actualizaciones:
            // throw new InvalidInputException("No se permite modificar movimientos ya registrados");
            return movimientoInventarioRepository.save(movimientoInventario);
        }

        // Validar tipo de movimiento
        if (movimientoInventario.getTipoMovimiento() == null) {
            throw new InvalidInputException("El tipo de movimiento es obligatorio.");
        }

        TipoMovimiento tipo = movimientoInventario.getTipoMovimiento();

        // Validar bodegas según el tipo
        switch (tipo) {
            case ENTRADA -> {
                if (movimientoInventario.getBodegaDestino() == null) {
                    throw new InvalidInputException("La bodega destino es obligatoria para movimientos de ENTRADA.");
                }
            }
            case SALIDA -> {
                if (movimientoInventario.getBodegaOrigen() == null) {
                    throw new InvalidInputException("La bodega origen es obligatoria para movimientos de SALIDA.");
                }
            }
            case TRANSFERENCIA -> {
                if (movimientoInventario.getBodegaOrigen() == null ||
                        movimientoInventario.getBodegaDestino() == null) {
                    throw new InvalidInputException("La bodega origen y destino son obligatorias para TRANSFERENCIA.");
                }
            }
        }

        // Asignar fecha si no viene
        if (movimientoInventario.getFecha() == null) {
            movimientoInventario.setFecha(LocalDateTime.now());
        }

        // Asignar usuario responsable desde el contexto de seguridad si no viene
        if (movimientoInventario.getUsuarioResponsable() == null) {
            Usuario actual = obtenerUsuarioActual();
            if (actual == null) {
                throw new InvalidInputException("No se pudo determinar el usuario responsable del movimiento.");
            }
            movimientoInventario.setUsuarioResponsable(actual);
        }

        // Validar que tenga al menos un detalle
        if (movimientoInventario.getDetalles() == null ||
                movimientoInventario.getDetalles().isEmpty()) {
            throw new InvalidInputException("El movimiento debe tener al menos un producto en los detalles.");
        }

        // Procesar cada detalle: actualizar stock por bodega y setear la relación inversa
        for (DetalleMovimiento detalle : movimientoInventario.getDetalles()) {

            if (detalle.getProducto() == null || detalle.getProducto().getId() == null) {
                throw new InvalidInputException("Cada detalle debe tener un producto con id.");
            }

            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con id: " + detalle.getProducto().getId()));

            int cantidad = detalle.getCantidad();
            if (cantidad <= 0) {
                throw new InvalidInputException("La cantidad debe ser mayor a cero.");
            }

            Long productoId = producto.getId();
            Long bodegaOrigenId = movimientoInventario.getBodegaOrigen() != null 
                    ? movimientoInventario.getBodegaOrigen().getId() : null;
            Long bodegaDestinoId = movimientoInventario.getBodegaDestino() != null 
                    ? movimientoInventario.getBodegaDestino().getId() : null;

            // Actualizar stock por bodega según el tipo de movimiento
            switch (tipo) {
                case ENTRADA -> {
                    // ENTRADA: aumenta el stock en la bodega destino
                    if (bodegaDestinoId == null) {
                        throw new InvalidInputException("La bodega destino es obligatoria para movimientos de ENTRADA.");
                    }
                    
                    // Validar capacidad de la bodega destino
                    if (!stockBodegaService.validarCapacidadBodega(bodegaDestinoId, cantidad)) {
                        Integer stockActual = stockBodegaService.calcularStockTotalBodega(bodegaDestinoId);
                        throw new InvalidInputException(
                                String.format("La bodega destino no tiene capacidad suficiente. Capacidad: %d, Stock actual: %d, Intento agregar: %d",
                                        movimientoInventario.getBodegaDestino().getCapacidad() != null 
                                                ? movimientoInventario.getBodegaDestino().getCapacidad() : 0,
                                        stockActual, cantidad));
                    }
                    
                    // Incrementar stock en bodega destino
                    stockBodegaService.incrementarStock(productoId, bodegaDestinoId, cantidad);
                    
                    // Actualizar stock total del producto (sumar)
                    actualizarStockTotalProducto(producto);
                }
                case SALIDA -> {
                    // SALIDA: disminuye el stock en la bodega origen
                    if (bodegaOrigenId == null) {
                        throw new InvalidInputException("La bodega origen es obligatoria para movimientos de SALIDA.");
                    }
                    
                    // Validar que haya stock suficiente en la bodega origen
                    if (!stockBodegaService.validarStockDisponible(productoId, bodegaOrigenId, cantidad)) {
                        Integer stockDisponible = stockBodegaService.obtenerStock(productoId, bodegaOrigenId)
                                .map(StockBodega::getCantidad)
                                .orElse(0);
                        throw new InvalidInputException(
                                String.format("Stock insuficiente en la bodega origen. Disponible: %d, Requerido: %d",
                                        stockDisponible, cantidad));
                    }
                    
                    // Decrementar stock en bodega origen
                    stockBodegaService.decrementarStock(productoId, bodegaOrigenId, cantidad);
                    
                    // Actualizar stock total del producto (restar)
                    actualizarStockTotalProducto(producto);
                }
                case TRANSFERENCIA -> {
                    // TRANSFERENCIA: disminuye stock en origen y aumenta en destino
                    if (bodegaOrigenId == null || bodegaDestinoId == null) {
                        throw new InvalidInputException("La bodega origen y destino son obligatorias para TRANSFERENCIA.");
                    }
                    
                    // Validar que haya stock suficiente en la bodega origen
                    if (!stockBodegaService.validarStockDisponible(productoId, bodegaOrigenId, cantidad)) {
                        Integer stockDisponible = stockBodegaService.obtenerStock(productoId, bodegaOrigenId)
                                .map(StockBodega::getCantidad)
                                .orElse(0);
                        throw new InvalidInputException(
                                String.format("Stock insuficiente en la bodega origen para transferencia. Disponible: %d, Requerido: %d",
                                        stockDisponible, cantidad));
                    }
                    
                    // Validar capacidad de la bodega destino
                    if (!stockBodegaService.validarCapacidadBodega(bodegaDestinoId, cantidad)) {
                        Integer stockActual = stockBodegaService.calcularStockTotalBodega(bodegaDestinoId);
                        throw new InvalidInputException(
                                String.format("La bodega destino no tiene capacidad suficiente para la transferencia. Capacidad: %d, Stock actual: %d, Intento agregar: %d",
                                        movimientoInventario.getBodegaDestino().getCapacidad() != null 
                                                ? movimientoInventario.getBodegaDestino().getCapacidad() : 0,
                                        stockActual, cantidad));
                    }
                    
                    // Decrementar en origen y incrementar en destino
                    stockBodegaService.decrementarStock(productoId, bodegaOrigenId, cantidad);
                    stockBodegaService.incrementarStock(productoId, bodegaDestinoId, cantidad);
                    
                    // En TRANSFERENCIA el stock total del producto no cambia (solo se mueve entre bodegas)
                    // Pero actualizamos el valor para mantener consistencia
                    actualizarStockTotalProducto(producto);
                }
            }

            // Setear la relación inversa para que se guarde el detalle junto al movimiento
            detalle.setMovimiento(movimientoInventario);
            // Aseguramos que el producto del detalle sea el manejado por JPA
            detalle.setProducto(producto);
        }

        // Guardar el movimiento (gracias a cascade, también se guardan los detalles)
        return movimientoInventarioRepository.save(movimientoInventario);
    }

    @Override
    public void deleteById(Long id) {
        Long userId = currentUserService.getCurrentUserId();
        auditoriaContextService.setUsuarioActual(userId);
        movimientoInventarioRepository.deleteById(id);
    }

    @Override
    public List<MovimientoInventario> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta) {
        return movimientoInventarioRepository.findByFechaBetween(desde, hasta);
    }

    /**
     * Obtiene el usuario actual desde el token JWT (SecurityContext).
     */
    private Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        String documento = auth.getName(); // en tu JwtFilter usas documento como "username"
        return usuarioRepository.findByDocumento(documento);
    }

    /**
     * Actualiza el stock total del producto sumando el stock de todas las bodegas.
     * Este método recalcula el stock total basándose en el stock real en todas las bodegas.
     */
    private void actualizarStockTotalProducto(Producto producto) {
        // Obtener el stock del producto en todas las bodegas
        List<StockBodega> stockList = stockBodegaService.obtenerStockPorProducto(producto.getId());
        
        // Calcular el stock total sumando todas las bodegas
        Integer stockTotal = stockList.stream()
                .mapToInt(StockBodega::getCantidad)
                .sum();
        
        // Actualizar el campo stock del producto
        producto.setStock(stockTotal);
        productoRepository.save(producto);
    }

    @Override
    public List<MovimientoInventario> findByUsuarioResponsableIdAndFechaBetween(Long usuarioId, LocalDate fechaInicio, LocalDate fechaFin){
        LocalDateTime inicio = (fechaInicio != null) ?
                fechaInicio.atStartOfDay() : LocalDateTime.of(2000, 1, 1, 0, 0);

        LocalDateTime fin = (fechaFin != null) ?
                fechaFin.atTime(23, 59, 59) : LocalDateTime.now();

        return movimientoInventarioRepository
                .findByUsuarioResponsableIdAndFechaBetween(usuarioId, inicio, fin);

    }

    /**
     * Transfiere stock de una bodega a otra de forma atómica.
     * Si algo falla al sumar en destino, todo se revierte automáticamente (rollback).
     */
    @Override
    @Transactional
    public void realizarTransferencia(Long idOrigen, Long idDestino, Long idProducto, int cantidad) {
        // Validación básica de parámetros
        if (idOrigen == null || idDestino == null || idProducto == null) {
            throw new InvalidInputException("Origen, destino y producto son obligatorios.");
        }
        if (idOrigen.equals(idDestino)) {
            throw new InvalidInputException("La bodega origen y destino deben ser distintas.");
        }
        if (cantidad <= 0) {
            throw new InvalidInputException("La cantidad debe ser mayor a cero.");
        }

        // Obtener producto real para asegurar existencia
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + idProducto));

        // Validar stock en origen antes de mover
        if (!stockBodegaService.validarStockDisponible(idProducto, idOrigen, cantidad)) {
            Integer stockDisponible = stockBodegaService.obtenerStock(idProducto, idOrigen)
                    .map(StockBodega::getCantidad)
                    .orElse(0);
            throw new InvalidInputException(
                    String.format("Stock insuficiente en bodega origen. Disponible: %d, Requerido: %d",
                            stockDisponible, cantidad));
        }

        // Validar capacidad en destino antes de mover
        if (!stockBodegaService.validarCapacidadBodega(idDestino, cantidad)) {
            Integer stockActualDestino = stockBodegaService.calcularStockTotalBodega(idDestino);
            throw new InvalidInputException(
                    String.format("La bodega destino no tiene capacidad. Stock actual: %d, Intento agregar: %d",
                            stockActualDestino, cantidad));
        }

        // 1) Restar en origen
        stockBodegaService.decrementarStock(idProducto, idOrigen, cantidad);

        // 2) Sumar en destino
        stockBodegaService.incrementarStock(idProducto, idDestino, cantidad);

        // Mantener stock total del producto consistente
        actualizarStockTotalProducto(producto);

        // Registrar movimientos de salida y entrada para trazabilidad
        Usuario responsable = obtenerUsuarioActual();
        if (responsable == null) {
            throw new InvalidInputException("No se pudo determinar el usuario responsable.");
        }

        MovimientoInventario salida = construirMovimiento(
                TipoMovimiento.SALIDA, idOrigen, null, producto, cantidad, responsable);
        movimientoInventarioRepository.save(salida);

        MovimientoInventario entrada = construirMovimiento(
                TipoMovimiento.ENTRADA, null, idDestino, producto, cantidad, responsable);
        movimientoInventarioRepository.save(entrada);
    }

    // Crea un movimiento simple con un solo detalle
    private MovimientoInventario construirMovimiento(
            TipoMovimiento tipo, Long bodegaOrigenId, Long bodegaDestinoId,
            Producto producto, int cantidad, Usuario responsable) {

        MovimientoInventario mov = new MovimientoInventario();
        mov.setTipoMovimiento(tipo);
        mov.setFecha(LocalDateTime.now());
        mov.setUsuarioResponsable(responsable);

        if (bodegaOrigenId != null) {
            Bodega origen = new Bodega();
            origen.setId(bodegaOrigenId);
            mov.setBodegaOrigen(origen);
        }
        if (bodegaDestinoId != null) {
            Bodega destino = new Bodega();
            destino.setId(bodegaDestinoId);
            mov.setBodegaDestino(destino);
        }

        DetalleMovimiento detalle = new DetalleMovimiento();
        detalle.setCantidad(cantidad);
        detalle.setProducto(producto);
        detalle.setMovimiento(mov);

        List<DetalleMovimiento> detalles = new ArrayList<>();
        detalles.add(detalle);
        mov.setDetalles(detalles);

        return mov;
    }

    @Override
    public List<MovimientoInventario> buscarPorFiltros(TipoMovimiento tipo, LocalDateTime desde, LocalDateTime hasta, Long productoId) {
        // Combina los filtros que lleguen; los nulos se ignoran
        Specification<MovimientoInventario> spec = Specification.allOf(
                MovimientoSpecification.tipoEs(tipo),
                MovimientoSpecification.fechaEntre(desde, hasta),
                MovimientoSpecification.productoEs(productoId)
        );

        return movimientoInventarioRepository.findAll(spec);
    }
}
