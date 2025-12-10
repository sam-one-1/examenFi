 package com.campus.proyecto_springboot.controller;

import com.campus.proyecto_springboot.dto.CrearProductoRequest;
import com.campus.proyecto_springboot.exception.InvalidInputException;
import com.campus.proyecto_springboot.exception.ResourceNotFoundException;
import com.campus.proyecto_springboot.model.Producto;
import com.campus.proyecto_springboot.model.Proveedor;
import com.campus.proyecto_springboot.model.StockBodega;
import com.campus.proyecto_springboot.service.Producto.ProductoService;
import com.campus.proyecto_springboot.service.Proveedor.ProveedorService;
import com.campus.proyecto_springboot.service.StockBodega.StockBodegaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/productos", "/api/productos"})
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private StockBodegaService stockBodegaService;

    @Autowired
    private ProveedorService proveedorService;

    // Búsqueda dinámica por categoría, precio y bodega
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Double precioMaximo,
            @RequestParam(required = false) Long bodegaId
    ) {
        List<Producto> resultados = productoService.buscarPorFiltros(categoria, precioMaximo, bodegaId);
        return ResponseEntity.ok(resultados);
    }

    // Obtener todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> getAll(){
        return ResponseEntity.ok(productoService.findAll());
    }

    // Obtener stock de un producto en todas las bodegas (debe ir antes de /{id})
    @GetMapping("/{id}/stock")
    public ResponseEntity<List<StockBodegaInfo>> getStockByProducto(@PathVariable Long id) {
        List<StockBodega> stockList = stockBodegaService.obtenerStockPorProducto(id);
        
        List<StockBodegaInfo> stockInfo = stockList.stream()
                .map(sb -> new StockBodegaInfo(
                        sb.getBodega().getId(),
                        sb.getBodega().getNombre(),
                        sb.getCantidad()
                ))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(stockInfo);
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getById(@PathVariable Long id){
        Producto producto = productoService.findById(id);

        if (producto == null){
            // Lanza la excepción si el producto no es encontrado
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        return ResponseEntity.ok(producto);
    }

    // Obtener producto por nombre
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Producto> getByNombre(@PathVariable String nombre){
        Producto producto = productoService.findByNombre(nombre);

        if (producto == null){
            // Lanza la excepción si el producto no es encontrado
            throw new ResourceNotFoundException("Producto no encontrado con nombre: " + nombre);
        }
        return ResponseEntity.ok(producto);
    }

    // Crear un nuevo producto
    @PostMapping
    @Transactional
    public ResponseEntity<Producto> create(@RequestBody CrearProductoRequest request){
        // Validación para comprobar que el nombre y categoría no sean nulos
        if (request.getNombre() == null || request.getCategoria() == null) {
            throw new InvalidInputException("Nombre y categoría son obligatorios.");
        }

        // VALIDAR CAPACIDAD DE LA BODEGA ANTES DE CREAR EL PRODUCTO
        // Si se especificó una bodega y stock inicial, validar capacidad primero
        if (request.getBodegaId() != null && request.getStock() != null && request.getStock() > 0) {
            // Validar capacidad de la bodega ANTES de crear el producto
            if (!stockBodegaService.validarCapacidadBodega(request.getBodegaId(), request.getStock())) {
                Integer stockActual = stockBodegaService.calcularStockTotalBodega(request.getBodegaId());
                // Intentar obtener la bodega para mostrar su capacidad
                String mensajeError = String.format(
                    "La bodega no tiene capacidad suficiente para almacenar el stock inicial. " +
                    "Stock a agregar: %d, Stock actual en bodega: %d. " +
                    "No se puede crear el producto.",
                    request.getStock(), stockActual
                );
                throw new InvalidInputException(mensajeError);
            }
        }

        // Crear el producto solo si la validación pasó
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setCategoria(request.getCategoria());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock() != null ? request.getStock() : 0);

        // Asignar proveedor si viene en la solicitud
        if (request.getProveedorId() != null) {
            Proveedor proveedor = proveedorService.findById(request.getProveedorId());
            if (proveedor == null) {
                throw new ResourceNotFoundException("Proveedor no encontrado con id: " + request.getProveedorId());
            }
            producto.setProveedor(proveedor);
        }

        Producto nuevo = productoService.save(producto);

        // Si se especificó una bodega y stock inicial, crear el stock en esa bodega
        if (request.getBodegaId() != null && request.getStock() != null && request.getStock() > 0) {
            // Crear el stock en la bodega (ya validado arriba)
            stockBodegaService.actualizarStock(nuevo.getId(), request.getBodegaId(), request.getStock());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // Clase interna para el DTO de respuesta
    public static class StockBodegaInfo {
        private Long bodegaId;
        private String bodegaNombre;
        private Integer cantidad;

        public StockBodegaInfo(Long bodegaId, String bodegaNombre, Integer cantidad) {
            this.bodegaId = bodegaId;
            this.bodegaNombre = bodegaNombre;
            this.cantidad = cantidad;
        }

        public Long getBodegaId() {
            return bodegaId;
        }

        public void setBodegaId(Long bodegaId) {
            this.bodegaId = bodegaId;
        }

        public String getBodegaNombre() {
            return bodegaNombre;
        }

        public void setBodegaNombre(String bodegaNombre) {
            this.bodegaNombre = bodegaNombre;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }

    // Actualizar producto por ID
    @PutMapping("/{id}")
    public ResponseEntity<Producto> update(@PathVariable Long id, @RequestBody Producto datosActualizados){
        Producto existente = productoService.findById(id);

        if (existente == null){
            // Lanza la excepción si el producto no existe
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }

        existente.setNombre(datosActualizados.getNombre());
        existente.setCategoria(datosActualizados.getCategoria());
        existente.setPrecio(datosActualizados.getPrecio());
        existente.setStock(datosActualizados.getStock());

        Producto actualizado = productoService.save(existente);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar producto por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        Producto existente = productoService.findById(id);

        if (existente == null){
            // Lanza la excepción si el producto no existe
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> productosConStockBajo(
            @RequestParam(defaultValue = "10") Integer limite) {

        // Obtener todos los productos
        List<Producto> todosProductos = productoService.findAll();
        
        // Filtrar productos cuyo stock total en todas las bodegas sea menor al límite
        List<Producto> productosStockBajo = todosProductos.stream()
                .filter(producto -> {
                    // Calcular stock total sumando todas las bodegas
                    List<StockBodega> stockList = stockBodegaService.obtenerStockPorProducto(producto.getId());
                    Integer stockTotal = stockList.stream()
                            .mapToInt(StockBodega::getCantidad)
                            .sum();
                    // Retornar true si el stock total es menor al límite
                    return stockTotal < limite;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(productosStockBajo);
    }
}

