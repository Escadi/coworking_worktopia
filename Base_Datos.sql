--Desabilita temporalmente las restricciones de clave foránea
SET FOREIGN_KEY_CHECKS = 0;
-- Vuelve a habilitar las restricciones de clave foránea
SET FOREIGN_KEY_CHECKS = 1;
-- Borrar esta cuando funcione..
DROP TABLE IF EXISTS Factura_Reservas;
DROP TABLE IF EXISTS Espacios;

CREATE DATABASE IF NOT EXISTS Worktopia;
USE Worktopia;

DROP TABLE IF EXISTS Clientes;
DROP TABLE IF EXISTS Reservas;
DROP TABLE IF EXISTS Facturas;



CREATE TABLE Clientes (
    dni VARCHAR(10) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    primerApellido VARCHAR(30) NOT NULL,
    segundoApellido VARCHAR(30),
    email VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(15),
    CONSTRAINT pk_dni_cliente PRIMARY KEY (dni)
);



CREATE TABLE Asientos (
    id_asiento INT AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    tarifa_hora DECIMAL(10, 2) NOT NULL,
    CONSTRAINT pk_id_asiento PRIMARY KEY (id_asiento)
);


CREATE TABLE Reservas (
    id_reserva INT AUTO_INCREMENT,
    dni VARCHAR(10) NOT NULL,
    id_asiento INT NOT NULL,
    id_factura INT NOT NULL,
    fecha_hora_inicio DATETIME NOT NULL,
    fecha_hora_fin DATETIME NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT pk_id_reserva PRIMARY KEY (id_reserva),
    CONSTRAINT fk_dni_reserva FOREIGN KEY (dni) REFERENCES Clientes(dni),
    CONSTRAINT fk_id_asiento_reserva FOREIGN KEY (id_asiento) REFERENCES Asientos(id_asiento),
    CONSTRAINT fk_id_factura_reserva FOREIGN KEY (id_factura) REFERENCES Facturas(id_factura)
);


 CREATE TABLE Facturas (
    id_factura INT NOT NULL,
    dni VARCHAR(10) NOT NULL,
    precio_total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    descuento DECIMAL(5,2) DEFAULT 0.00,
    subtotal DECIMAL(10,2) NOT NULL,
    fecha_hora_emision DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('Pendiente', 'Pagada') NOT NULL DEFAULT 'Pendiente',
    fecha_hora_pago DATETIME,
    forma_pago VARCHAR(50),
    CONSTRAINT pk_id_factura PRIMARY KEY (id_factura),
    CONSTRAINT fk_dni_factura FOREIGN KEY (dni) REFERENCES Clientes(dni)
);

ALTER TABLE Facturas MODIFY COLUMN forma_pago VARCHAR(50);
ALTER TABLE Facturas ADD COLUMN subtotal DECIMAL(10,2) NOT NULL;

DROP TABLE IF EXISTS Usuarios;
CREATE TABLE Usuarios (
    id_usuario INT AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    contrasenia VARCHAR(255) NOT NULL,
    categoria ENUM('Admin', 'Empleado') NOT NULL,
    CONSTRAINT pk_id_usuario PRIMARY KEY (id_usuario)
);


DELETE FROM Clientes;
INSERT INTO Clientes (dni, nombre, primerApellido, segundoApellido, email, telefono) 
VALUES ("12345678A", "Agustín", "Perez", "Perez", "aguseltin@gmail.com", "646053394"),
    ("23456789B", "Beatriz", "Gomez", "Lopez", "beatrizgomez@hotmail.com", "654789321"),
    ("34567890C", "Carlos", "Martinez", "Ruiz", "carlosmruiz@gmail.com", "612345678"),
    ("45678901D", "Diana", "Fernandez", "Soto", "dianafernandez@yahoo.com", "678123456"),
    ("56789012E", "Eduardo", "Ramirez", "Torres", "eduardoramirez@gmail.es", "698765432"),
    ("67890123F", "Fernando", "Lopez", "Garcia", "fernandolopez@gmail.com", "612398765"),
    ("78901234G", "Gabriela", "Sanchez", "Diaz", "gabrielasanchez@gmail.es", "634567890"),
    ("89012345H", "Hector", "Jimenez", "Ortiz", "hectorjimenez@outlook.com", "678901234"),
    ("90123456I", "Isabel", "Torres", "Martinez", "isabeltorres@gmail.com", "612345987"),
    ("01234567J", "Javier", "Ruiz", "Hernandez", "javierruiz@outlook.com", "698765123"),
    ("12345678K", "Karen", "Morales", "Perez", "karenmorales@outlook.com", "612398456"),
    ("23456789L", "Luis", "Fernandez", "Gomez", "luisfernandez@gmail.com", "634567123"),
    ("34567890M", "Maria", "Gonzalez", "Lopez", "mariagonzalez@gmail.com", "678901567"),
    ("45678901N", "Nicolas", "Ramirez", "Torres", "nicolasramirez@gmail.com", "612345654"),
    ("56789012O", "Olga", "Martinez", "Ruiz", "olgamartinez@outlook.es", "698765890"),
    ("67890123P", "Pablo", "Sanchez", "Diaz", "pablosanchez@outlook.es", "612398321"),
    ("78901234Q", "Quintin", "Jimenez", "Ortiz", "quintinjimenez@gmail.com", "634567654"),
    ("89012345R", "Rosa", "Torres", "Martinez", "rosatorres@outlook.es", "678901890"),
    ("90123456S", "Sergio", "Ruiz", "Hernandez", "sergioruiz@outlook.es", "612345321"),
    ("01234567T", "Teresa", "Morales", "Perez", "teresamorales@yahoo.com", "698765567"),
    ("12345678U", "Ulises", "Fernandez", "Gomez", "ulisesfernandez@gmail.es", "612398789"),
    ("23456789V", "Valeria", "Gonzalez", "Lopez", "valeriagonzalez@outlook.com", "634567321"),
    ("34567890W", "Walter", "Ramirez", "Torres", "walterramirez@goutlook.com", "678901654"),
    ("45678901X", "Ximena", "Martinez", "Ruiz", "ximenamartinez@outlook.com", "612345890"),
    ("56789012Y", "Yolanda", "Sanchez", "Diaz", "yolandasanchez@outlook.com", "698765321");


DELETE FROM Usuarios;
ALTER TABLE Usuarios AUTO_INCREMENT = 1;
INSERT INTO Usuarios (nombre, email, contrasenia, categoria)
VALUES ("Eliu", "eliuadmin@worktopia.com", "Eliu.123E", "Admin"),
    ("David", "davidadmin@worktopia.com", "David.123D", "Admin"),
    ("Luis", "luisppps123@hotmail.com", "Luis.123L", "Empleado"),
    ("Sofia", "lsofi@yahoo.es", "Sofia.123S", "Empleado");


DELETE FROM Asientos;
ALTER TABLE Asientos AUTO_INCREMENT = 1;
INSERT INTO Asientos (nombre, tarifa_hora)
VALUES
("A1", 3.00),
("A2", 3.00),
("A3", 3.00),
("A4", 3.00),
("A5", 3.00),
("A6", 3.00),
("A7", 3.00),
("A8", 3.00),
("A9", 3.00),
("A10", 3.00),
("A11", 3.00),
("A12", 3.00),
("A13", 3.00),
("A14", 3.00),
("A15", 3.00),
("A16", 3.00),
("A17", 3.00),
("A18", 3.00),
("Oficina 1", 8.00),
("Oficina 2", 8.00),
("Sala de Conferencias 1", 15.00),
("Sala de Conferencias 2", 10.00);


DELETE FROM Facturas;
ALTER TABLE Facturas AUTO_INCREMENT = 1;
INSERT INTO Facturas (tiene_descuento, dni)
VALUES
(FALSE. "12345678A"),
(FALSE, "23456789B");

-- El calculo de la factura deberá de programarse en el backend de java sumando el total de las horas de la reserva y multiplicando por la tarifa de la hora del asiento.

DELETE FROM Reservas;
ALTER TABLE Reservas AUTO_INCREMENT = 1;
INSERT INTO Reservas (dni, id_asiento, id_factura, fecha_hora_inicio, fecha_hora_fin)
VALUES
("12345678A", 1, 1, "2025-01-01 09:00:00", "2025-01-01 12:00:00"),
("12345678A", 1, 1, "2025-01-02 09:00:00", "2025-01-02 12:00:00"),
("12345678A", 1, 1, "2025-01-03 09:00:00", "2025-01-03 12:00:00"),
("12345678A", 4, 1, "2025-01-04 09:00:00", "2025-01-04 12:00:00"),
("12345678A", 1, 1, "2025-01-05 09:00:00", "2025-01-05 12:00:00"),
("23456789B", 3, 2, "2025-02-01 10:00:00", "2025-02-01 15:00:00"),
("23456789B", 3, 2, "2025-02-02 10:00:00", "2025-02-02 15:00:00"),
("23456789B", 8, 2, "2025-02-03 10:00:00", "2025-02-03 15:00:00"),
("23456789B", 4, 2, "2025-02-04 10:00:00", "2025-02-04 15:00:00"),
("23456789B", 10, 2, "2025-02-05 10:00:00", "2025-02-05 15:00:00");




