# Control 2 - Taller de Base de Datos
## Sistema de Gestion de Tareas con Geolocalizacion

Universidad de Santiago de Chile
Facultad de Ingenieria
Departamento de Ingenieria Informatica

---

## 1. Descripcion General

Sistema web para la gestion de tareas con capacidades geoespaciales. Permite a los usuarios registrarse con una ubicacion geografica, crear tareas asociadas a sectores geograficos, y realizar consultas espaciales avanzadas utilizando PostGIS.

### 1.1 Funcionalidades Principales

- Registro de usuarios con ubicacion geografica (latitud/longitud)
- Autenticacion mediante JWT (JSON Web Tokens)
- Gestion completa de tareas (crear, editar, eliminar, completar)
- Asociacion de tareas a sectores geograficos
- Notificaciones de tareas proximas a vencer
- Consultas geoespaciales avanzadas
- Visualizacion de estadisticas espaciales

---

## 2. Arquitectura del Sistema

### 2.1 Arquitectura General

```
+------------------+     +------------------+     +------------------+
|                  |     |                  |     |                  |
|    Frontend      |---->|    Backend       |---->|   PostgreSQL     |
|    (Vue.js)      |     |  (Spring Boot)   |     |   + PostGIS      |
|                  |     |                  |     |                  |
+------------------+     +------------------+     +------------------+
     Puerto 80          Puerto 8080              Puerto 5432
```

### 2.2 Patron de Diseno

El backend sigue una arquitectura en capas:

```
Controller  -->  Service  -->  Repository  -->  Database
    |               |              |
    v               v              v
   DTOs         Entities       JPA/SQL
```

---

## 3. Tecnologias Utilizadas

### 3.1 Frontend

| Tecnologia | Version | Proposito |
|------------|---------|-----------|
| Vue.js | 3.x | Framework principal |
| Vite | 6.x | Build tool |
| Vuetify | 3.x | Componentes UI |
| Leaflet | 1.9.x | Mapas interactivos |
| Axios | 1.x | Cliente HTTP |
| Vue Router | 4.x | Navegacion SPA |

### 3.2 Backend

| Tecnologia | Version | Proposito |
|------------|---------|-----------|
| Spring Boot | 3.5.5 | Framework principal |
| Spring Security | 3.5.5 | Seguridad y autenticacion |
| Spring Data JPA | 3.5.5 | Persistencia |
| Hibernate Spatial | 6.6.0 | Soporte geoespacial |
| JWT (jjwt) | 0.11.5 | Tokens de autenticacion |
| BCrypt | - | Hash de contrasenas |
| Bean Validation | - | Validacion de entrada |

### 3.3 Base de Datos

| Tecnologia | Version | Proposito |
|------------|---------|-----------|
| PostgreSQL | 15.x | Base de datos relacional |
| PostGIS | 3.3.x | Extension geoespacial |

---

## 4. Estructura del Proyecto

### 4.1 Backend

```
Backend/
├── src/main/java/com/tbd/control2tbd/
│   ├── Controllers/
│   │   ├── TareaController.java      # Endpoints de tareas
│   │   ├── UsuarioController.java    # Endpoints de usuarios
│   │   └── SectorController.java     # Endpoints de sectores
│   ├── Services/
│   │   ├── TareaService.java         # Logica de negocio tareas
│   │   └── UsuarioService.java       # Logica de negocio usuarios
│   ├── Repositories/
│   │   ├── TareaRepository.java      # Consultas de tareas
│   │   ├── UsuarioRepository.java    # Consultas de usuarios
│   │   └── SectorRepository.java     # Consultas de sectores
│   ├── Entities/
│   │   ├── Tarea.java                # Entidad tarea
│   │   ├── Usuario.java              # Entidad usuario
│   │   └── Sector.java               # Entidad sector
│   ├── DTOs/
│   │   ├── TareaDTO.java             # DTO para tareas
│   │   ├── UsuarioRegistroDTO.java   # DTO para registro
│   │   └── AuthResponse.java         # DTO para respuesta auth
│   └── Security/
│       ├── SecurityConfig.java       # Configuracion de seguridad
│       ├── JwtUtil.java              # Utilidades JWT
│       └── JwtRequestFilter.java     # Filtro de autenticacion
├── src/main/resources/
│   ├── application.properties        # Configuracion desarrollo
│   └── application-docker.properties # Configuracion Docker
├── Dockerfile
└── pom.xml
```

### 4.2 Frontend

```
Frontend/
├── src/
│   ├── views/
│   │   ├── HomeView.vue          # Pagina de inicio
│   │   ├── LoginView.vue         # Inicio de sesion
│   │   ├── RegisterView.vue      # Registro de usuarios
│   │   ├── TareasView.vue        # Gestion de tareas
│   │   ├── StatsView.vue         # Estadisticas geoespaciales
│   │   └── MapaView.vue          # Visualizacion de mapa
│   ├── components/
│   │   ├── Navbar.vue            # Barra de navegacion
│   │   └── NotificacionesTareas.vue  # Notificaciones
│   ├── services/
│   │   ├── api.js                # Configuracion Axios
│   │   ├── tareaService.js       # Servicios de tareas
│   │   ├── usuarioService.js     # Servicios de usuarios
│   │   └── sectorService.js      # Servicios de sectores
│   └── router/
│       └── index.js              # Configuracion de rutas
├── Dockerfile
├── nginx.conf
└── package.json
```

---

## 5. Modelo de Datos

### 5.1 Diagrama Entidad-Relacion

```
+-------------+       +-------------+       +-------------+
|   USUARIO   |       |    TAREA    |       |   SECTOR    |
+-------------+       +-------------+       +-------------+
| id (PK)     |       | id (PK)     |       | id (PK)     |
| username    |<------| usuario_id  |       | nombre      |
| password    |  1:N  | sector_id   |------>| ubicacion   |
| ubicacion   |       | titulo      |  N:1  | (POINT)     |
| (POINT)     |       | descripcion |       +-------------+
+-------------+       | estado      |
                      | fecha_venc  |
                      +-------------+
```

### 5.2 Tipos de Datos Geoespaciales

- **ubicacion (Usuario)**: GEOMETRY(POINT, 4326) - Posicion del usuario
- **ubicacion (Sector)**: GEOMETRY(POINT, 4326) - Centro del sector

El SRID 4326 corresponde al sistema de coordenadas WGS84 (latitud/longitud).

---

## 6. API REST - Endpoints

### 6.1 Autenticacion

| Metodo | Endpoint | Descripcion | Autenticacion |
|--------|----------|-------------|---------------|
| POST | /api/usuarios/registro | Registrar usuario | No |
| POST | /api/usuarios/login | Iniciar sesion | No |

### 6.2 Tareas

| Metodo | Endpoint | Descripcion | Autenticacion |
|--------|----------|-------------|---------------|
| GET | /api/tareas | Listar todas las tareas | Si |
| GET | /api/tareas/{id} | Obtener tarea por ID | Si |
| GET | /api/tareas/usuario | Tareas del usuario actual | Si |
| POST | /api/tareas | Crear nueva tarea | Si |
| PUT | /api/tareas/{id}/editar | Editar tarea | Si |
| PUT | /api/tareas/{id}/completar | Marcar como completada | Si |
| DELETE | /api/tareas/{id}/eliminar | Eliminar tarea | Si |

### 6.3 Consultas Geoespaciales

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| GET | /api/tareas/por-sector/{usuarioId} | Tareas por sector del usuario |
| GET | /api/tareas/cercana/{usuarioId} | Tarea mas cercana |
| GET | /api/tareas/sector-top/{usuarioId}?radioKm=X | Sector con mas tareas en radio |
| GET | /api/tareas/promedio-distancia/{usuarioId} | Promedio de distancia |
| GET | /api/tareas/tareas-pendientes/sectores | Concentracion de pendientes |
| GET | /api/tareas/cercanas/{usuarioId} | Tarea pendiente mas cercana |
| GET | /api/tareas/tareas-completadas/por-sector | Completadas por usuario/sector |
| GET | /api/tareas/promedio-distancia-global | Promedio distancia global |

### 6.4 Sectores

| Metodo | Endpoint | Descripcion | Autenticacion |
|--------|----------|-------------|---------------|
| GET | /api/sectores | Listar todos los sectores | Si |

---

## 7. Consultas Geoespaciales Implementadas

### 7.1 P1: Tareas por Sector del Usuario

Cuenta cuantas tareas tiene un usuario en cada sector.

```sql
SELECT s.nombre, COUNT(t.id) as cantidad
FROM tareas t
JOIN sectores s ON t.sector_id = s.id
WHERE t.usuario_id = :usuarioId
GROUP BY s.nombre
```

### 7.2 P2: Tarea Pendiente Mas Cercana

Encuentra la tarea pendiente mas cercana a la ubicacion del usuario.

```sql
SELECT t FROM Tarea t
JOIN t.sector s
WHERE t.estado = 'PENDIENTE'
ORDER BY ST_Distance(s.ubicacion, :ubicacionUsuario)
LIMIT 1
```

### 7.3 P3: Sector con Mas Tareas en Radio

Identifica el sector con mas tareas completadas dentro de un radio especifico.

```sql
SELECT s.nombre FROM sectores s
JOIN tareas t ON t.sector_id = s.id
WHERE t.estado = 'COMPLETADA'
AND ST_DWithin(
    CAST(s.ubicacion AS geography),
    CAST(:ubicacionUsuario AS geography),
    :radioMetros
)
GROUP BY s.id, s.nombre
ORDER BY COUNT(t.id) DESC
LIMIT 1
```

### 7.4 P4: Promedio de Distancia (Usuario)

Calcula la distancia promedio entre las tareas completadas de un usuario y su ubicacion.

```sql
SELECT AVG(ST_Distance(
    CAST(s.ubicacion AS geography),
    CAST(:ubicacionUsuario AS geography)
)) FROM tareas t
JOIN sectores s ON t.sector_id = s.id
WHERE t.estado = 'COMPLETADA' AND t.usuario_id = :usuarioId
```

### 7.5 P5: Concentracion de Tareas Pendientes

Muestra en que sectores se concentran las tareas pendientes.

```sql
SELECT s.nombre, COUNT(t.id) as pendientes
FROM tareas t
JOIN sectores s ON t.sector_id = s.id
WHERE t.estado = 'PENDIENTE'
GROUP BY s.nombre
ORDER BY pendientes DESC
```

### 7.6 P7: Tareas Completadas por Usuario y Sector

Cuenta las tareas completadas por cada usuario en cada sector.

```sql
SELECT u.username, s.nombre, COUNT(t.id) as completadas
FROM tareas t
JOIN sectores s ON t.sector_id = s.id
JOIN usuarios u ON t.usuario_id = u.id
WHERE t.estado = 'COMPLETADA'
GROUP BY u.username, s.nombre
```

### 7.7 P8: Sector Top en Radio Extendido (5km)

Similar a P3 pero con radio de 5 kilometros.

### 7.8 P9: Promedio de Distancia Global

Calcula el promedio de distancia entre todas las tareas completadas y la ubicacion de sus respectivos usuarios.

```sql
SELECT AVG(ST_Distance(s.ubicacion, u.ubicacion))
FROM tareas t
JOIN sectores s ON t.sector_id = s.id
JOIN usuarios u ON t.usuario_id = u.id
WHERE t.estado = 'COMPLETADA'
```

---

## 8. Seguridad

### 8.1 Autenticacion JWT

El sistema utiliza JSON Web Tokens para autenticacion:

1. Usuario envia credenciales a /api/usuarios/login
2. Backend valida credenciales y genera token JWT
3. Token se almacena en localStorage del navegador
4. Cada peticion incluye el token en header Authorization
5. JwtRequestFilter valida el token en cada peticion

Configuracion del token:
- Algoritmo: HS256
- Expiracion: 10 horas
- Header: Authorization: Bearer {token}

### 8.2 Hash de Contrasenas

Las contrasenas se almacenan hasheadas con BCrypt:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

- Registro: La contrasena se hashea antes de guardar
- Login: Se compara usando passwordEncoder.matches()

### 8.3 Validacion de Entrada

Se utiliza Bean Validation para validar datos de entrada:

```java
public class UsuarioRegistroDTO {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 6, max = 100)
    private String password;
}
```

### 8.4 Proteccion SQL Injection

La proteccion se implementa en dos capas:

1. **JPA/Hibernate**: Consultas parametrizadas automaticas
2. **Bean Validation**: Validacion de entrada con @Valid

---

## 9. Instalacion y Configuracion

### 9.1 Requisitos Previos

- Java 17 o superior
- Node.js 18 o superior
- PostgreSQL 15 con PostGIS 3.3
- Maven 3.9 o superior
- Docker y Docker Compose (opcional)

### 9.2 Configuracion de Base de Datos

1. Crear base de datos:
```sql
CREATE DATABASE control2tbd;
```

2. Habilitar PostGIS:
```sql
\c control2tbd
CREATE EXTENSION postgis;
```

3. Ejecutar script de datos de prueba:
```bash
psql -U postgres -d control2tbd -f datos_prueba.sql
```

### 9.3 Configuracion del Backend

Editar `Backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/control2tbd
spring.datasource.username=postgres
spring.datasource.password=tu_contrasena
```

Ejecutar:
```bash
cd Backend
mvn spring-boot:run
```

### 9.4 Configuracion del Frontend

```bash
cd Frontend
npm install
npm run dev
```

---

## 10. Despliegue con Docker

### 10.1 Archivos de Configuracion

- `docker-compose.yml`: Orquestacion de servicios
- `Backend/Dockerfile`: Imagen del backend
- `Frontend/Dockerfile`: Imagen del frontend
- `.env.example`: Variables de entorno

### 10.2 Comandos de Despliegue

```bash
# Construir imagenes
docker-compose build

# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down

# Detener y eliminar volumenes
docker-compose down -v
```

### 10.3 Servicios y Puertos

| Servicio | Puerto | Descripcion |
|----------|--------|-------------|
| frontend | 80 | Aplicacion web |
| backend | 8080 | API REST |
| postgres | 5432 | Base de datos |

### 10.4 Variables de Entorno

| Variable | Descripcion | Valor por defecto |
|----------|-------------|-------------------|
| DB_PASSWORD | Contrasena PostgreSQL | postgres |
| VITE_API_URL | URL del backend | http://localhost:8080 |

---

## 11. Usuarios de Prueba

| Usuario | Contrasena | Ubicacion |
|---------|------------|-----------|
| juan | password123 | -33.4380, -70.6450 |
| maria | password123 | -33.4300, -70.6100 |
| pedro | password123 | -33.4450, -70.6300 |
| ana | password123 | -33.4200, -70.5900 |

---

## 12. Funciones PostGIS Utilizadas

| Funcion | Descripcion |
|---------|-------------|
| ST_SetSRID | Asigna sistema de coordenadas |
| ST_MakePoint | Crea punto desde coordenadas |
| ST_Distance | Calcula distancia entre geometrias |
| ST_DWithin | Verifica si esta dentro de distancia |
| CAST AS geography | Convierte a tipo geography para distancias en metros |

---

## 13. Referencias

- Vue.js 3 Documentation: https://vuejs.org/
- Spring Boot Documentation: https://spring.io/projects/spring-boot
- PostGIS Documentation: https://postgis.net/documentation/
- Leaflet Documentation: https://leafletjs.com/reference.html
