package com.tbd.control2tbd.Repositories;

import com.tbd.control2tbd.Entities.Tarea;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    // 1. Filtrar tareas por usuario
    List<Tarea> findByUsuarioId(Long usuarioId);

    List<Tarea> findByUsuarioUsername(String username);


    // --- CONSULTAS DEL ENUNCIADO ---

    // Pregunta: ¿Cuál es la tarea más cercana al usuario (que esté pendiente)?
    // Ordenamos por distancia y tomamos la primera (LIMIT 1)
    @Query(value = "SELECT t.* FROM tareas t " +
            "JOIN sectores s ON t.sector_id = s.id " +
            "WHERE t.estado = 'PENDIENTE' " +
            "ORDER BY ST_Distance(s.ubicacion, :ubicacionUsuario) ASC " +
            "LIMIT 1", nativeQuery = true)
    Tarea findTareaMasCercana(@Param("ubicacionUsuario") Point ubicacionUsuario);

    // Pregunta: ¿Cuántas tareas ha hecho el usuario por sector?
    // Devuelve una lista de arreglos [NombreSector, Cantidad]
    @Query(value = "SELECT s.nombre, COUNT(t.id) as cantidad " +
            "FROM tareas t " +
            "JOIN sectores s ON t.sector_id = s.id " +
            "WHERE t.usuario_id = :usuarioId " +
            "GROUP BY s.nombre", nativeQuery = true)
    List<Object[]> contarTareasPorSectorUsuario(@Param("usuarioId") Long usuarioId);

    // Pregunta: ¿Cuál es el sector con más tareas completadas en un radio X (2km o 5km)?
    // Usamos ST_DWithin para filtrar por radio y CAST AS geography para calcular en metros
    @Query(value = "SELECT s.nombre FROM sectores s " +
            "JOIN tareas t ON t.sector_id = s.id " +
            "WHERE t.estado = 'COMPLETADA' " +
            "AND ST_DWithin(CAST(s.ubicacion AS geography), CAST(:ubicacionUsuario AS geography), :radioMetros) " +
            "GROUP BY s.id, s.nombre " +
            "ORDER BY COUNT(t.id) DESC " +
            "LIMIT 1", nativeQuery = true)
    String findSectorConMasTareasEnRadio(@Param("ubicacionUsuario") Point ubicacionUsuario, @Param("radioMetros") double radioMetros);

    // Pregunta: ¿Promedio de distancia de tareas completadas?
    @Query(value = "SELECT AVG(ST_Distance(CAST(s.ubicacion AS geography), CAST(:ubicacionUsuario AS geography))) " +
            "FROM tareas t " +
            "JOIN sectores s ON t.sector_id = s.id " +
            "WHERE t.estado = 'COMPLETADA' AND t.usuario_id = :usuarioId", nativeQuery = true)
    Double calcularPromedioDistanciaTareas(@Param("ubicacionUsuario") Point ubicacionUsuario, @Param("usuarioId") Long usuarioId);

    //¿En qué sectores geográficos se concentran la mayoría de las tareas pendientes? (utilizando agrupación espacial).
    @Query("SELECT s.nombre AS sector, COUNT(t.id) AS cantidadTareasPendientes, " +
            "ST_AsText(s.ubicacion) AS ubicacionGeografica " +
            "FROM Tarea t " +
            "JOIN t.sector s " +
            "WHERE t.estado = 'PENDIENTE' " +
            "GROUP BY s.id, s.nombre, s.ubicacion " +
            "ORDER BY cantidadTareasPendientes DESC")
    List<Object[]> findTareasPendientesPorSector();

    //¿Cuál es la tarea pendiente más cercana a la ubicación del usuario?
    @Query("SELECT t FROM Tarea t " +
            "JOIN t.sector s " +
            "JOIN t.usuario u " +
            "WHERE t.estado = 'PENDIENTE' " +
            "AND ST_Distance(s.ubicacion, u.ubicacion) = " +
            "(SELECT MIN(ST_Distance(s2.ubicacion, u.ubicacion)) " +
            "FROM Tarea t2 JOIN t2.sector s2 WHERE t2.estado = 'PENDIENTE' AND t2.usuario.id = :usuarioId) " +
            "ORDER BY ST_Distance(s.ubicacion, u.ubicacion) ASC")
    Optional<Tarea> findTareaPendienteMasCercanaAUsuario(@Param("usuarioId") Long usuarioId);

    //¿Cuántas tareas ha realizado cada usuario por sector?
    @Query(value = "SELECT u.username, s.nombre, COUNT(t.id) as cantidad " +
            "FROM tareas t " +
            "JOIN sectores s ON t.sector_id = s.id " +
            "JOIN usuarios u ON t.usuario_id = u.id " +
            "WHERE t.estado = 'COMPLETADA' " +  // Filtra por tareas completadas
            "GROUP BY u.id, s.nombre", nativeQuery = true)
    List<Object[]> contarTareasCompletadasPorUsuarioYSector();

    //¿Cuál es el promedio de distancia entre las tareas completadas y el punto registrado del usuario?
    @Query("SELECT AVG(ST_Distance(s.ubicacion, u.ubicacion)) " +
            "FROM Tarea t " +
            "JOIN t.sector s " +
            "JOIN t.usuario u " +
            "WHERE t.estado = 'COMPLETADA'")
    Double calcularPromedioDistanciaTareasCompletadas();

}