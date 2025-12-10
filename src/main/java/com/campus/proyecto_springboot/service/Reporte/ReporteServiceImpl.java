package com.campus.proyecto_springboot.service.Reporte;

import com.campus.proyecto_springboot.dto.ProductoMasMovidoDTO;
import com.campus.proyecto_springboot.dto.ResumenGeneralDTO;
import com.campus.proyecto_springboot.dto.StockPorBodegaDTO;
import com.campus.proyecto_springboot.model.DetalleMovimiento;
import com.campus.proyecto_springboot.model.MovimientoInventario;
import com.campus.proyecto_springboot.model.TipoMovimiento;
import com.campus.proyecto_springboot.repository.MovimientoInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @Override
    public ResumenGeneralDTO obtenerResumenGeneral() {

        List<MovimientoInventario> movimientos = movimientoInventarioRepository.findAll();

        Map<Long, StockPorBodegaDTO> stockPorBodegaMap = new HashMap<>();
        Map<Long, ProductoMasMovidoDTO> productosMasMovidosMap = new HashMap<>();

        for (MovimientoInventario mov : movimientos) {
            TipoMovimiento tipo = mov.getTipoMovimiento();

            if (mov.getDetalles() == null) continue;

            for (DetalleMovimiento det : mov.getDetalles()) {
                int cantidad = det.getCantidad();

                // -------------------------------
                // 1. Productos más movidos (sumamos cantidad sin importar tipo/bodega)
                // -------------------------------
                if (det.getProducto() != null) {
                    Long prodId = det.getProducto().getId();
                    String prodNombre = det.getProducto().getNombre();

                    ProductoMasMovidoDTO dto =
                            productosMasMovidosMap.getOrDefault(prodId,
                                    new ProductoMasMovidoDTO(prodId, prodNombre, 0L));

                    dto.setTotalMovido(dto.getTotalMovido() + cantidad);
                    productosMasMovidosMap.put(prodId, dto);
                }

                // -------------------------------
                // 2. Stock por bodega (neto de movimientos)
                // -------------------------------
                switch (tipo) {
                    case ENTRADA -> {
                        if (mov.getBodegaDestino() != null) {
                            Long bodegaId = mov.getBodegaDestino().getId();
                            String nombre = mov.getBodegaDestino().getNombre();

                            StockPorBodegaDTO dto =
                                    stockPorBodegaMap.getOrDefault(bodegaId,
                                            new StockPorBodegaDTO(bodegaId, nombre, 0L));

                            dto.setStockTotal(dto.getStockTotal() + cantidad);
                            stockPorBodegaMap.put(bodegaId, dto);
                        }
                    }
                    case SALIDA -> {
                        if (mov.getBodegaOrigen() != null) {
                            Long bodegaId = mov.getBodegaOrigen().getId();
                            String nombre = mov.getBodegaOrigen().getNombre();

                            StockPorBodegaDTO dto =
                                    stockPorBodegaMap.getOrDefault(bodegaId,
                                            new StockPorBodegaDTO(bodegaId, nombre, 0L));

                            dto.setStockTotal(dto.getStockTotal() - cantidad);
                            stockPorBodegaMap.put(bodegaId, dto);
                        }
                    }
                    case TRANSFERENCIA -> {
                        // Origen: sale stock
                        if (mov.getBodegaOrigen() != null) {
                            Long bodegaId = mov.getBodegaOrigen().getId();
                            String nombre = mov.getBodegaOrigen().getNombre();

                            StockPorBodegaDTO dto =
                                    stockPorBodegaMap.getOrDefault(bodegaId,
                                            new StockPorBodegaDTO(bodegaId, nombre, 0L));

                            dto.setStockTotal(dto.getStockTotal() - cantidad);
                            stockPorBodegaMap.put(bodegaId, dto);
                        }

                        // Destino: entra stock
                        if (mov.getBodegaDestino() != null) {
                            Long bodegaId = mov.getBodegaDestino().getId();
                            String nombre = mov.getBodegaDestino().getNombre();

                            StockPorBodegaDTO dto =
                                    stockPorBodegaMap.getOrDefault(bodegaId,
                                            new StockPorBodegaDTO(bodegaId, nombre, 0L));

                            dto.setStockTotal(dto.getStockTotal() + cantidad);
                            stockPorBodegaMap.put(bodegaId, dto);
                        }
                    }
                }
            }
        }

        // Ordenar resultados (opcional): por stock descendente / movimientos descendentes
        List<StockPorBodegaDTO> stockPorBodegaOrdenado = stockPorBodegaMap.values().stream()
                .sorted(Comparator.comparingLong(StockPorBodegaDTO::getStockTotal).reversed())
                .collect(Collectors.toList());

        List<ProductoMasMovidoDTO> productosMasMovidosOrdenado = productosMasMovidosMap.values().stream()
                .sorted(Comparator.comparingLong(ProductoMasMovidoDTO::getTotalMovido).reversed())
                .collect(Collectors.toList());

        ResumenGeneralDTO resumen = new ResumenGeneralDTO();
        resumen.setStockPorBodega(stockPorBodegaOrdenado);
        resumen.setProductosMasMovidos(productosMasMovidosOrdenado);

        return resumen;
    }
}