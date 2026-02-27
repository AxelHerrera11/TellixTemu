CREATE OR ALTER PROCEDURE dbo.sp_ingreso_venta
	@Cliente VARCHAR(50),
	@UsuarioSistema VARCHAR(50),
	@MetodoPago INT,
	@PlazoCredito INT,
	@TipoPlazo CHAR(1),
	@FechaLimite DATE,
	@NumeroCuenta VARCHAR(50),
	@TotalVenta DECIMAL(18, 2),
	@Secuencia INT OUTPUT

	AS

	BEGIN
		SET NOCOUNT ON;
		SET XACT_ABORT ON;

		DECLARE @FechaOperacion DATE = CONVERT(DATE, GETDATE()),
				@HoraOperacion DATETIME2 = GETDATE(),
				@EsCredito CHAR(1);

		IF (ISNULL(@Cliente, '') = '')
		THROW 50001, 'El código de cliente no puede estar vacío.', 1;

		IF (@MetodoPago <= 0 OR @MetodoPago IS NULL)
		THROW 50002, 'Método de pago no válido', 1;

		IF (@UsuarioSistema IS NULL OR @UsuarioSistema = '')
		THROW 50003, 'El usuario del sistema no puede estar vacío.', 1;

		IF(@TotalVenta <= 0 OR @TotalVenta IS NULL)
		THROW 50004, 'El total de la venta debe ser mayor a cero.', 1;

		--LOGICA PARA DETERMINAR SI LA VENTA ES A CRÉDITO
		IF (@PlazoCredito < 0 OR @PlazoCredito IS NULL)
		SET @EsCredito = 'N';
		ELSE
		SET @EsCredito = 'S';

		IF(@EsCredito = 'S' AND (@TipoPlazo NOT IN ('D', 'M') OR @TipoPlazo IS NULL))
		THROW 50005, 'El tipo de plazo no es un plazo válido', 1;

		IF(@EsCredito = 'S' AND @PlazoCredito <= 0)
		THROW 50006, 'El plazo de crédito debe ser mayor a cero para ventas a crédito.', 1;

		IF(@EsCredito = 'S' AND @NumeroCuenta IS NULL OR @NumeroCuenta = ' ')
		THROW 50007, 'El número de cuenta no puede estar vacío para ventas a crédito.', 1;

		IF(@EsCredito = 'S' AND (@FechaLimite <= @FechaOperacion OR @FechaLimite IS NULL))
		THROW 50008, 'La fecha límite de pago debe ser posterior a la fecha de operación.', 1;

		IF NOT EXISTS (SELECT 1 FROM dbo.Cliente WHERE Nit = @Cliente)
		THROW 50009, 'El cliente no existe.', 1;

		BEGIN TRANSACTION;
		BEGIN TRY
			PRINT 'Iniciando proceso de ingreso de venta...';

			--Inserts
			INSERT INTO dbo.venta (Cliente, Fecha_Operacion, Hora_Operacion, Usuario_Sistema, fk_Metodo_Pago, Plazo_Credito, Tipo_Plazo, Estado, Total)
			VALUES (@Cliente, @FechaOperacion, @HoraOperacion, @UsuarioSistema, @MetodoPago, @PlazoCredito, @TipoPlazo, 'A', @TotalVenta);

			SET @Secuencia = CAST(SCOPE_IDENTITY() AS INT);

			IF(@EsCredito = 'S')
			BEGIN
				INSERT INTO dbo.Cuenta_Por_Cobrar (Secuencia, Estado, Metodo_Pago, Valor_Total, Valor_Pagado, Fecha_Limite, Numero_Cuenta, Cliente_Nit)
				VALUES (@Secuencia, 'A', @MetodoPago, @TotalVenta, 0, @FechaLimite, @NumeroCuenta, @Cliente);
			END
			COMMIT TRANSACTION;
			PRINT 'Venta ingresada exitosamente con secuencia: ' + CAST(@Secuencia AS VARCHAR);

		END TRY
		BEGIN CATCH
			ROLLBACK TRANSACTION;
			DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
			THROW 59999, @ErrorMessage, 1;
		END CATCH

	END;
	GO

--- PROCEDURE PARA INGRESO DE DETALLE DE VENTAS
CREATE OR ALTER PROCEDURE dbo.sp_ingreso_detalle_venta
	@Secuencia INT,
	@CodigoProducto INT,
	@Cantidad INT,
	@PrecioBruto DECIMAL(18, 2),
	@Descuentos DECIMAL(18, 2),
	@Impuestos DECIMAL(18, 2)

	AS

	BEGIN
		SET NOCOUNT ON;
		SET XACT_ABORT ON;

		DECLARE @Correlativo INT,
				@StockActual INT,
				@StockMinimo INT;

		IF (@Secuencia <= 0 OR @Secuencia IS NULL)
		THROW 60001, 'La secuencia de la venta no es válida.', 1;

		IF (@CodigoProducto <= 0 OR @CodigoProducto IS NULL)
		THROW 60002, 'El código del producto no es válido.', 1;

		IF NOT EXISTS (SELECT 1 FROM dbo.Producto WHERE Codigo = @CodigoProducto)
		THROW 60003, 'El producto no existe.', 1;

		IF (@Cantidad <= 0 OR @Cantidad IS NULL)
		THROW 60004, 'La cantidad debe ser mayor a cero.', 1;

		IF (@PrecioBruto <= 0 OR @PrecioBruto IS NULL)
		THROW 60005, 'El precio bruto debe ser mayor a cero.', 1;

		IF (@Descuentos < 0)
		THROW 60006, 'Los descuentos no pueden ser negativos.', 1;

		IF (@Impuestos < 0)
		THROW 60007, 'Los impuestos no pueden ser negativos.', 1;

		IF (@Impuestos = 0 OR @Impuestos IS NULL)
		SET @Impuestos = 0;

		IF (@Descuentos = 0 OR @Descuentos IS NULL)
		SET @Descuentos = 0;

		IF NOT EXISTS (SELECT 1 FROM dbo.Venta WHERE Secuencia = @Secuencia)
		THROW 60008, 'La venta con la secuencia proporcionada no existe.', 1;

		SELECT @StockActual = Stock_Actual, @StockMinimo = Stock_Minimo FROM dbo.Producto WHERE Codigo = @CodigoProducto;
		IF (@Cantidad > @StockActual)
		THROW 60009, 'La cantidad solicitada excede el stock actual del producto.', 1;
		IF (@StockActual - @Cantidad < @StockMinimo)
		THROW 50010, 'La cantidad solicitada dejaría el stock por debajo del mínimo permitido.', 1;

		BEGIN TRANSACTION;
		BEGIN TRY
			PRINT 'Iniciando proceso de ingreso de detalle de venta...';
		    
			INSERT INTO dbo.Detalle_Venta (Secuencia, Codigo_Producto, Cantidad, Precio_Bruto, Descuentos, Impuestos)
			VALUES (@Secuencia, @CodigoProducto, @Cantidad, @PrecioBruto, @Descuentos, @Impuestos);

			--Bloqueamos antes de actualizar el stock
			SELECT * FROM dbo.producto WITH (UPDLOCK, HOLDLOCK)  WHERE Codigo = @CodigoProducto 
			--Actualizar stock del producto
			UPDATE dbo.Producto
			SET Stock_Actual = Stock_Actual - @Cantidad
			WHERE Codigo = @CodigoProducto;

			COMMIT;
		END TRY
		BEGIN CATCH
			ROLLBACK;
			DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
			THROW 69999, @ErrorMessage, 1;
		END CATCH
	END;
	GO