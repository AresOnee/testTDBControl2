-- 1. Habilitar la extensión PostGIS (Requisito)
CREATE EXTENSION IF NOT EXISTS postgis;

-- 2. Tabla de Usuarios
-- Requisito: Nombre de usuario, contraseña y punto geoespacial[cite: 18, 19].
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Recuerda hashear la contraseña en el backend (Spring)
    ubicacion GEOMETRY(POINT, 4326) NOT NULL -- SRID 4326 es el estándar para coordenadas GPS (WGS 84)
);

-- 3. Tabla de Sectores
-- Requisito: Sectores específicos (construcción, calles, etc.) georreferenciados.
CREATE TABLE sectores (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL, -- Ej: "Reparación Semáforo Av. Matta", "Obras Calle 1"
    tipo VARCHAR(50), -- Opcional: Para categorizar si es "Construcción", "Vialidad", etc.
    ubicacion GEOMETRY(POINT, 4326) NOT NULL -- La ubicación espacial del sector
);

-- 4. Tabla de Tareas
-- Requisito: Título, descripción, fecha vencimiento, estado y asociación a sector[cite: 22, 23, 26].
CREATE TABLE tareas (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha_vencimiento TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'COMPLETADA')), -- [cite: 26, 29]
    usuario_id INT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE, -- El usuario dueño de la tarea
    sector_id INT NOT NULL REFERENCES sectores(id) ON DELETE SET NULL, -- Asociación geográfica 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Índices Espaciales (Muy importante para el rendimiento de las consultas PostGIS)
CREATE INDEX idx_usuarios_ubicacion ON usuarios USING GIST (ubicacion);
CREATE INDEX idx_sectores_ubicacion ON sectores USING GIST (ubicacion);