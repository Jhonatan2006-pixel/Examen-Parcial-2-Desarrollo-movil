-- ============================================================
--  GeoTracker - Script de creacion para SQL Server (local)
--  Ejecutar en SQL Server Management Studio.
--  Solo crea lo que no existe: no borra tablas ni datos.
-- ============================================================

-- 1. Crear la base de datos
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'TaskFlowDB')
BEGIN
    CREATE DATABASE TaskFlowDB;
END
GO

USE TaskFlowDB;
GO

-- 2. Tabla usuarios
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'usuarios' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.usuarios (
        id          INT IDENTITY(1,1)  NOT NULL,
        nombre      VARCHAR(100)       NOT NULL,
        email       VARCHAR(150)       NOT NULL,
        telefono    VARCHAR(20)            NULL,
        password    VARCHAR(255)       NOT NULL,
        created_at  DATETIMEOFFSET         NULL DEFAULT SYSDATETIMEOFFSET(),
        CONSTRAINT PK_usuarios       PRIMARY KEY (id),
        CONSTRAINT UQ_usuarios_email UNIQUE (email)
    );
END
GO

-- 3. Tabla ubicaciones: cada ubicacion de interes capturada
--    desde la app, asociada al usuario que la guardo
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'ubicaciones' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.ubicaciones (
        id           INT IDENTITY(1,1)  NOT NULL,
        usuario_id   INT                NOT NULL,
        nombre       NVARCHAR(100)      NOT NULL,
        descripcion  NVARCHAR(500)      NOT NULL,
        latitud      DECIMAL(10,7)      NOT NULL,
        longitud     DECIMAL(10,7)      NOT NULL,
        created_at   DATETIMEOFFSET     NOT NULL DEFAULT SYSDATETIMEOFFSET(),
        CONSTRAINT PK_ubicaciones          PRIMARY KEY (id),
        CONSTRAINT FK_ubicaciones_usuario  FOREIGN KEY (usuario_id)
            REFERENCES dbo.usuarios(id),
        CONSTRAINT CK_ubicaciones_latitud  CHECK (latitud  BETWEEN -90  AND 90),
        CONSTRAINT CK_ubicaciones_longitud CHECK (longitud BETWEEN -180 AND 180)
    );

    CREATE INDEX IX_ubicaciones_usuario
        ON dbo.ubicaciones (usuario_id, created_at DESC);
END
GO
