-- Proveedores iniciales
INSERT INTO proveedores (nombre, telefono, email) VALUES ('Proveedor Norte', '111-111-111', 'norte@proveedores.com');
INSERT INTO proveedores (nombre, telefono, email) VALUES ('Proveedor Sur', '222-222-222', 'sur@proveedores.com');

-- Asignar proveedor por defecto a productos existentes si no tienen uno
UPDATE productos SET proveedor_id = 1 WHERE proveedor_id IS NULL;
