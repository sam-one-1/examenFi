-- Script para crear la tabla stock_bodega
-- Esta tabla relaciona productos con bodegas y almacena la cantidad disponible en cada bodega

CREATE TABLE IF NOT EXISTS stock_bodega (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    bodega_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_stock_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    CONSTRAINT fk_stock_bodega FOREIGN KEY (bodega_id) REFERENCES bodegas(id) ON DELETE CASCADE,
    CONSTRAINT uk_producto_bodega UNIQUE (producto_id, bodega_id),
    CONSTRAINT chk_cantidad_positiva CHECK (cantidad >= 0)
);

-- Índices para mejorar el rendimiento de las consultas
CREATE INDEX IF NOT EXISTS idx_stock_producto ON stock_bodega(producto_id);
CREATE INDEX IF NOT EXISTS idx_stock_bodega ON stock_bodega(bodega_id);
CREATE INDEX IF NOT EXISTS idx_stock_producto_bodega ON stock_bodega(producto_id, bodega_id);

-- Comentarios en la tabla y columnas
COMMENT ON TABLE stock_bodega IS 'Almacena el stock de cada producto en cada bodega';
COMMENT ON COLUMN stock_bodega.id IS 'Identificador único del registro de stock';
COMMENT ON COLUMN stock_bodega.producto_id IS 'Referencia al producto';
COMMENT ON COLUMN stock_bodega.bodega_id IS 'Referencia a la bodega';
COMMENT ON COLUMN stock_bodega.cantidad IS 'Cantidad disponible del producto en la bodega';

