create database tellix;

use tellix;

/* =====================================================
   CATALOGOS
===================================================== */

CREATE TABLE opciones_sistema(
    codigo INT IDENTITY(1,1) PRIMARY KEY,
    descripcion VARCHAR(200) NOT NULL
);

CREATE TABLE roles(
    rol_usuario VARCHAR(100) PRIMARY KEY,
    descripcion VARCHAR(200) NOT NULL,
    nivel INT NOT NULL
);

CREATE TABLE banco(
    codigo_number INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE tipo_cuenta(
    codigo VARCHAR(50) PRIMARY KEY,
    descripcion VARCHAR(200) NOT NULL
);

CREATE TABLE tipo_contacto(
    codigo INT IDENTITY(1,1) PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL
);

CREATE TABLE tipo_cliente(
    codigo INT IDENTITY(1,1) PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL
);

CREATE TABLE metodo_liquidacion(
    codigo INT IDENTITY(1,1) PRIMARY KEY,
    descripcion VARCHAR(200) NOT NULL
);

CREATE TABLE categoria(
    codigo INT IDENTITY(1,1) PRIMARY KEY,
    descripcion VARCHAR(200) NOT NULL
);

CREATE TABLE marca(
    marca VARCHAR(100) PRIMARY KEY,
    descripcion VARCHAR(200) NOT NULL
);

CREATE TABLE medida(
    codigo VARCHAR(50) PRIMARY KEY,
    descripcion VARCHAR(200) NOT NULL
);

CREATE TABLE impuesto(
    codigo INT IDENTITY(1,1) PRIMARY KEY,
    descripcion VARCHAR(200) NOT NULL,
    tipo_calculo CHAR(1) NOT NULL,
    valor DECIMAL(18,2) NOT NULL
);

CREATE TABLE descuento(
    codigo INT IDENTITY(1,1) PRIMARY KEY,
    descripcion VARCHAR(200) NOT NULL,
    tipo_calculo CHAR(1) NOT NULL,
    valor DECIMAL(18,2) NOT NULL
);

/* =====================================================
   TABLAS GENERALES
===================================================== */

CREATE TABLE proveedor(
    nit_proveedor VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    direccion_fiscal VARCHAR(200) NOT NULL,
    telefono VARCHAR(50) NOT NULL
);

CREATE TABLE representante(
    nit_representante VARCHAR(50) NOT NULL,
    fk_proveedor_nit VARCHAR(50) NOT NULL,
    codigo INT NOT NULL,
    nombre1 VARCHAR(100) NOT NULL,
    nombre2 VARCHAR(100) NOT NULL,
    apellido1 VARCHAR(100) NOT NULL,
    apellido2 VARCHAR(100) NOT NULL,
    apellido_casada VARCHAR(100),
    PRIMARY KEY(nit_representante,fk_proveedor_nit),
    UNIQUE(nit_representante),
    FOREIGN KEY(fk_proveedor_nit) REFERENCES proveedor(nit_proveedor)
);

CREATE TABLE cuenta(
    numero VARCHAR(50) PRIMARY KEY,
    banco_number INT NOT NULL,
    titular VARCHAR(200) NOT NULL,
    tipo_cuenta VARCHAR(50) NOT NULL,
    estado CHAR(1) DEFAULT 'A' NOT NULL,
    descripcion VARCHAR(200),
    FOREIGN KEY(banco_number) REFERENCES banco(codigo_number),
    FOREIGN KEY(tipo_cuenta) REFERENCES tipo_cuenta(codigo)
);

CREATE TABLE empleado(
    codigo_empleado INT IDENTITY(1,1) PRIMARY KEY,
    documento_identificacion VARCHAR(100) NOT NULL,
    nombre_1 VARCHAR(100) NOT NULL,
    nombre_2 VARCHAR(100) NOT NULL,
    apellido_1 VARCHAR(100) NOT NULL,
    apellido_2 VARCHAR(100) NOT NULL,
    apellido_casada VARCHAR(100),
    estado CHAR(1) DEFAULT 'A' NOT NULL,
    codigo_jefe INT,
    FOREIGN KEY(codigo_jefe) REFERENCES empleado(codigo_empleado)
);

CREATE TABLE usuario(
    usuario VARCHAR(20) PRIMARY KEY,
    codigo_empleado INT NOT NULL,
    contrasena VARCHAR(200),
    rol_usuario VARCHAR(100) NOT NULL,
    FOREIGN KEY(codigo_empleado) REFERENCES empleado(codigo_empleado),
    FOREIGN KEY(rol_usuario) REFERENCES roles(rol_usuario)
);

CREATE TABLE cliente(
    nit VARCHAR(50) PRIMARY KEY,
    codigo INT NOT NULL,
    nombre_1 VARCHAR(100) NOT NULL,
    nombre_2 VARCHAR(100) NOT NULL,
    nombre_3 VARCHAR(100),
    apellido_1 VARCHAR(100) NOT NULL,
    apellido_2 VARCHAR(100) NOT NULL,
    apellido_casada VARCHAR(100),
    tipo_cliente INT NOT NULL,
    limite_credito DECIMAL(18,2) NOT NULL,
    estado CHAR(1) DEFAULT 'A' NOT NULL,
    direccion VARCHAR(200),
    FOREIGN KEY(tipo_cliente) REFERENCES tipo_cliente(codigo)
);

CREATE TABLE producto(
    codigo INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion VARCHAR(100),
    stock_minimo INT NOT NULL,
    stock_actual INT NOT NULL,
    estado CHAR(1) DEFAULT 'A' NOT NULL,
    categoria INT NOT NULL,
    marca VARCHAR(100) NOT NULL,
    medida VARCHAR(50) NOT NULL,
    cantidad_medida INT NOT NULL,
    FOREIGN KEY(categoria) REFERENCES categoria(codigo),
    FOREIGN KEY(marca) REFERENCES marca(marca),
    FOREIGN KEY(medida) REFERENCES medida(codigo)
);

CREATE TABLE venta(
    secuencia INT IDENTITY(1,1) PRIMARY KEY,
    cliente VARCHAR(50) NOT NULL,
    fecha_operacion DATE NOT NULL,
    hora_operacion DATETIME2 NOT NULL,
    usuario_sistema VARCHAR(20) NOT NULL,
    fk_metodo_pago INT NOT NULL,
    plazo_credito INT,
    tipo_plazo CHAR(1),
    estado CHAR(1) NOT NULL,
    total DECIMAL(18,2) NOT NULL,
    FOREIGN KEY(cliente) REFERENCES cliente(nit),
    FOREIGN KEY(fk_metodo_pago) REFERENCES metodo_liquidacion(codigo)
);

CREATE TABLE compra(
    no_documento INT IDENTITY(1,1) PRIMARY KEY,
    proveedor VARCHAR(50) NOT NULL,
    representante VARCHAR(50) NOT NULL,
    fecha_operacion DATE NOT NULL,
    hora_operacion DATETIME2 NOT NULL,
    usuario_sistema VARCHAR(20) NOT NULL,
    fk_metodo_pago INT NOT NULL,
    plazo_credito INT,
    tipo_plazo CHAR(1),
    estado CHAR(1) NOT NULL,
    FOREIGN KEY(proveedor) REFERENCES proveedor(nit_proveedor),
    FOREIGN KEY(representante) REFERENCES representante(nit_representante),
    FOREIGN KEY(fk_metodo_pago) REFERENCES metodo_liquidacion(codigo)
);

/* =====================================================
   RELACIONALES
===================================================== */

CREATE TABLE asignacion_impuesto(
    secuencia INT IDENTITY(1,1),
    codigo_producto INT NOT NULL,
    codigo_impuesto INT NOT NULL,
    valor DECIMAL(18,2) NOT NULL,
    aplicaciones VARCHAR(200) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado CHAR(1),
    PRIMARY KEY(secuencia,codigo_producto),
    FOREIGN KEY(codigo_producto) REFERENCES producto(codigo),
    FOREIGN KEY(codigo_impuesto) REFERENCES impuesto(codigo)
);

CREATE TABLE asignacion_descuento(
    secuencia INT IDENTITY(1,1),
    codigo_producto INT NOT NULL,
    codigo_descuento INT NOT NULL,
    valor DECIMAL(18,2) NOT NULL,
    aplicaciones CHAR(1) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado CHAR(1) DEFAULT 'A' NOT NULL,
    PRIMARY KEY(secuencia,codigo_producto),
    FOREIGN KEY(codigo_producto) REFERENCES producto(codigo),
    FOREIGN KEY(codigo_descuento) REFERENCES descuento(codigo)
);

CREATE TABLE precio(
    codigo_producto INT NOT NULL,
    aplicacion CHAR(1) NOT NULL,
    precio_venta DECIMAL(18,2) NOT NULL,
    inicio_vigencia DATE NOT NULL,
    fin_vigencia DATE NOT NULL,
    estado CHAR(1),
    PRIMARY KEY(codigo_producto,aplicacion),
    FOREIGN KEY(codigo_producto) REFERENCES producto(codigo)
);

CREATE TABLE inventario(
    codigo_producto INT NOT NULL,
    correlativo INT IDENTITY(1,1),
    cantidad_afectada INT NOT NULL,
    motivo VARCHAR(200) NOT NULL,
    operacion CHAR(1) NOT NULL,
    usuario VARCHAR(20) NOT NULL,
    fecha_operacion DATE DEFAULT GETDATE() NOT NULL,
    hora_operacion DATETIME2 DEFAULT SYSDATETIME() NOT NULL,
    PRIMARY KEY(codigo_producto,correlativo),
    FOREIGN KEY(codigo_producto) REFERENCES producto(codigo),
    FOREIGN KEY(usuario) REFERENCES usuario(usuario)
);

CREATE TABLE detalle_venta(
    secuencia INT NOT NULL,
    correlativo INT IDENTITY(1,1),
    codigo_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_bruto DECIMAL(18,2) NOT NULL,
    descuentos DECIMAL(18,2) NOT NULL,
    impuestos DECIMAL(18,2) NOT NULL,
    PRIMARY KEY(secuencia,correlativo),
    FOREIGN KEY(secuencia) REFERENCES venta(secuencia),
    FOREIGN KEY(codigo_producto) REFERENCES producto(codigo)
);

CREATE TABLE cuenta_por_cobrar(
    correlativo INT IDENTITY(1,1),
    secuencia INT NOT NULL,
    estado CHAR(1) NOT NULL,
    metodo_pago INT NOT NULL,
    valor_total DECIMAL(18,2) NOT NULL,
    valor_pagado DECIMAL(18,2) NOT NULL,
    fecha_limite DATE NOT NULL,
    numero_cuenta VARCHAR(50) NOT NULL,
    cliente_nit VARCHAR(50) NOT NULL,
    PRIMARY KEY(correlativo,secuencia),
    FOREIGN KEY(secuencia) REFERENCES venta(secuencia),
    FOREIGN KEY(metodo_pago) REFERENCES metodo_liquidacion(codigo),
    FOREIGN KEY(numero_cuenta) REFERENCES cuenta(numero),
    FOREIGN KEY(cliente_nit) REFERENCES cliente(nit)
);

CREATE TABLE detalle_compra(
    no_documento INT NOT NULL,
    correlativo INT IDENTITY(1,1),
    codigo_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_bruto DECIMAL(18,2) NOT NULL,
    descuentos DECIMAL(18,2) NOT NULL,
    impuestos DECIMAL(18,2) NOT NULL,
    PRIMARY KEY(no_documento,correlativo),
    FOREIGN KEY(no_documento) REFERENCES compra(no_documento),
    FOREIGN KEY(codigo_producto) REFERENCES producto(codigo)
);

CREATE TABLE cuenta_por_pagar(
    correlativo INT IDENTITY(1,1),
    no_documento INT NOT NULL,
    estado CHAR(1) NOT NULL,
    metodo_pago INT NOT NULL,
    valor_total DECIMAL(18,2) NOT NULL,
    valor_pagado DECIMAL(18,2) NOT NULL,
    fecha_limite DATE NOT NULL,
    numero_cuenta VARCHAR(50) NOT NULL,
    banco VARCHAR(100) NOT NULL,
    PRIMARY KEY(correlativo,no_documento),
    FOREIGN KEY(no_documento) REFERENCES compra(no_documento),
    FOREIGN KEY(metodo_pago) REFERENCES metodo_liquidacion(codigo),
    FOREIGN KEY(numero_cuenta) REFERENCES cuenta(numero)
);

/* =====================================================
   CONTACTOS Y ACCESOS
===================================================== */

CREATE TABLE contacto_cliente(
    identificacion INT NOT NULL,
    correlativo_contacto INT IDENTITY(1,1),
    tipo_contacto INT NOT NULL,
    info_contacto VARCHAR(200) NOT NULL,
    telefono VARCHAR(50),
    fk_cliente_nit VARCHAR(50) NOT NULL,
    PRIMARY KEY(identificacion,correlativo_contacto),
    FOREIGN KEY(tipo_contacto) REFERENCES tipo_contacto(codigo),
    FOREIGN KEY(fk_cliente_nit) REFERENCES cliente(nit)
);

CREATE TABLE contacto_empleado(
    correlativo_contacto INT IDENTITY(1,1),
    identificacion INT NOT NULL,
    info_contacto VARCHAR(200) NOT NULL,
    tipo_contacto INT NOT NULL,
    fk_empleado_codigo INT NOT NULL,
    PRIMARY KEY(correlativo_contacto,identificacion),
    FOREIGN KEY(tipo_contacto) REFERENCES tipo_contacto(codigo),
    FOREIGN KEY(fk_empleado_codigo) REFERENCES empleado(codigo_empleado)
);

CREATE TABLE asignacion_accesos(
    correlativo INT IDENTITY(1,1),
    rol_usuario VARCHAR(100) NOT NULL,
    opcion_sistema INT NOT NULL,
    PRIMARY KEY(correlativo,rol_usuario,opcion_sistema),
    FOREIGN KEY(rol_usuario) REFERENCES roles(rol_usuario),
    FOREIGN KEY(opcion_sistema) REFERENCES opciones_sistema(codigo)
);

CREATE TABLE contacto_representante(
    correlativo_contacto INT IDENTITY(1,1),
    codigo INT NOT NULL,
    info_contacto VARCHAR(200) NOT NULL,
    tipo_contacto INT NOT NULL,
    fk_representante VARCHAR(50) NOT NULL,
    PRIMARY KEY(correlativo_contacto,codigo),
    FOREIGN KEY(tipo_contacto) REFERENCES tipo_contacto(codigo),
    FOREIGN KEY(fk_representante) REFERENCES representante(nit_representante)
);


CREATE TYPE dbo.TVP_DetalleVenta AS TABLE
    (
    codigo_producto INT NOT NULL,
    cantidad        INT NOT NULL,
    precio_bruto    DECIMAL(18,2) NOT NULL,
    descuentos      DECIMAL(18,2) NOT NULL,
    impuestos       DECIMAL(18,2) NOT NULL
    );
GO



/**Utilizar el store procedure**/
/*
DECLARE @Detalle dbo.TVP_DetalleVenta;
DECLARE @Secuencia INT;

INSERT INTO @Detalle (codigo_producto, cantidad, precio_bruto, descuentos, impuestos)
VALUES
    (1, 2, 50.00, 0.00, 6.00),
    (3, 1, 100.00, 10.00, 10.80);

EXEC dbo.sp_ingreso_venta
    @Cliente        = 'CF',
    @UsuarioSistema = 'kevin',
    @MetodoPago     = 1,
    @PlazoCredito   = 0,
    @TipoPlazo      = NULL,
    @FechaLimite    = NULL,
    @NumeroCuenta   = NULL,
    @TotalVenta     = 190.80,
    @Detalle        = @Detalle,
    @Secuencia      = @Secuencia OUTPUT;

SELECT @Secuencia AS SecuenciaGenerada;
 */