package com.campus.proyecto_springboot.service.StockBodega;

import com.campus.proyecto_springboot.exception.InvalidInputException;
import com.campus.proyecto_springboot.exception.ResourceNotFoundException;
import com.campus.proyecto_springboot.model.Bodega;
import com.campus.proyecto_springboot.model.Producto;
import com.campus.proyecto_springboot.model.StockBodega;
import com.campus.proyecto_springboot.repository.BodegaRepository;
import com.campus.proyecto_springboot.repository.ProductoRepository;
import com.campus.proyecto_springboot.repository.StockBodegaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockBodegaServiceImpl implements StockBodegaService {

    @Autowired
    private StockBodegaRepository stockBodegaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private BodegaRepository bodegaRepository;

    @Override
    @Transactional
    public StockBodega obtenerOCrearStock(Long productoId, Long bodegaId) {
        Optional<StockBodega> stockExistente = stockBodegaRepository.findByProductoIdAndBodegaId(productoId, bodegaId);
        
        if (stockExistente.isPresent()) {
            return stockExistente.get();
        }
        
        // Crear nuevo registro de stock
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productoId));
        
        Bodega bodega = bodegaRepository.findById(bodegaId)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con id: " + bodegaId));
        
        StockBodega nuevoStock = new StockBodega();
        nuevoStock.setProducto(producto);
        nuevoStock.setBodega(bodega);
        nuevoStock.setCantidad(0);
        
        return stockBodegaRepository.save(nuevoStock);
    }

    @Override
    public Optional<StockBodega> obtenerStock(Long productoId, Long bodegaId) {
        return stockBodegaRepository.findByProductoIdAndBodegaId(productoId, bodegaId);
    }

    @Override
    @Transactional
    public StockBodega actualizarStock(Long productoId, Long bodegaId, Integer cantidad) {
        if (cantidad < 0) {
            throw new InvalidInputException("La cantidad no puede ser negativa.");
        }
        
        StockBodega stock = obtenerOCrearStock(productoId, bodegaId);
        stock.setCantidad(cantidad);
        return stockBodegaRepository.save(stock);
    }

    @Override
    @Transactional
    public StockBodega incrementarStock(Long productoId, Long bodegaId, Integer cantidad) {
        if (cantidad <= 0) {
            throw new InvalidInputException("La cantidad a incrementar debe ser mayor a cero.");
        }
        
        StockBodega stock = obtenerOCrearStock(productoId, bodegaId);
        stock.setCantidad(stock.getCantidad() + cantidad);
        return stockBodegaRepository.save(stock);
    }

    @Override
    @Transactional
    public StockBodega decrementarStock(Long productoId, Long bodegaId, Integer cantidad) {
        if (cantidad <= 0) {
            throw new InvalidInputException("La cantidad a decrementar debe ser mayor a cero.");
        }
        
        StockBodega stock = obtenerOCrearStock(productoId, bodegaId);
        
        if (stock.getCantidad() < cantidad) {
            throw new InvalidInputException(
                    String.format("Stock insuficiente. Disponible: %d, Requerido: %d", 
                            stock.getCantidad(), cantidad));
        }
        
        stock.setCantidad(stock.getCantidad() - cantidad);
        return stockBodegaRepository.save(stock);
    }

    @Override
    public List<StockBodega> obtenerStockPorProducto(Long productoId) {
        return stockBodegaRepository.findByProductoId(productoId);
    }

    @Override
    public List<StockBodega> obtenerStockPorBodega(Long bodegaId) {
        return stockBodegaRepository.findByBodegaId(bodegaId);
    }

    @Override
    public Integer calcularStockTotalBodega(Long bodegaId) {
        Integer total = stockBodegaRepository.calcularStockTotalBodega(bodegaId);
        return total != null ? total : 0;
    }

    @Override
    public boolean validarStockDisponible(Long productoId, Long bodegaId, Integer cantidadRequerida) {
        Optional<StockBodega> stock = obtenerStock(productoId, bodegaId);
        if (stock.isEmpty()) {
            return cantidadRequerida == 0;
        }
        return stock.get().getCantidad() >= cantidadRequerida;
    }

    @Override
    public boolean validarCapacidadBodega(Long bodegaId, Integer cantidadAAgregar) {
        Bodega bodega = bodegaRepository.findById(bodegaId)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con id: " + bodegaId));
        
        // Si la capacidad es null o 0, se considera ilimitada
        if (bodega.getCapacidad() == null || bodega.getCapacidad() == 0) {
            return true;
        }
        
        Integer stockActual = calcularStockTotalBodega(bodegaId);
        return (stockActual + cantidadAAgregar) <= bodega.getCapacidad();
    }
}

