/*
  Script de creación de la base de datos de Cosmi.
  Crea el esquema, el usuario de la aplicación, las tablas y los datos de prueba.
  Las tablas las crea este script (Hibernate no las modifica).
  Ejecutar una vez en un entorno de desarrollo, con un usuario administrador de MySQL.

  Usuarios de prueba:
    admin@cosmi.com    / admin123    (rol ADMIN)
    cliente@cosmi.com  / cliente123  (rol CLIENTE)
*/
-- Sección de administración
drop database if exists cosmi;
drop user if exists 'usuario_cosmi'@'%';

-- Creación del esquema
CREATE database cosmi
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- Creación del usuario (la clave debe coincidir con spring.datasource.password)
create user 'usuario_cosmi'@'%' identified by 'cosmi1234';

-- Permisos solo sobre la base de datos de Cosmi
grant select, insert, update, delete on cosmi.* to 'usuario_cosmi'@'%';
flush privileges;

use cosmi;

-- Tablas
CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(255) NOT NULL,
  apellidos VARCHAR(255),
  correo VARCHAR(255) NOT NULL,
  contrasena VARCHAR(255) NOT NULL,
  telefono VARCHAR(255),
  tipo_piel VARCHAR(20),
  rol VARCHAR(20) NOT NULL DEFAULT 'CLIENTE',
  activo TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id_usuario),
  UNIQUE KEY uk_usuario_correo (correo)
) ENGINE=InnoDB;

CREATE TABLE categoria (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(255) NOT NULL,
  descripcion VARCHAR(255),
  activo TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id_categoria),
  UNIQUE KEY uk_categoria_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE producto (
  id_producto INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(255) NOT NULL,
  descripcion VARCHAR(255),
  precio DECIMAL(10,2) NOT NULL,
  existencias INT NOT NULL DEFAULT 0,
  imagen VARCHAR(255),
  tipo_piel VARCHAR(20),
  activo TINYINT(1) NOT NULL DEFAULT 1,
  id_categoria INT,
  PRIMARY KEY (id_producto),
  CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria),
  CONSTRAINT chk_producto_existencias CHECK (existencias >= 0)
) ENGINE=InnoDB;

CREATE TABLE carrito (
  id_carrito INT NOT NULL AUTO_INCREMENT,
  id_usuario INT,
  PRIMARY KEY (id_carrito),
  UNIQUE KEY uk_carrito_usuario (id_usuario),
  CONSTRAINT fk_carrito_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB;

CREATE TABLE detalle_carrito (
  id_detalle_carrito INT NOT NULL AUTO_INCREMENT,
  id_carrito INT,
  id_producto INT,
  cantidad INT NOT NULL,
  PRIMARY KEY (id_detalle_carrito),
  CONSTRAINT fk_detcarrito_carrito FOREIGN KEY (id_carrito) REFERENCES carrito (id_carrito),
  CONSTRAINT fk_detcarrito_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
) ENGINE=InnoDB;

CREATE TABLE pedido (
  id_pedido INT NOT NULL AUTO_INCREMENT,
  id_usuario INT,
  fecha DATETIME(6),
  total DECIMAL(12,2),
  estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  PRIMARY KEY (id_pedido),
  CONSTRAINT fk_pedido_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB;

CREATE TABLE detalle_pedido (
  id_detalle_pedido INT NOT NULL AUTO_INCREMENT,
  id_pedido INT,
  id_producto INT,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(10,2),
  PRIMARY KEY (id_detalle_pedido),
  CONSTRAINT fk_detpedido_pedido FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido),
  CONSTRAINT fk_detpedido_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
) ENGINE=InnoDB;

-- Datos de prueba
INSERT INTO usuario (nombre, apellidos, correo, contrasena, tipo_piel, rol, activo) VALUES
  ('Admin Cosmi', NULL, 'admin@cosmi.com', '$2b$12$xgiGZxy2wdWThwOYRhqwwOV22r21AhBP8M5gKpeY9vlC46lJDQYqa', NULL, 'ADMIN', 1),
  ('Cliente Prueba', NULL, 'cliente@cosmi.com', '$2b$12$3c8xZc5is/l9wrUdVe06QO0GbNV2B/uk.T43n0y34JQ/EFFUyLE1q', NULL, 'CLIENTE', 1);

INSERT INTO categoria (nombre, descripcion, activo) VALUES
  ('Maquillaje', 'Bases, labiales y sombras', 1),
  ('Skincare', 'Cuidado facial', 1),
  ('Cabello', 'Shampoo y tratamientos', 1),
  ('Cuidado corporal', 'Cremas y exfoliantes', 1);

-- id_categoria: 1 Maquillaje, 2 Skincare, 3 Cabello, 4 Cuidado corporal
INSERT INTO producto (nombre, descripcion, precio, existencias, tipo_piel, activo, id_categoria) VALUES
  ('Limpiador facial gel', 'Limpieza suave para piel grasa', 8500, 20, 'grasa', 1, 2),
  ('Crema hidratante', 'Hidratación intensa para piel seca', 12500, 15, 'seca', 1, 2),
  ('Sérum de vitamina C', 'Ilumina y unifica el tono', 18900, 4, 'todas', 1, 2),
  ('Protector solar FPS 50', 'Uso diario, textura ligera', 14200, 30, 'todas', 1, 2),
  ('Tónico calmante', 'Para piel sensible', 9800, 0, 'sensible', 1, 2),
  ('Base líquida mate', 'Cobertura media', 11500, 12, 'mixta', 1, 1),
  ('Labial rojo clásico', 'Acabado cremoso', 7500, 25, 'todas', 1, 1),
  ('Máscara de pestañas', 'Volumen y definición', 6900, 5, 'todas', 1, 1),
  ('Shampoo nutritivo', 'Para cabello seco', 6500, 18, 'todas', 1, 3),
  ('Crema corporal de karité', 'Piel suave por 24 horas', 10900, 10, 'normal', 1, 4);
