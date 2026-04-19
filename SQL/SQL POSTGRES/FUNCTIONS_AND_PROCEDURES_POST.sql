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
-- LIMPIEZA (equivalente a DROP PACKAGE)
-- ============================================

DROP FUNCTION IF EXISTS crear_producto CASCADE;
DROP FUNCTION IF EXISTS modificar_producto CASCADE;
DROP FUNCTION IF EXISTS eliminar_producto CASCADE;
DROP FUNCTION IF EXISTS consultar_producto CASCADE;
DROP FUNCTION IF EXISTS listar_productos CASCADE;
DROP FUNCTION IF EXISTS cargar_productos_activos CASCADE;

DROP FUNCTION IF EXISTS registrar_lote CASCADE;
DROP FUNCTION IF EXISTS modificar_inventario CASCADE;
DROP FUNCTION IF EXISTS consultar_inventario CASCADE;
DROP FUNCTION IF EXISTS verificarStock CASCADE;

DROP FUNCTION IF EXISTS registrar_usuario CASCADE;
DROP FUNCTION IF EXISTS modificar_usuario CASCADE;
DROP FUNCTION IF EXISTS consultar_usuario CASCADE;

DROP FUNCTION IF EXISTS agregar_al_carrito CASCADE;
DROP FUNCTION IF EXISTS actualizar_cantidad CASCADE;
DROP FUNCTION IF EXISTS eliminar_del_carrito CASCADE;
DROP FUNCTION IF EXISTS limpiar_carrito CASCADE;
DROP FUNCTION IF EXISTS calcular_total_carrito CASCADE;
DROP FUNCTION IF EXISTS verificar_disponibilidad_carrito CASCADE;

DROP FUNCTION IF EXISTS procesar_compra CASCADE;
DROP FUNCTION IF EXISTS cancelar_orden CASCADE;
DROP FUNCTION IF EXISTS cambiar_estado_orden CASCADE;


-- ============================================
-- PRODUCTO
-- ============================================

CREATE OR REPLACE FUNCTION crear_producto(
    v_proName TEXT,
    v_proImg TEXT,
    v_proPrice NUMERIC,
    v_proType TEXT,
    v_descript TEXT,
    v_proMark TEXT,
    v_status TEXT DEFAULT 'A'
)
RETURNS VOID AS $$
BEGIN
    INSERT INTO product(proName, proImg, proPrice, proType, descript, proMark, proStatus)
    VALUES (v_proName, v_proImg, v_proPrice, v_proType, v_descript, v_proMark, v_status);
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION modificar_producto(
    v_proCode BIGINT,
    v_proName TEXT,
    v_proImg TEXT,
    v_proPrice NUMERIC,
    v_proType TEXT,
    v_descript TEXT,
    v_proMark TEXT,
    v_status TEXT
)
RETURNS VOID AS $$
BEGIN
    UPDATE product
    SET proName = v_proName,
        proImg = v_proImg,
        proPrice = v_proPrice,
        proType = v_proType,
        descript = v_descript,
        proMark = v_proMark,
        proStatus = v_status
    WHERE proCode = v_proCode;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION eliminar_producto(v_proCode BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE inventory SET invStatus = 'I' WHERE proCode = v_proCode;
    UPDATE product SET proStatus = 'I' WHERE proCode = v_proCode;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION consultar_producto(v_proCode BIGINT)
RETURNS SETOF product AS $$
BEGIN
    RETURN QUERY SELECT * FROM product WHERE proCode = v_proCode;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION listar_productos()
RETURNS SETOF product AS $$
BEGIN
    RETURN QUERY SELECT * FROM product ORDER BY proCode;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION cargar_productos_activos()
RETURNS SETOF product AS $$
BEGIN
    RETURN QUERY SELECT * FROM product WHERE proStatus = 'A' ORDER BY proPrice DESC;
END;
$$ LANGUAGE plpgsql;


-- ============================================
-- INVENTARIO
-- ============================================

CREATE OR REPLACE FUNCTION registrar_lote(
    v_proCode BIGINT,
    v_invStock INTEGER,
    v_sellingPrice NUMERIC,
    v_invDate TIMESTAMP,
    v_invProv TEXT
)
RETURNS VOID AS $$
BEGIN
    INSERT INTO inventory(proCode, invStock, sellingPrice, invDate, invProv)
    VALUES (v_proCode, v_invStock, v_sellingPrice, v_invDate, v_invProv);
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION modificar_inventario(
    v_invCode BIGINT,
    v_proCode BIGINT,
    v_invStock INTEGER,
    v_sellingPrice NUMERIC,
    v_invDate TIMESTAMP,
    v_invProv TEXT
)
RETURNS VOID AS $$
BEGIN
    UPDATE inventory
    SET proCode = v_proCode,
        invStock = v_invStock,
        sellingPrice = v_sellingPrice,
        invDate = v_invDate,
        invProv = v_invProv
    WHERE invCode = v_invCode;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION consultar_inventario(v_invCode BIGINT)
RETURNS SETOF inventory AS $$
BEGIN
    RETURN QUERY SELECT * FROM inventory WHERE invCode = v_invCode;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION verificarStock(v_proCode BIGINT)
RETURNS INTEGER AS $$
DECLARE v_total INTEGER;
BEGIN
    SELECT COALESCE(SUM(invStock),0) INTO v_total
    FROM inventory WHERE proCode = v_proCode;
    RETURN v_total;
END;
$$ LANGUAGE plpgsql;


-- ============================================
-- USUARIO
-- ============================================

CREATE OR REPLACE FUNCTION registrar_usuario(
    p_userName TEXT,
    p_userPassword TEXT,
    p_userEmail TEXT,
    p_userPhone TEXT,
    p_firstName TEXT,
    p_secondName TEXT,
    p_firstLastName TEXT,
    p_secondLastName TEXT,
    p_address TEXT,
    p_descAddress TEXT,
    p_cityID BIGINT,
    p_depID BIGINT
)
RETURNS VOID AS $$
DECLARE v_userID BIGINT;
BEGIN
    INSERT INTO users(userName, userPassword, userEmail, userPhone)
    VALUES (p_userName, p_userPassword, p_userEmail, p_userPhone)
    RETURNING userID INTO v_userID;

    INSERT INTO client_detail(userID, firstName, secondName, firstLastName, secondLastName,
    address, descAddress, cityID, depID)
    VALUES (v_userID, p_firstName, p_secondName, p_firstLastName, p_secondLastName,
    p_address, p_descAddress, p_cityID, p_depID);
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION modificar_usuario(
    p_userID BIGINT,
    p_userPhone TEXT,
    p_userEmail TEXT,
    p_userPassword TEXT,
    p_firstName TEXT,
    p_secondName TEXT,
    p_firstLastName TEXT,
    p_secondLastName TEXT,
    p_address TEXT,
    p_descAddress TEXT,
    p_cityID BIGINT,
    p_depID BIGINT
)
RETURNS VOID AS $$
BEGIN
    UPDATE users SET userPhone=p_userPhone,userEmail=p_userEmail,userPassword=p_userPassword
    WHERE userID=p_userID;

    UPDATE client_detail SET firstName=p_firstName,secondName=p_secondName,
    firstLastName=p_firstLastName,secondLastName=p_secondLastName,
    address=p_address,descAddress=p_descAddress,cityID=p_cityID,depID=p_depID
    WHERE userID=p_userID;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION consultar_usuario(p_userID BIGINT)
RETURNS TABLE(
    userName TEXT,userEmail TEXT,userPassword TEXT,userPhone TEXT,
    firstName TEXT,secondName TEXT,firstLastName TEXT,secondLastName TEXT,
    address TEXT,descAddress TEXT,cityID BIGINT,depID BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT u.userName,u.userEmail,u.userPassword,u.userPhone,
           c.firstName,c.secondName,c.firstLastName,c.secondLastName,
           c.address,c.descAddress,c.cityID,c.depID
    FROM users u JOIN client_detail c ON u.userID=c.userID
    WHERE u.userID=p_userID;
END;
$$ LANGUAGE plpgsql;


-- ============================================
-- CARRITO
-- ============================================

CREATE OR REPLACE FUNCTION agregar_al_carrito(p_userID BIGINT,p_proCode BIGINT,p_quantity INT)
RETURNS VOID AS $$
BEGIN
    INSERT INTO cart(userID,proCode,quantity)
    VALUES(p_userID,p_proCode,p_quantity)
    ON CONFLICT (userID,proCode)
    DO UPDATE SET quantity = cart.quantity + EXCLUDED.quantity;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION actualizar_cantidad(p_cartID BIGINT,p_quantity INT)
RETURNS VOID AS $$
BEGIN
    UPDATE cart SET quantity=p_quantity WHERE cartID=p_cartID;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION eliminar_del_carrito(p_cartID BIGINT)
RETURNS VOID AS $$
BEGIN
    DELETE FROM cart WHERE cartID=p_cartID;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION limpiar_carrito(p_userID BIGINT)
RETURNS VOID AS $$
BEGIN
    DELETE FROM cart WHERE userID=p_userID;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION calcular_total_carrito(p_userID BIGINT)
RETURNS NUMERIC AS $$
DECLARE v_total NUMERIC;
BEGIN
    SELECT COALESCE(SUM(p.proPrice*c.quantity),0)
    INTO v_total FROM cart c JOIN product p ON c.proCode=p.proCode
    WHERE c.userID=p_userID;
    RETURN v_total;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION verificar_disponibilidad_carrito(p_userID BIGINT)
RETURNS BOOLEAN AS $$
DECLARE item RECORD; v_stock INT;
BEGIN
    FOR item IN SELECT * FROM cart WHERE userID=p_userID LOOP
        SELECT COALESCE(SUM(invStock),0) INTO v_stock
        FROM inventory WHERE proCode=item.proCode;
        IF v_stock < item.quantity THEN RETURN FALSE; END IF;
    END LOOP;
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;


-- ============================================
-- COMPRAS
-- ============================================

CREATE OR REPLACE FUNCTION procesar_compra(
    p_userID BIGINT,
    p_paymentType TEXT
)
RETURNS BIGINT AS $$
DECLARE 
    v_ordID BIGINT;
    v_total NUMERIC := 0;
    item RECORD;
    v_stock INT;
BEGIN

    -- ============================================
    -- VALIDAR STOCK COMPLETO DEL CARRITO
    -- ============================================
    FOR item IN SELECT * FROM cart WHERE userID = p_userID LOOP
        
        SELECT COALESCE(SUM(invStock),0)
        INTO v_stock
        FROM inventory
        WHERE proCode = item.proCode AND invStatus = 'A';

        IF v_stock < item.quantity THEN
            RAISE EXCEPTION 'Stock insuficiente para producto %', item.proCode;
        END IF;

    END LOOP;


    -- ============================================
    -- CREAR ORDEN
    -- ============================================
    INSERT INTO "order"(ordState, ordDate, userID)
    VALUES('Processing', NOW(), p_userID)
    RETURNING ordID INTO v_ordID;


    -- ============================================
    -- PROCESAR CADA ITEM (TIPO CURSOR ORACLE)
    -- ============================================
    FOR item IN 
        SELECT c.*, p.proPrice 
        FROM cart c 
        JOIN product p ON c.proCode = p.proCode
        WHERE c.userID = p_userID
    LOOP

        -- INSERTAR DETALLE
        INSERT INTO order_detail(ordID, proCode, quantity, unitPrice)
        VALUES (v_ordID, item.proCode, item.quantity, item.proPrice);

        -- ACUMULAR TOTAL
        v_total := v_total + (item.proPrice * item.quantity);


        -- ============================================
        -- DESCONTAR INVENTARIO (POR LOTES)
        -- ============================================
        DECLARE
            v_restante INT := item.quantity;
            lote RECORD;
        BEGIN
            FOR lote IN 
                SELECT * FROM inventory
                WHERE proCode = item.proCode 
                AND invStatus = 'A'
                ORDER BY invDate ASC
            LOOP

                EXIT WHEN v_restante <= 0;

                IF lote.invStock >= v_restante THEN
                    UPDATE inventory
                    SET invStock = invStock - v_restante
                    WHERE invCode = lote.invCode;

                    v_restante := 0;

                ELSE
                    v_restante := v_restante - lote.invStock;

                    UPDATE inventory
                    SET invStock = 0,
                        invStatus = 'I'
                    WHERE invCode = lote.invCode;
                END IF;

            END LOOP;
        END;

    END LOOP;


    -- ============================================
    -- CREAR FACTURA
    -- ============================================
    INSERT INTO bill(ordID, paymentType, billDate, totalBill)
    VALUES (v_ordID, p_paymentType, NOW(), v_total);


    -- ============================================
    -- LIMPIAR CARRITO
    -- ============================================
    DELETE FROM cart WHERE userID = p_userID;


    RETURN v_ordID;


-- ============================================
-- MANEJO DE ERRORES (ESTILO ORACLE)
-- ============================================
EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Error procesando compra: %', SQLERRM;
END;
$$ LANGUAGE plpgsql;