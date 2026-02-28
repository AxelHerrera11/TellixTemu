CREATE OR ALTER PROCEDURE dbo.sp_ingreso_venta
    @Cliente        VARCHAR(50),
    @UsuarioSistema VARCHAR(50),
    @MetodoPago     INT,
    @PlazoCredito   INT,
    @TipoPlazo      CHAR(1),
    @FechaLimite    DATE,
    @NumeroCuenta   VARCHAR(50),
    @TotalVenta     DECIMAL(18, 2),
    @Detalle        dbo.TVP_DetalleVenta READONLY,
    @Secuencia      INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE @FechaOperacion DATE = CONVERT(DATE, GETDATE()),
            @HoraOperacion  DATETIME2 = SYSDATETIME(),
            @EsCredito      CHAR(1);

    IF (ISNULL(@Cliente, '') = '')         THROW 50001, 'El cliente no puede estar vacío.', 1;
    IF (@MetodoPago IS NULL OR @MetodoPago <= 0) THROW 50002, 'Método de pago no válido.', 1;
    IF (ISNULL(@UsuarioSistema,'') = '')   THROW 50003, 'El usuario del sistema no puede estar vacío.', 1;
    IF (@TotalVenta IS NULL OR @TotalVenta <= 0) THROW 50004, 'El total de la venta debe ser mayor a cero.', 1;

    IF NOT EXISTS (SELECT 1 FROM dbo.cliente WHERE nit = @Cliente)
        THROW 50009, 'El cliente no existe.', 1;

    SET @EsCredito = CASE WHEN ISNULL(@PlazoCredito, 0) > 0 THEN 'S' ELSE 'N' END;

    IF (@EsCredito = 'S' AND (@TipoPlazo IS NULL OR @TipoPlazo NOT IN ('D','M')))
        THROW 50005, 'El tipo de plazo no es válido.', 1;

    IF (@EsCredito = 'S' AND @PlazoCredito <= 0)
        THROW 50006, 'El plazo de crédito debe ser mayor a cero para ventas a crédito.', 1;

    IF (@EsCredito = 'S' AND (ISNULL(LTRIM(RTRIM(@NumeroCuenta)),'') = ''))
        THROW 50007, 'El número de cuenta no puede estar vacío para ventas a crédito.', 1;

    IF (@EsCredito = 'S' AND (@FechaLimite IS NULL OR @FechaLimite <= @FechaOperacion))
        THROW 50008, 'La fecha límite debe ser posterior a la fecha de operación.', 1;

    IF NOT EXISTS (SELECT 1 FROM @Detalle)
        THROW 50011, 'La venta debe incluir al menos un producto en el detalle.', 1;

    -- Validar datos del TVP
    IF EXISTS (SELECT 1 FROM @Detalle WHERE codigo_producto IS NULL OR codigo_producto <= 0)
        THROW 50012, 'Hay productos inválidos en el detalle.', 1;

    IF EXISTS (SELECT 1 FROM @Detalle WHERE cantidad IS NULL OR cantidad <= 0)
        THROW 50013, 'Hay cantidades inválidas en el detalle.', 1;

    IF EXISTS (SELECT 1 FROM @Detalle WHERE precio_bruto IS NULL OR precio_bruto <= 0)
        THROW 50014, 'Hay precios brutos inválidos en el detalle.', 1;

    IF EXISTS (SELECT 1 FROM @Detalle WHERE descuentos IS NULL OR descuentos < 0 OR impuestos IS NULL OR impuestos < 0)
        THROW 50015, 'Descuentos/Impuestos no pueden ser negativos ni nulos.', 1;

    -- Validar que los productos existan
    IF EXISTS (
        SELECT 1
        FROM @Detalle d
        LEFT JOIN dbo.producto p ON p.codigo = d.codigo_producto
        WHERE p.codigo IS NULL
    )
        THROW 50016, 'El detalle contiene productos que no existen.', 1;

    BEGIN TRANSACTION;
    BEGIN TRY

        /* 1) Insertar encabezado venta */
        INSERT INTO dbo.venta
            (cliente, fecha_operacion, hora_operacion, usuario_sistema, fk_metodo_pago, plazo_credito, tipo_plazo, estado, total)
        VALUES
            (@Cliente, @FechaOperacion, @HoraOperacion, @UsuarioSistema, @MetodoPago,
             CASE WHEN @EsCredito='S' THEN @PlazoCredito ELSE NULL END,
             CASE WHEN @EsCredito='S' THEN @TipoPlazo ELSE NULL END,
             'A', @TotalVenta);

        SET @Secuencia = CAST(SCOPE_IDENTITY() AS INT);

        /* 2) Si es crédito, insertar cuenta por cobrar */
        IF (@EsCredito = 'S')
        BEGIN
            INSERT INTO dbo.cuenta_por_cobrar
                (secuencia, estado, metodo_pago, valor_total, valor_pagado, fecha_limite, numero_cuenta, cliente_nit)
            VALUES
                (@Secuencia, 'A', @MetodoPago, @TotalVenta, 0, @FechaLimite, @NumeroCuenta, @Cliente);
        END

        /* 3) Bloquear productos a afectar y validar stock (sumando cantidades por producto) */
        ;WITH req AS (
            SELECT codigo_producto, SUM(cantidad) AS cantidad_total
            FROM @Detalle
            GROUP BY codigo_producto
        )
        -- Locks para evitar carreras (ventas simultáneas)
        SELECT p.codigo
        FROM dbo.producto p WITH (UPDLOCK, HOLDLOCK)
        JOIN req r ON r.codigo_producto = p.codigo;

        -- Validar stock vs lo pedido (ya con lock puesto)
        IF EXISTS (
            SELECT 1
            FROM dbo.producto p
            JOIN (
                SELECT codigo_producto, SUM(cantidad) AS cantidad_total
                FROM @Detalle
                GROUP BY codigo_producto
            ) r ON r.codigo_producto = p.codigo
            WHERE r.cantidad_total > p.stock_actual
        )
            THROW 60009, 'La cantidad solicitada excede el stock actual.', 1;

        IF EXISTS (
            SELECT 1
            FROM dbo.producto p
            JOIN (
                SELECT codigo_producto, SUM(cantidad) AS cantidad_total
                FROM @Detalle
                GROUP BY codigo_producto
            ) r ON r.codigo_producto = p.codigo
            WHERE (p.stock_actual - r.cantidad_total) < p.stock_minimo
        )
            THROW 50010, 'La venta dejaría stock por debajo del mínimo permitido.', 1;

        /* 4) Insertar TODOS los detalles de una sola vez */
        INSERT INTO dbo.detalle_venta
            (secuencia, codigo_producto, cantidad, precio_bruto, descuentos, impuestos)
        SELECT
            @Secuencia, codigo_producto, cantidad, precio_bruto, descuentos, impuestos
        FROM @Detalle;

        /* 5) Actualizar stock por producto (sumado) */
        ;WITH req AS (
            SELECT codigo_producto, SUM(cantidad) AS cantidad_total
            FROM @Detalle
            GROUP BY codigo_producto
        )
        UPDATE p
        SET p.stock_actual = p.stock_actual - r.cantidad_total
        FROM dbo.producto p
        JOIN req r ON r.codigo_producto = p.codigo;

        /* 6) (Opcional pero recomendado) Registrar movimiento en inventario */
        INSERT INTO dbo.inventario (codigo_producto, cantidad_afectada, motivo, operacion, usuario)
        SELECT
            codigo_producto,
            SUM(cantidad) AS cantidad_afectada,
            CONCAT('Venta secuencia ', @Secuencia),
            'V', -- S = salida (según tu convención)
            @UsuarioSistema
        FROM @Detalle
        GROUP BY codigo_producto;

        COMMIT TRANSACTION;

    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        THROW 59999, @ErrorMessage, 1;
    END CATCH
END;
GO