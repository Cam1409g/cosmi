/*
  Script de creación de base de datos para Cosmi
  Este script crea el esquema, tablas, usuarios, y
  carga datos de ejemplo.
*/
-- Sección de administración (ejecutar una vez en un entorno de desarrollo)
drop database if exists cosmi;
drop user if exists 'usuario_cosmi'@'%';
drop user if exists 'usuario_cosmi'@'localhost';

-- Creación del esquema
CREATE database cosmi
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- Creación de usuarios
create user 'usuario_cosmi'@'%' identified by 'cosmi1234';
create user 'usuario_cosmi'@'localhost' identified by 'cosmi1234';

-- Asignación de permisos
grant select, insert, update, delete on cosmi.* to 'usuario_cosmi'@'%';
grant select, insert, update, delete on cosmi.* to 'usuario_cosmi'@'localhost';
flush privileges;

use cosmi;

-- --- Sección de Creación de Tablas ---

-- Tabla de usuarios
create table usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(30) NOT NULL,
  apellidos VARCHAR(50),
  correo VARCHAR(75) NOT NULL,
  contrasena VARCHAR(512) NOT NULL,
  telefono VARCHAR(25),
  tipo_piel VARCHAR(20),
  rol VARCHAR(20) NOT NULL DEFAULT 'CLIENTE',
  activo boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  unique (correo))
  ENGINE = InnoDB;

-- Tabla de categorías
create table categoria (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50) NOT NULL,
  descripcion VARCHAR(255),
  activo boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_categoria),
  unique (nombre))
  ENGINE = InnoDB;

-- Tabla de productos
create table producto (
  id_producto INT NOT NULL AUTO_INCREMENT,
  id_categoria INT,
  nombre VARCHAR(50) NOT NULL,
  descripcion VARCHAR(255),
  precio decimal(12,2) NOT NULL CHECK (precio >= 0),
  existencias int NOT NULL CHECK (existencias >= 0),
  imagen varchar(1024),
  tipo_piel VARCHAR(20),
  activo boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_producto),
  foreign key fk_producto_categoria (id_categoria) references categoria(id_categoria))
  ENGINE = InnoDB;

-- Tabla del carrito de cada usuario
create table carrito (
  id_carrito INT NOT NULL AUTO_INCREMENT,
  id_usuario INT,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_carrito),
  unique (id_usuario),
  foreign key fk_carrito_usuario (id_usuario) references usuario(id_usuario))
  ENGINE = InnoDB;

-- Tabla con los productos de cada carrito
create table detalle_carrito (
  id_detalle_carrito INT NOT NULL AUTO_INCREMENT,
  id_carrito INT,
  id_producto INT,
  cantidad INT NOT NULL CHECK (cantidad > 0),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_detalle_carrito),
  foreign key fk_detcarrito_carrito (id_carrito) references carrito(id_carrito),
  foreign key fk_detcarrito_producto (id_producto) references producto(id_producto))
  ENGINE = InnoDB;

-- Tabla de pedidos
create table pedido (
  id_pedido INT NOT NULL AUTO_INCREMENT,
  id_usuario INT,
  fecha DATETIME,
  total DECIMAL(12,2),
  estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_pedido),
  index ndx_id_usuario (id_usuario),
  foreign key fk_pedido_usuario (id_usuario) references usuario(id_usuario))
  ENGINE = InnoDB;

-- Tabla con el detalle de cada pedido
create table detalle_pedido (
  id_detalle_pedido INT NOT NULL AUTO_INCREMENT,
  id_pedido INT,
  id_producto INT,
  cantidad INT NOT NULL CHECK (cantidad > 0),
  precio_unitario DECIMAL(12,2),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_detalle_pedido),
  index ndx_pedido (id_pedido),
  index ndx_producto (id_producto),
  foreign key fk_detpedido_pedido (id_pedido) references pedido(id_pedido),
  foreign key fk_detpedido_producto (id_producto) references producto(id_producto))
  ENGINE = InnoDB;

-- --- Sección de Inserción de Datos ---

-- Inserción de usuarios (claves: admin123 y cliente123)
INSERT INTO usuario (nombre, apellidos, correo, contrasena, tipo_piel, rol, activo) VALUES
('Admin', 'Cosmi', 'admin@cosmi.com', '$2a$10$ehERQZGeOC.CrTMmO3vSwOGeEN9NcumTz4jh19oLhaxFwFF5yDgXm', NULL, 'ADMIN', true),
('Cliente', 'Prueba', 'cliente@cosmi.com', '$2a$10$lFQXYFfv33Vr6PwC4SzVq.CbdgV.L44TuJWKt8H4INCPEzvY0m.6q', NULL, 'CLIENTE', true);

-- Inserción de categorías
INSERT INTO categoria (nombre, descripcion, activo) VALUES
('Maquillaje', 'Bases, labiales y sombras', true),
('Skincare', 'Cuidado facial', true),
('Cabello', 'Shampoo y tratamientos', true),
('Cuidado corporal', 'Cremas y exfoliantes', true);

-- Inserción de productos
INSERT INTO producto (id_categoria, nombre, descripcion, precio, existencias, imagen, tipo_piel, activo) VALUES
(2, 'Limpiador facial gel', 'Limpieza suave para piel grasa', 8500, 20, 'https://images.unsplash.com/photo-1629327126489-85b9b2a1b01d?q=80&w=627&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=NHwxMjA3fDA%3D', 'grasa', true),
(2, 'Crema hidratante', 'Hidratación intensa para piel seca', 12500, 15, 'https://images.unsplash.com/photo-1629732047847-50219e9c5aef?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=NHwxMjA3fDA%3D', 'seca', true),
(2, 'Sérum de vitamina C', 'Ilumina y unifica el tono', 18900, 4, 'https://plus.unsplash.com/premium_photo-1670514094627-9ed5a1e9c06f?q=80&w=686&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=NHwxMjA3fDA%3D', 'todas', true),
(2, 'Protector solar FPS 50', 'Uso diario, textura ligera', 14200, 30, 'https://images.unsplash.com/photo-1623676714504-edd78728155e?q=80&w=880&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=NHwxMjA3fDA%3D', 'todas', true),
(2, 'Tónico calmante', 'Para piel sensible', 9800, 0, 'https://siman.vtexassets.com/arquivos/ids/4264549/104180605-1.jpg?v=638199523421270000', 'sensible', true),
(1, 'Base líquida mate', 'Cobertura media', 11500, 12, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSIiItQ5f7C9QfP5tqA8Zh5NvlEe0QRrbO_0s4K-pAj4evV8NsDltbnzYwj&s=10', 'mixta', true),
(1, 'Labial rojo clásico', 'Acabado cremoso', 7500, 25, 'https://plus.unsplash.com/premium_photo-1676380030496-1b1fd3a33a1b?q=80&w=784&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=NHwxMjA3fDA%3D', 'todas', true),
(1, 'Máscara de pestañas', 'Volumen y definición', 6900, 5, 'https://plus.unsplash.com/premium_photo-1678377960130-bb70ffeb1cd8?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=NHwxMjA3fDA%3D', 'todas', true),
(3, 'Shampoo nutritivo', 'Para cabello seco', 6500, 18, 'https://images.unsplash.com/photo-1786725009844-3daea6bd1fb0?q=80&w=880&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=NHwxMjA3fDA%3D', 'todas', true),
(4, 'Crema corporal de karité', 'Piel suave por 24 horas', 10900, 10, 'https://aromas.co.cr/cdn/shop/files/LifestyleWebsite_0012s_0001_JHC_2291-Edit.jpg?v=1775831079&width=3840', 'normal', true);