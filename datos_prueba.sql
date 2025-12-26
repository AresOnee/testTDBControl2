-- =====================================================
-- DATOS DE PRUEBA PARA CONTROL 2 - TBD
-- Ejecutar en PostgreSQL con PostGIS habilitado
-- =====================================================

-- Limpiar datos existentes (opcional - descomentar si es necesario)
-- TRUNCATE TABLE tareas CASCADE;
-- TRUNCATE TABLE sectores CASCADE;
-- TRUNCATE TABLE usuarios CASCADE;

-- =====================================================
-- 1. INSERTAR SECTORES (ubicaciones en Santiago, Chile)
-- =====================================================
INSERT INTO sectores (id, nombre, ubicacion) VALUES
(1, 'Construccion Centro', ST_SetSRID(ST_MakePoint(-70.6506, -33.4372), 4326)),
(2, 'Obras Sanitarias Este', ST_SetSRID(ST_MakePoint(-70.6000, -33.4500), 4326)),
(3, 'Reparacion Semaforos Norte', ST_SetSRID(ST_MakePoint(-70.6400, -33.4200), 4326)),
(4, 'Mantencion Calles Sur', ST_SetSRID(ST_MakePoint(-70.6600, -33.4600), 4326)),
(5, 'Proyecto Parque Oeste', ST_SetSRID(ST_MakePoint(-70.6800, -33.4400), 4326)),
(6, 'Iluminacion Providencia', ST_SetSRID(ST_MakePoint(-70.6100, -33.4250), 4326)),
(7, 'Alcantarillado Las Condes', ST_SetSRID(ST_MakePoint(-70.5700, -33.4100), 4326)),
(8, 'Pavimentacion Nunoa', ST_SetSRID(ST_MakePoint(-70.5950, -33.4550), 4326))
ON CONFLICT (id) DO UPDATE SET
  nombre = EXCLUDED.nombre,
  ubicacion = EXCLUDED.ubicacion;

-- Actualizar secuencia de sectores
SELECT setval('sectores_id_seq', (SELECT MAX(id) FROM sectores));

-- =====================================================
-- 2. INSERTAR USUARIOS (con ubicaciones cercanas a sectores)
-- =====================================================
-- NOTA: Este sistema NO usa BCrypt, las contrasenas son texto plano

INSERT INTO usuarios (id, username, password, ubicacion) VALUES
(1, 'juan', 'password123',
   ST_SetSRID(ST_MakePoint(-70.6450, -33.4380), 4326)),
(2, 'maria', 'password123',
   ST_SetSRID(ST_MakePoint(-70.6100, -33.4300), 4326)),
(3, 'pedro', 'password123',
   ST_SetSRID(ST_MakePoint(-70.6300, -33.4450), 4326)),
(4, 'ana', 'password123',
   ST_SetSRID(ST_MakePoint(-70.5900, -33.4200), 4326))
ON CONFLICT (id) DO UPDATE SET
  username = EXCLUDED.username,
  password = EXCLUDED.password,
  ubicacion = EXCLUDED.ubicacion;

-- Actualizar secuencia de usuarios
SELECT setval('usuarios_id_seq', (SELECT MAX(id) FROM usuarios));

-- =====================================================
-- 3. INSERTAR TAREAS
-- Mezcla de PENDIENTE y COMPLETADA para diferentes consultas
-- =====================================================

INSERT INTO tareas (id, titulo, descripcion, estado, fecha_vencimiento, usuario_id, sector_id) VALUES
-- Tareas de Juan (usuario 1)
(1, 'Reparar semaforo', 'Semaforo danado en Av. Principal', 'PENDIENTE',
   CURRENT_DATE + INTERVAL '2 days', 1, 1),
(2, 'Instalar luminarias', 'Nuevas luminarias LED sector norte', 'COMPLETADA',
   CURRENT_DATE - INTERVAL '5 days', 1, 2),
(3, 'Revision alcantarillado', 'Inspeccion rutinaria de alcantarillas', 'COMPLETADA',
   CURRENT_DATE - INTERVAL '10 days', 1, 2),
(4, 'Pintar senaletica', 'Repintar lineas de transito', 'PENDIENTE',
   CURRENT_DATE + INTERVAL '1 day', 1, 3),
(5, 'Cambiar postes', 'Postes danados por accidente', 'COMPLETADA',
   CURRENT_DATE - INTERVAL '3 days', 1, 4),

-- Tareas de Maria (usuario 2)
(6, 'Reparar baches', 'Baches en calle principal', 'PENDIENTE',
   CURRENT_DATE + INTERVAL '5 days', 2, 6),
(7, 'Instalar camaras', 'Camaras de seguridad nuevas', 'COMPLETADA',
   CURRENT_DATE - INTERVAL '7 days', 2, 6),
(8, 'Mantencion parque', 'Riego y poda de arboles', 'COMPLETADA',
   CURRENT_DATE - INTERVAL '2 days', 2, 5),
(9, 'Revision cables', 'Cables electricos expuestos', 'PENDIENTE',
   CURRENT_DATE, 2, 7),

-- Tareas de Pedro (usuario 3)
(10, 'Pavimentar vereda', 'Vereda deteriorada', 'COMPLETADA',
    CURRENT_DATE - INTERVAL '15 days', 3, 4),
(11, 'Instalar bancas', 'Bancas nuevas en plaza', 'PENDIENTE',
    CURRENT_DATE + INTERVAL '7 days', 3, 5),
(12, 'Reparar fuente', 'Fuente de agua danada', 'COMPLETADA',
    CURRENT_DATE - INTERVAL '1 day', 3, 5),
(13, 'Limpieza general', 'Limpieza de area verde', 'PENDIENTE',
    CURRENT_DATE + INTERVAL '3 days', 3, 8),

-- Tareas de Ana (usuario 4)
(14, 'Cambiar focos', 'Focos fundidos en avenida', 'COMPLETADA',
    CURRENT_DATE - INTERVAL '4 days', 4, 7),
(15, 'Revision tuberias', 'Posible fuga de agua', 'PENDIENTE',
    CURRENT_DATE + INTERVAL '1 day', 4, 2),
(16, 'Instalar senales', 'Senales de transito nuevas', 'COMPLETADA',
    CURRENT_DATE - INTERVAL '8 days', 4, 3),
(17, 'Reparar verja', 'Verja danada en parque', 'PENDIENTE',
    CURRENT_DATE + INTERVAL '4 days', 4, 8),

-- Tareas adicionales para mejor visualizacion
(18, 'Pintar murales', 'Murales artisticos en paso peatonal', 'COMPLETADA',
    CURRENT_DATE - INTERVAL '12 days', 1, 1),
(19, 'Instalar basureros', 'Basureros inteligentes', 'PENDIENTE',
    CURRENT_DATE + INTERVAL '6 days', 2, 1),
(20, 'Reparar acera', 'Acera rota por raices', 'COMPLETADA',
    CURRENT_DATE - INTERVAL '6 days', 3, 1)
ON CONFLICT (id) DO UPDATE SET
  titulo = EXCLUDED.titulo,
  descripcion = EXCLUDED.descripcion,
  estado = EXCLUDED.estado,
  fecha_vencimiento = EXCLUDED.fecha_vencimiento,
  usuario_id = EXCLUDED.usuario_id,
  sector_id = EXCLUDED.sector_id;

-- Actualizar secuencia de tareas
SELECT setval('tareas_id_seq', (SELECT MAX(id) FROM tareas));

-- =====================================================
-- VERIFICACION DE DATOS
-- =====================================================

-- Contar registros insertados
SELECT 'Sectores insertados:' AS info, COUNT(*) AS cantidad FROM sectores;
SELECT 'Usuarios insertados:' AS info, COUNT(*) AS cantidad FROM usuarios;
SELECT 'Tareas insertadas:' AS info, COUNT(*) AS cantidad FROM tareas;
SELECT 'Tareas pendientes:' AS info, COUNT(*) AS cantidad FROM tareas WHERE estado = 'PENDIENTE';
SELECT 'Tareas completadas:' AS info, COUNT(*) AS cantidad FROM tareas WHERE estado = 'COMPLETADA';

-- =====================================================
-- CONSULTAS DE PRUEBA (las 9 preguntas)
-- =====================================================

-- P1: Tareas por sector del usuario (ej: usuario 1 = juan)
SELECT 'P1 - Tareas por sector (usuario juan):' AS pregunta;
SELECT s.nombre, COUNT(t.id) as cantidad
FROM tareas t
JOIN sectores s ON t.sector_id = s.id
WHERE t.usuario_id = 1
GROUP BY s.nombre;

-- P2: Tarea mas cercana pendiente
SELECT 'P2 - Tarea pendiente mas cercana a juan:' AS pregunta;
SELECT t.titulo, t.descripcion, s.nombre as sector
FROM tareas t
JOIN sectores s ON t.sector_id = s.id
WHERE t.estado = 'PENDIENTE'
ORDER BY ST_Distance(s.ubicacion, (SELECT ubicacion FROM usuarios WHERE id = 1))
LIMIT 1;

-- P3: Sector con mas tareas completadas en 2km
SELECT 'P3 - Sector top en 2km:' AS pregunta;
SELECT s.nombre
FROM sectores s
JOIN tareas t ON t.sector_id = s.id
WHERE t.estado = 'COMPLETADA'
AND ST_DWithin(
  CAST(s.ubicacion AS geography),
  CAST((SELECT ubicacion FROM usuarios WHERE id = 1) AS geography),
  2000
)
GROUP BY s.id, s.nombre
ORDER BY COUNT(t.id) DESC
LIMIT 1;

-- P4: Promedio distancia tareas completadas (usuario)
SELECT 'P4 - Promedio distancia (juan):' AS pregunta;
SELECT AVG(ST_Distance(
  CAST(s.ubicacion AS geography),
  CAST((SELECT ubicacion FROM usuarios WHERE id = 1) AS geography)
)) as promedio_metros
FROM tareas t
JOIN sectores s ON t.sector_id = s.id
WHERE t.estado = 'COMPLETADA' AND t.usuario_id = 1;

-- P5: Sectores con concentracion de tareas pendientes
SELECT 'P5 - Concentracion de pendientes:' AS pregunta;
SELECT s.nombre, COUNT(t.id) as pendientes
FROM tareas t
JOIN sectores s ON t.sector_id = s.id
WHERE t.estado = 'PENDIENTE'
GROUP BY s.nombre
ORDER BY pendientes DESC;

-- P6: Tarea pendiente mas cercana (usando ubicacion del usuario)
SELECT 'P6 - Tarea pendiente mas cercana a ubicacion de juan:' AS pregunta;
SELECT t.titulo, s.nombre as sector
FROM tareas t
JOIN sectores s ON t.sector_id = s.id
JOIN usuarios u ON t.usuario_id = u.id
WHERE t.estado = 'PENDIENTE'
ORDER BY ST_Distance(s.ubicacion, u.ubicacion)
LIMIT 1;

-- P7: Tareas completadas por usuario y sector
SELECT 'P7 - Tareas completadas por usuario y sector:' AS pregunta;
SELECT u.username, s.nombre, COUNT(t.id) as completadas
FROM tareas t
JOIN sectores s ON t.sector_id = s.id
JOIN usuarios u ON t.usuario_id = u.id
WHERE t.estado = 'COMPLETADA'
GROUP BY u.username, s.nombre
ORDER BY u.username, completadas DESC;

-- P8: Sector con mas tareas completadas en 5km
SELECT 'P8 - Sector top en 5km:' AS pregunta;
SELECT s.nombre
FROM sectores s
JOIN tareas t ON t.sector_id = s.id
WHERE t.estado = 'COMPLETADA'
AND ST_DWithin(
  CAST(s.ubicacion AS geography),
  CAST((SELECT ubicacion FROM usuarios WHERE id = 1) AS geography),
  5000
)
GROUP BY s.id, s.nombre
ORDER BY COUNT(t.id) DESC
LIMIT 1;

-- P9: Promedio distancia global
SELECT 'P9 - Promedio distancia global:' AS pregunta;
SELECT AVG(ST_Distance(s.ubicacion, u.ubicacion)) as promedio_metros
FROM tareas t
JOIN sectores s ON t.sector_id = s.id
JOIN usuarios u ON t.usuario_id = u.id
WHERE t.estado = 'COMPLETADA';
