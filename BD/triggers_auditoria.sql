-- ================================================
--  AUDITORÍA GENERAL PARA ENTIDADES DE NEGOCIO
--  Proyecto: LogiTrack - Spring Boot + PostgreSQL
--  NOTA: La tabla "auditorias" ya existe (creada por JPA).
--  Este script solo crea la función y los triggers.
-- ================================================

-- Opcional: eliminar triggers previos si estás rehaciendo el script
DROP TRIGGER IF EXISTS tr_aud_bodegas ON bodegas;
DROP TRIGGER IF EXISTS tr_aud_productos ON productos;
DROP TRIGGER IF EXISTS tr_aud_movimientos_inventario ON movimientos_inventario;
DROP TRIGGER IF EXISTS tr_aud_detalle_movimiento ON detalle_movimiento;
DROP TRIGGER IF EXISTS tr_aud_usuarios ON usuarios;

DROP FUNCTION IF EXISTS fn_registrar_auditoria();

-- ================================================
--  FUNCIÓN GENÉRICA DE AUDITORÍA
-- ================================================
CREATE OR REPLACE FUNCTION fn_registrar_auditoria()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    v_usuario_id BIGINT;
    v_valores_anteriores TEXT;
    v_valores_nuevos TEXT;
BEGIN
    -- Intentar leer el usuario actual desde una variable de sesión:
    --   SELECT set_config('app.current_user_id', '<id_usuario>', true);
    -- Esto lo configurás desde el backend (filtro/interceptor).
    BEGIN
        v_usuario_id := NULLIF(current_setting('app.current_user_id', true), '')::BIGINT;
    EXCEPTION
        WHEN OTHERS THEN
            v_usuario_id := NULL;
    END;

    -- Definir valores según el tipo de operación
    IF TG_OP = 'INSERT' THEN
        v_valores_anteriores := NULL;
        v_valores_nuevos     := row_to_json(NEW)::TEXT;
    ELSIF TG_OP = 'UPDATE' THEN
        v_valores_anteriores := row_to_json(OLD)::TEXT;
        v_valores_nuevos     := row_to_json(NEW)::TEXT;
    ELSIF TG_OP = 'DELETE' THEN
        v_valores_anteriores := row_to_json(OLD)::TEXT;
        v_valores_nuevos     := NULL;
    END IF;

    -- Insertar en tabla de auditorías
    INSERT INTO auditorias (
        tipo_operacion,
        fecha_hora,
        usuario_responsable_id,
        entidad_afectada,
        valores_anteriores,
        valores_nuevos
    ) VALUES (
        TG_OP,                  -- "INSERT", "UPDATE" o "DELETE"
        NOW(),
        v_usuario_id,           -- puede ser NULL si no se seteó
        TG_TABLE_NAME,          -- nombre de la tabla afectada
        v_valores_anteriores,
        v_valores_nuevos
    );

    -- Devolver el registro adecuado según el tipo de trigger
    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$$;

-- ================================================
--  TRIGGERS POR TABLA
--  Se auditan: bodegas, productos, movimientos_inventario,
--              detalle_movimiento, usuarios
-- ================================================

-- BODEGAS
CREATE TRIGGER tr_aud_bodegas
AFTER INSERT OR UPDATE OR DELETE ON bodegas
FOR EACH ROW
EXECUTE FUNCTION fn_registrar_auditoria();

-- PRODUCTOS
CREATE TRIGGER tr_aud_productos
AFTER INSERT OR UPDATE OR DELETE ON productos
FOR EACH ROW
EXECUTE FUNCTION fn_registrar_auditoria();

-- MOVIMIENTOS DE INVENTARIO
CREATE TRIGGER tr_aud_movimientos_inventario
AFTER INSERT OR UPDATE OR DELETE ON movimientos_inventario
FOR EACH ROW
EXECUTE FUNCTION fn_registrar_auditoria();

-- DETALLE DE MOVIMIENTO
CREATE TRIGGER tr_aud_detalle_movimiento
AFTER INSERT OR UPDATE OR DELETE ON detalle_movimiento
FOR EACH ROW
EXECUTE FUNCTION fn_registrar_auditoria();

-- USUARIOS
CREATE TRIGGER tr_aud_usuarios
AFTER INSERT OR UPDATE OR DELETE ON usuarios
FOR EACH ROW
EXECUTE FUNCTION fn_registrar_auditoria();


ALTER TABLE auditorias
  ALTER COLUMN usuario_responsable_id DROP NOT NULL;
