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

-- ==========================================================
-- INSERTS CORRECTOS SEGÚN TU DDL
-- ==========================================================

-- ===============================
-- 1. PRODUCT_TYPE
-- ===============================
INSERT INTO product_type (type_name) VALUES 
('Smartphones'),
('Laptops'),
('Accesorios'),
('Componentes'),
('Monitores'),
('Audio'),
('Gaming'),
('Redes'),
('Almacenamiento'),
('Tarjeta Gráfica'),
('Otros');

-- ===============================
-- 2. PRODUCT
-- ===============================
INSERT INTO product (pro_name, pro_img, pro_price, pro_type, descript, pro_mark) VALUES
('iPhone 15 Pro','img',6500000,1,'Apple A17 Pro','Apple'),
('Samsung Galaxy S24 Ultra','img',5800000,1,'AMOLED 200MP','Samsung'),
('ASUS ROG Strix G16','img',7200000,2,'i9 RTX 4070','ASUS'),
('HP Pavilion 15','img',3900000,2,'Ryzen 7','HP'),
('Logitech G502','img',250000,3,'Mouse gamer','Logitech'),
('Redragon Kumara','img',220000,3,'Teclado mecánico','Redragon'),
('SSD Kingston 1TB','img',310000,9,'NVMe','Kingston'),
('LG Ultragear 27','img',1250000,5,'144Hz','LG'),
('Sony WH-1000XM5','img',1890000,6,'Noise cancelling','Sony'),
('TP-Link AX3000','img',430000,8,'WiFi 6','TP-Link');

-- ===============================
-- 3. PROVIDER
-- ===============================
INSERT INTO provider (prov_email, prov_name, prov_phone) VALUES
('ventas@apple.com','Apple Distribution','3155550001'),
('ventas@samsung.com','Samsung Colombia','3205550002'),
('ventas@asus.com','ASUS LATAM','3105550003'),
('ventas@hp.com','HP Partner','3005550004'),
('ventas@logitech.com','Logitech','3115550005'),
('ventas@redragon.com','Redragon','3125550006'),
('ventas@kingston.com','Kingston','3135550007'),
('ventas@lg.com','LG','3145550008'),
('ventas@sony.com','Sony','3165550009'),
('ventas@tplink.com','TP-Link','3175550010');

-- ===============================
-- 4. INVENTORY
-- ===============================
INSERT INTO inventory (pro_code, inv_stock, selling_price, inv_prov) VALUES
(1,15,6700000,1),
(2,20,5900000,2),
(3,10,7500000,3),
(4,12,4000000,4),
(5,50,270000,5),
(6,40,230000,6),
(7,25,330000,7),
(8,18,1300000,8),
(9,15,1950000,9),
(10,30,450000,10);

-- ===============================
-- 5. DEPARTMENTS
-- ===============================
INSERT INTO departments (dep_name) VALUES
('Cundinamarca'),('Antioquia'),('Valle del Cauca'),
('Atlántico'),('Risaralda'),('Santander'),
('Caldas'),('Huila'),('Boyacá'),
('Bogotá D.C.'),('Cauca');

-- ===============================
-- 6. CITIES
-- ===============================
INSERT INTO cities (city_name, dep_id) VALUES
('Soacha',1),('Zipaquirá',1),('Chía',1),
('Medellín',2),('Bello',2),('Envigado',2),
('Cali',3),('Palmira',3),('Buenaventura',3),
('Barranquilla',4),('Soledad',4),('Malambo',4),
('Pereira',5),('Dosquebradas',5),('Santa Rosa',5),
('Bucaramanga',6),('Floridablanca',6),('Girón',6),
('Manizales',7),('Villamaría',7),('Chinchiná',7),
('Neiva',8),('Pitalito',8),('Garzón',8),
('Tunja',9),('Duitama',9),('Sogamoso',9),
('Bogotá',10),
('Popayán',11),('Santander de Quilichao',11),('Puerto Tejada',11);

-- ===============================
-- 7. USERS
-- ===============================
INSERT INTO users (user_name,user_password,user_email,user_role,user_phone) VALUES
('admin','admin123','admin@techstore.com','ADMIN','3001112233'),
('juanperez','1234','juan@mail.com','CLIENT','3012223344'),
('maria','1234','maria@mail.com','CLIENT','3023334455'),
('carlos','1234','carlos@mail.com','CLIENT','3034445566'),
('laura','1234','laura@mail.com','CLIENT','3045556677'),
('andres','1234','andres@mail.com','CLIENT','3056667788'),
('camila','1234','camila@mail.com','CLIENT','3067778899'),
('pedro','1234','pedro@mail.com','CLIENT','3078889900'),
('sofia','1234','sofia@mail.com','CLIENT','3089990011'),
('javier','1234','javier@mail.com','CLIENT','3090001122');

-- ===============================
-- 8. CLIENT_DETAIL
-- ===============================
INSERT INTO client_detail VALUES
(2,'Juan','Carlos','Perez','Rojas','Dir1',NULL,1,1),
(3,'Maria','Elena','Gomez','Lopez','Dir2',NULL,2,2),
(4,'Carlos',NULL,'Soto','Ramirez','Dir3',NULL,3,3),
(5,'Laura','Isabel','Diaz','Vargas','Dir4',NULL,4,4),
(6,'Andres','Felipe','Rojas','Martinez','Dir5',NULL,5,5),
(7,'Camila','Andrea','Ramos','Suarez','Dir6',NULL,6,6),
(8,'Pedro','Jose','Martinez','Moreno','Dir7',NULL,7,7),
(9,'Sofia','Alejandra','Cortes','Vega','Dir8',NULL,8,8),
(10,'Javier','Eduardo','Lopez','Torres','Dir9',NULL,9,9),
(1,'Admin',NULL,'General',NULL,'Principal',NULL,1,1);

-- ===============================
-- 9. ORDER
-- ===============================
INSERT INTO "ORDER" (ord_state, user_id) VALUES
('Pending',2),
('Processing',3),
('Shipped',4),
('Delivered',5),
('Cancelled',6),
('Pending',7),
('Processing',8),
('Delivered',9),
('Pending',10),
('Shipped',3);

-- ===============================
-- 10. ORDER_DETAIL
-- ===============================
INSERT INTO order_detail VALUES
(1,1,1,6500000),
(1,5,2,250000),
(2,2,1,5800000),
(3,3,1,7200000),
(4,4,1,3900000),
(5,7,2,310000),
(6,8,1,1250000),
(7,9,1,1890000),
(8,10,1,430000),
(9,6,3,220000);

-- ===============================
-- 11. BILL
-- ===============================
INSERT INTO bill (inv_code, ord_id, payment_type, total_bill) VALUES
(1,1,'TARJETA',7000000),
(2,2,'TRANSFERENCIA',5800000),
(3,3,'EFECTIVO',7200000),
(4,4,'TARJETA',3900000),
(5,5,'TRANSFERENCIA',620000),
(6,6,'TARJETA',1250000),
(7,7,'EFECTIVO',1890000),
(8,8,'TRANSFERENCIA',430000),
(9,9,'TARJETA',660000),
(10,10,'EFECTIVO',5900000);

-- ===============================
-- 12. CART
-- ===============================
INSERT INTO cart (user_id, pro_code, quantity) VALUES
(2,1,2),
(3,5,1),
(4,3,1),
(5,9,2),
(6,7,1);