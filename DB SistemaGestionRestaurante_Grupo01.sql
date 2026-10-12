CREATE DATABASE sistemaGestionRestaurante;

USE sistemaGestionRestaurante;


CREATE TABLE Empleado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dni VARCHAR(8) NOT NULL UNIQUE,
    turno VARCHAR(50) NOT NULL
);

CREATE TABLE Cajero (
    id INT PRIMARY KEY,

    CONSTRAINT fk_cajero_empleado
        FOREIGN KEY (id)
        REFERENCES Empleado(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Mozo (
    id INT PRIMARY KEY,

    CONSTRAINT fk_mozo_empleado
        FOREIGN KEY (id)
        REFERENCES Empleado(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


CREATE TABLE Mesa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    capacidad INT NOT NULL,
    estado VARCHAR(30) NOT NULL,

    CONSTRAINT chk_mesa_capacidad
        CHECK (capacidad > 0)
);

CREATE TABLE IF NOT EXISTS AtencionMesa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    mesa_id INT NOT NULL,
    mozo_id INT NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_fin DATETIME NULL,
    estado VARCHAR(30) NOT NULL,

    CONSTRAINT fk_atencion_mesa
        FOREIGN KEY (mesa_id)
        REFERENCES Mesa(id),

    CONSTRAINT fk_atencion_mozo
        FOREIGN KEY (mozo_id)
        REFERENCES Mozo(id)
);

-- Datos de peueba--
INSERT INTO Empleado (nombre, dni, turno)
VALUES
('Juan Perez', '12345678', 'Mañana'),
('Monica Lopez', '87654321', 'Tarde');


INSERT INTO Mozo (id)
VALUES
(1),
(2);

INSERT INTO Mesa (numero, capacidad, estado)
VALUES
(1, 4, 'DISPONIBLE'),
(2, 4, 'DISPONIBLE'),
(3, 6, 'DISPONIBLE'),
(4, 2, 'DISPONIBLE');

-- Crear un empleado para el rol de cajero.
INSERT INTO Empleado (nombre, dni, turno)
VALUES ('Carlos Ramirez', '45678912', 'Mañana');

-- Asignar el empleado registrado al rol de cajero.
INSERT INTO Cajero (id)
VALUES (LAST_INSERT_ID());



-- cajero de registro correctamente--



-- separados mozo y cajero--

SELECT e.id, e.nombre, e.dni, e.turno, 'MOZO' AS rol
FROM Empleado e
INNER JOIN Mozo m ON e.id = m.id

UNION ALL

SELECT e.id, e.nombre, e.dni, e.turno, 'CAJERO' AS rol
FROM Empleado e
INNER JOIN Cajero c ON e.id = c.id;




CREATE TABLE Plato (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(10,2) NOT NULL,
    categoria VARCHAR(50),
    estado VARCHAR(30) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    imagen VARCHAR(255),

    CONSTRAINT chk_plato_precio
        CHECK (precio >= 0),

    CONSTRAINT chk_plato_stock
        CHECK (stock >= 0)
        

        
);


CREATE TABLE Pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fechaHora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    subTotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    mesa_id INT NOT NULL,
    mozo_id INT NOT NULL,

    CONSTRAINT fk_pedido_mesa
        FOREIGN KEY (mesa_id)
        REFERENCES Mesa(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_pedido_mozo
        FOREIGN KEY (mozo_id)
        REFERENCES Mozo(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT chk_pedido_subtotal
        CHECK (subTotal >= 0)
);


CREATE TABLE DetallePedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cantidad INT NOT NULL,
    precioUnitario DECIMAL(10,2) NOT NULL,
    subTotal DECIMAL(10,2) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',

    pedido_id INT NOT NULL,
    plato_id INT NOT NULL,

    CONSTRAINT fk_detalle_pedido
        FOREIGN KEY (pedido_id)
        REFERENCES Pedido(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_detalle_plato
        FOREIGN KEY (plato_id)
        REFERENCES Plato(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT chk_detalle_cantidad
        CHECK (cantidad > 0),

    CONSTRAINT chk_detalle_precio
        CHECK (precioUnitario >= 0),

    CONSTRAINT chk_detalle_subtotal
        CHECK (subTotal >= 0)
);
ALTER TABLE DetallePedido
ADD COLUMN estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE';

UPDATE DetallePedido d
INNER JOIN Pedido p ON d.pedido_id = p.id
SET d.estado = p.estado;

-- Lean --
CREATE TABLE Cuenta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fechaGeneracion DATE NOT NULL,
    subTotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    igv DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    metodoPago VARCHAR(30) NULL,
    fechaPago DATETIME NULL,
    
    pedido_id INT NOT NULL UNIQUE,

    CONSTRAINT fk_cuenta_pedido
        FOREIGN KEY (pedido_id)
        REFERENCES Pedido(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT chk_cuenta_subtotal
        CHECK (subTotal >= 0),

    CONSTRAINT chk_cuenta_igv
        CHECK (igv >= 0),

    CONSTRAINT chk_cuenta_total
        CHECK (total >= 0)
);


CREATE TABLE Pago (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fechaHora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodoPago VARCHAR(30) NOT NULL,
    monto DECIMAL(10,2) NOT NULL,

    cuenta_id INT NOT NULL UNIQUE,
    mozo_id INT NULL,
    cajero_id INT NULL,

    CONSTRAINT fk_pago_cuenta
        FOREIGN KEY (cuenta_id)
        REFERENCES Cuenta(id),

    CONSTRAINT fk_pago_mozo
        FOREIGN KEY (mozo_id)
        REFERENCES Mozo(id),

    CONSTRAINT fk_pago_cajero
        FOREIGN KEY (cajero_id)
        REFERENCES Cajero(id),

    CONSTRAINT chk_pago_monto
        CHECK (monto > 0),

    CONSTRAINT chk_pago_responsable
        CHECK (
            (mozo_id IS NOT NULL AND cajero_id IS NULL)
            OR
            (mozo_id IS NULL AND cajero_id IS NOT NULL)
        )
);


-- Comprueba el nombre del índice UNIQUE de cuenta_id.
SHOW INDEX FROM Pago;

-- En la tabla actual, normalmente el índice se llama cuenta_id.
-- Si SHOW INDEX muestra otro nombre para el índice UNIQUE,
-- reemplaza cuenta_id por el nombre que aparezca.
ALTER TABLE Pago
DROP INDEX cuenta_id;

-- Añadir datos necesarios para registrar los intentos de pago.
ALTER TABLE Pago
    ADD COLUMN modalidadPago VARCHAR(20) NOT NULL DEFAULT 'EN_CAJA',
    ADD COLUMN estadoPago VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    ADD COLUMN montoRecibido DECIMAL(10,2) NULL,
    ADD COLUMN vuelto DECIMAL(10,2) NULL,
    ADD COLUMN referencia VARCHAR(100) NULL,
    ADD COLUMN observacion VARCHAR(255) NULL;

-- Restringir los valores permitidos.
ALTER TABLE Pago
    ADD CONSTRAINT chk_pago_modalidad
        CHECK (modalidadPago IN ('DIRECTO_MOZO', 'EN_CAJA')),
    ADD CONSTRAINT chk_pago_estado
        CHECK (estadoPago IN ('PENDIENTE', 'CONFIRMADO', 'RECHAZADO'));
        




INSERT INTO Plato (id, nombre, descripcion, precio, categoria, estado, stock, imagen)
VALUES
(1, 'Chaufa de Pollo', 'Arroz chaufa salteado con pollo y verduras frescas.', 18.00, 'Chifa', 'DISPONIBLE', 50, '/platos/chaufa_pollo.jpg'),
(2, 'Chaufa de Chancho', 'Arroz chaufa con carne de chancho asado y verduras.', 18.00, 'Chifa', 'DISPONIBLE', 50, '/platos/chaufa_chancho.jpg'),
(3, 'Tallarín con Pollo Picado', 'Tallarín saltado con pollo picado y verduras al wok.', 20.00, 'Chifa', 'DISPONIBLE', 50, '/platos/tallarin_pollo_picado.jpg'),
(4, 'Tallarín con Pollo en Trozos', 'Tallarín saltado con pollo en trozos y verduras frescas.', 20.00, 'Chifa', 'DISPONIBLE', 50, '/platos/tallarin_pollo_trozos.jpg'),
(5, 'Tallarín con Chancho Asado', 'Tallarín saltado con chancho asado y verduras.', 20.00, 'Chifa', 'DISPONIBLE', 50, '/platos/tallarin_chancho_asado.jpg'),
(6, 'Combinado de Pollo', 'Mezcla de chaufa y tallarín con pollo y verduras.', 20.00, 'Chifa', 'DISPONIBLE', 50, '/platos/combinado_pollo.jpg'),
(7, 'Chaufa y Chijaukay', 'Arroz chaufa acompañado de pollo chijaukay en salsa agridulce.', 20.00, 'Chifa', 'DISPONIBLE', 50, '/platos/chaufa_chijaukay.jpg'),
(8, 'Chaufa y Pollo Enrollado', 'Arroz chaufa con pollo enrollado y verduras.', 20.00, 'Chifa', 'DISPONIBLE', 50, '/platos/chaufa_pollo_enrollado.jpg'),
(9, 'Chaufa y Pollo con Tausí', 'Chaufa acompañado de pollo en salsa tausí.', 20.00, 'Chifa', 'DISPONIBLE', 50, '/platos/chaufa_pollo_tausi.jpg'),
(10, 'Chaufa y Pollo con Piña', 'Chaufa con pollo en salsa dulce de piña.', 20.00, 'Chifa', 'DISPONIBLE', 50, '/platos/chaufa_pollo_pina.jpg');