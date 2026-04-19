-- ==========================================================
-- FUNCIONES Y PROCEDIMIENTOS
-- ==========================================================
/*
    NOMBRES: 
            - VALERIA MUÑOZ GUERRERO
            - NICOLE BURBANO SOLARTE
            - JUAN CAMILO HENAO VILLEGAS
            - JUAN PABLO COLLAZOS SAMBONI
            - JESUS EDUARDO LASSO MUÑOZ
*/

-- ============================================
-- LIMPIEZA
-- ============================================

DROP TRIGGER IF EXISTS trg_validate_stock ON order_detail;
DROP TRIGGER IF EXISTS trg_cart_update_date ON cart;
DROP TRIGGER IF EXISTS trg_update_vw_usuario_detalle ON vw_usuario_detalle;
DROP TRIGGER IF EXISTS trg_cancel_order_vw ON vw_order_full;
DROP TRIGGER IF EXISTS trg_inv_before ON inventory;
DROP TRIGGER IF EXISTS trg_inv_after ON inventory;

DROP FUNCTION IF EXISTS fn_validate_stock CASCADE;
DROP FUNCTION IF EXISTS fn_cart_update_date CASCADE;
DROP FUNCTION IF EXISTS fn_update_vw_usuario_detalle CASCADE;
DROP FUNCTION IF EXISTS fn_cancel_order_vw CASCADE;
DROP FUNCTION IF EXISTS fn_inv_before CASCADE;
DROP FUNCTION IF EXISTS fn_inv_after CASCADE;


-- ============================================
-- TRIGGER 1: VALIDAR STOCK
-- ============================================

CREATE OR REPLACE FUNCTION fn_validate_stock()
RETURNS TRIGGER AS $$
DECLARE
    v_stock INTEGER;
BEGIN
    SELECT COALESCE(SUM(inv_stock), 0)
    INTO v_stock
    FROM inventory
    WHERE pro_code = NEW.pro_code
      AND inv_status = 'A';

    IF v_stock < NEW.quantity THEN
        RAISE EXCEPTION 'Stock insuficiente para el producto %', NEW.pro_code;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_stock
BEFORE INSERT ON order_detail
FOR EACH ROW
EXECUTE FUNCTION fn_validate_stock();


-- ============================================
-- TRIGGER 2: UPDATE DATE CART
-- ============================================

CREATE OR REPLACE FUNCTION fn_cart_update_date()
RETURNS TRIGGER AS $$
BEGIN
    NEW.addedDate := NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cart_update_date
BEFORE UPDATE ON cart
FOR EACH ROW
EXECUTE FUNCTION fn_cart_update_date();


-- ============================================
-- VISTA USUARIO DETALLE
-- ============================================

CREATE OR REPLACE VIEW vw_usuario_detalle AS
SELECT 
    u.user_id, 
    u.user_name, 
    u.user_email, 
    u.user_phone,
    c.first_name, 
    c.second_name, 
    c.first_last_name, 
    c.second_last_name,
    c.address, 
    c.desc_address, 
    c.city_id, 
    c.dep_id
FROM users u
JOIN client_detail c ON u.user_id = c.user_id;


-- ============================================
-- TRIGGER INSTEAD OF UPDATE
-- ============================================

CREATE OR REPLACE FUNCTION fn_update_vw_usuario_detalle()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE users
    SET userEmail = NEW.userEmail,
        userPhone = NEW.userPhone
    WHERE userID = OLD.userID;

    UPDATE client_detail
    SET firstName = NEW.firstName,
        secondName = NEW.secondName,
        firstLastName = NEW.firstLastName,
        secondLastName = NEW.secondLastName,
        address = NEW.address,
        descAddress = NEW.descAddress,
        cityID = NEW.cityID,
        depID = NEW.depID
    WHERE userID = OLD.userID;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_vw_usuario_detalle
INSTEAD OF UPDATE ON vw_usuario_detalle
FOR EACH ROW
EXECUTE FUNCTION fn_update_vw_usuario_detalle();


-- ============================================
-- VISTA ORDEN COMPLETA
-- ============================================

CREATE OR REPLACE VIEW vw_order_full AS
SELECT 
    o.ord_id, 
    o.ord_state, 
    o.ord_date,
    u.user_id, 
    u.user_name, 
    u.user_email,
    cd.first_name, 
    cd.first_last_name,
    (
        SELECT SUM(od.quantity * od.unit_price)
        FROM order_detail od
        WHERE od.ord_id = o.ord_id
    ) AS order_total
FROM "ORDER" o
JOIN users u ON o.user_id = u.user_id
JOIN client_detail cd ON u.user_id = cd.user_id;

-- ============================================
-- TRIGGER INSTEAD OF DELETE (CANCELAR ORDEN)
-- ============================================

CREATE OR REPLACE FUNCTION fn_cancel_order_vw()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE "order"
    SET ordState = 'Cancelled'
    WHERE ordID = OLD.ordID;

    RAISE NOTICE 'Orden % cancelada desde la vista.', OLD.ordID;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cancel_order_vw
INSTEAD OF DELETE ON vw_order_full
FOR EACH ROW
EXECUTE FUNCTION fn_cancel_order_vw();


-- ============================================
-- "COMPOUND TRIGGER" SIMULADO (POSTGRES)
-- ============================================

-- BEFORE TRIGGER (simula BEFORE EACH ROW)
CREATE OR REPLACE FUNCTION fn_inv_before()
RETURNS TRIGGER AS $$
BEGIN
    RAISE NOTICE 
    'Antes del cambio → inv_code=%, OLD=%, NEW=%',
    OLD.inv_code, OLD.inv_stock, NEW.inv_stock;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_inv_before
BEFORE UPDATE OF inv_stock ON inventory
FOR EACH ROW
EXECUTE FUNCTION fn_inv_before();


-- AFTER TRIGGER (movimientos de inventario)
CREATE OR REPLACE FUNCTION fn_inv_after()
RETURNS TRIGGER AS $$
DECLARE
    v_mov_type TEXT;
    v_quantity INT;
BEGIN

    IF NEW.inv_stock > OLD.inv_stock THEN
        v_mov_type := 'ENTRADA';
        v_quantity := NEW.inv_stock - OLD.inv_stock;

    ELSIF NEW.inv_stock < OLD.inv_stock THEN
        v_mov_type := 'SALIDA';
        v_quantity := OLD.inv_stock - NEW.inv_stock;

    ELSE
        RETURN NEW;
    END IF;

    INSERT INTO invmovement(
        inv_code, mov_type, quantity, prev_stock, new_stock, reason
    ) VALUES (
        NEW.inv_code, v_mov_type, v_quantity,
        OLD.inv_stock, NEW.inv_stock,
        'Movimiento automático'
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_inv_after
AFTER UPDATE OF inv_stock ON inventory
FOR EACH ROW
EXECUTE FUNCTION fn_inv_after();