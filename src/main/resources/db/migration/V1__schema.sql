-- =========================================================
--  Óptica - Esquema relacional
-- =========================================================

CREATE TABLE usuarios (
    id             BIGSERIAL PRIMARY KEY,
    email          VARCHAR(120) NOT NULL UNIQUE,
    password_hash  VARCHAR(100) NOT NULL,
    rol            VARCHAR(20)  NOT NULL CHECK (rol IN ('CLIENTE', 'VENDEDOR')),
    activo         BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE clientes (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT       NOT NULL UNIQUE REFERENCES usuarios (id),
    nombres     VARCHAR(80)  NOT NULL,
    apellidos   VARCHAR(80)  NOT NULL,
    dni         CHAR(8)      NOT NULL UNIQUE CHECK (dni ~ '^[0-9]{8}$'),
    telefono    VARCHAR(15)
);

CREATE TABLE vendedores (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT      NOT NULL UNIQUE REFERENCES usuarios (id),
    nombres     VARCHAR(80) NOT NULL,
    apellidos   VARCHAR(80) NOT NULL
);

CREATE TABLE mediciones (
    id             BIGSERIAL PRIMARY KEY,
    cliente_id     BIGINT       NOT NULL REFERENCES clientes (id),
    vendedor_id    BIGINT       REFERENCES vendedores (id),
    fecha          DATE         NOT NULL DEFAULT CURRENT_DATE,
    od_esfera      NUMERIC(4,2) NOT NULL,
    od_cilindro    NUMERIC(4,2) NOT NULL DEFAULT 0,
    od_eje         INTEGER      NOT NULL DEFAULT 0 CHECK (od_eje BETWEEN 0 AND 180),
    od_adicion     NUMERIC(4,2) NOT NULL DEFAULT 0,
    oi_esfera      NUMERIC(4,2) NOT NULL,
    oi_cilindro    NUMERIC(4,2) NOT NULL DEFAULT 0,
    oi_eje         INTEGER      NOT NULL DEFAULT 0 CHECK (oi_eje BETWEEN 0 AND 180),
    oi_adicion     NUMERIC(4,2) NOT NULL DEFAULT 0,
    dip            NUMERIC(4,1),
    observaciones  VARCHAR(255)
);
CREATE INDEX idx_mediciones_cliente ON mediciones (cliente_id);

CREATE TABLE aros (
    id          BIGSERIAL PRIMARY KEY,
    codigo      VARCHAR(30)   NOT NULL,
    marca       VARCHAR(60)   NOT NULL,
    modelo      VARCHAR(60)   NOT NULL,
    material    VARCHAR(40)   NOT NULL,
    forma       VARCHAR(40)   NOT NULL,
    color       VARCHAR(40),
    precio      NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    stock       INTEGER       NOT NULL DEFAULT 0 CHECK (stock >= 0),
    imagen_url  VARCHAR(255),
    activo      BOOLEAN       NOT NULL DEFAULT TRUE
);

CREATE TABLE lunas (
    id           BIGSERIAL PRIMARY KEY,
    codigo       VARCHAR(30)   NOT NULL,
    tipo         VARCHAR(20)   NOT NULL CHECK (tipo IN ('MONOFOCAL', 'BIFOCAL', 'PROGRESIVO')),
    material     VARCHAR(40)   NOT NULL,
    indice       NUMERIC(3,2)  NOT NULL,
    tratamiento  VARCHAR(80),
    precio       NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    stock        INTEGER       NOT NULL DEFAULT 0 CHECK (stock >= 0),
    activo       BOOLEAN       NOT NULL DEFAULT TRUE
);

CREATE TABLE accesorios (
    id           BIGSERIAL PRIMARY KEY,
    codigo       VARCHAR(30)   NOT NULL,
    nombre       VARCHAR(80)   NOT NULL,
    descripcion  VARCHAR(255),
    precio       NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    stock        INTEGER       NOT NULL DEFAULT 0 CHECK (stock >= 0),
    activo       BOOLEAN       NOT NULL DEFAULT TRUE
);

-- El código es único solo entre productos activos: un producto dado de baja
-- (borrado lógico) libera su código para reutilizarlo.
CREATE UNIQUE INDEX uq_aros_codigo_activo       ON aros (codigo)       WHERE activo;
CREATE UNIQUE INDEX uq_lunas_codigo_activo      ON lunas (codigo)      WHERE activo;
CREATE UNIQUE INDEX uq_accesorios_codigo_activo ON accesorios (codigo) WHERE activo;

CREATE TABLE apartados (
    id          BIGSERIAL PRIMARY KEY,
    cliente_id  BIGINT      NOT NULL REFERENCES clientes (id),
    aro_id      BIGINT      NOT NULL REFERENCES aros (id),
    fecha       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado      VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'VENDIDO', 'CANCELADO'))
);
CREATE INDEX idx_apartados_cliente ON apartados (cliente_id);

CREATE TABLE ventas (
    id           BIGSERIAL PRIMARY KEY,
    cliente_id   BIGINT        NOT NULL REFERENCES clientes (id),
    vendedor_id  BIGINT        NOT NULL REFERENCES vendedores (id),
    medicion_id  BIGINT        NOT NULL REFERENCES mediciones (id),
    aro_id       BIGINT        NOT NULL REFERENCES aros (id),
    luna_id      BIGINT        NOT NULL REFERENCES lunas (id),
    fecha        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal     NUMERIC(10,2) NOT NULL,
    igv          NUMERIC(10,2) NOT NULL,
    total        NUMERIC(10,2) NOT NULL
);
CREATE INDEX idx_ventas_cliente ON ventas (cliente_id);

CREATE TABLE detalle_venta (
    id               BIGSERIAL PRIMARY KEY,
    venta_id         BIGINT        NOT NULL REFERENCES ventas (id) ON DELETE CASCADE,
    tipo_producto    VARCHAR(20)   NOT NULL CHECK (tipo_producto IN ('ARO', 'LUNA', 'ACCESORIO')),
    producto_id      BIGINT        NOT NULL,
    descripcion      VARCHAR(150)  NOT NULL,
    cantidad         INTEGER       NOT NULL CHECK (cantidad > 0),
    precio_unitario  NUMERIC(10,2) NOT NULL,
    subtotal         NUMERIC(10,2) NOT NULL
);
CREATE INDEX idx_detalle_venta ON detalle_venta (venta_id);
