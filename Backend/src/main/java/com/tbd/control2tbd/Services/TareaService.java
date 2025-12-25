package com.tbd.control2tbd.Services;

import com.tbd.control2tbd.DTOs.ListTareasDTO;
import com.tbd.control2tbd.DTOs.TareaDTO;
import com.tbd.control2tbd.Entities.Sector;
import com.tbd.control2tbd.Entities.Tarea;
import com.tbd.control2tbd.Entities.Usuario;
import com.tbd.control2tbd.Repositories.SectorRepository;
import com.tbd.control2tbd.Repositories.TareaRepository;
import com.tbd.control2tbd.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SectorRepository sectorRepository;

    public Tarea crearTarea(TareaDTO tareaDTO) {
        // Obtener el nombre de usuario del contexto de seguridad
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Buscar el usuario en la base de datos con el nombre de usuario
        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario == null) {
            // Si el usuario no es encontrado, lanzamos una excepción
            throw new RuntimeException("Usuario no encontrado");
        }
        // Buscar el sector por el ID
        Sector sector = sectorRepository.findById(tareaDTO.getSectorId())
                .orElseThrow(() -> new RuntimeException("Sector no encontrado"));

        // Crear la tarea con los datos obtenidos
        Tarea tarea = new Tarea();
        tarea.setTitulo(tareaDTO.getTitulo());
        tarea.setDescripcion(tareaDTO.getDescripcion());
        tarea.setFechaVencimiento(tareaDTO.getFechaVencimiento());
        tarea.setSector(sector);  // Asignamos el sector encontrado
        tarea.setUsuario(usuario);  // Asignamos el usuario autenticado

        // Guardar la tarea en la base de datos
        return tareaRepository.save(tarea);
    }


    public List<Tarea> obtenerTodas() {
        return tareaRepository.findAll();
    }

    public List<Tarea> obtenerPorUsuario(Long usuarioId) {
        return tareaRepository.findByUsuarioId(usuarioId);
    }
    
    // Cambiar estado a COMPLETADA
    public Tarea completarTarea(Long id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        tarea.setEstado("COMPLETADA");
        return tareaRepository.save(tarea);
    }

    public Tarea editarTarea(Long id, TareaDTO tareaDTO) {
        // Buscar la tarea existente por ID
        Tarea tareaExistente = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // Obtener el sector usando el ID del DTO
        Sector sector = sectorRepository.findById(tareaDTO.getSectorId())
                .orElseThrow(() -> new RuntimeException("Sector no encontrado"));

        // Asignar los nuevos valores al objeto tarea
        tareaExistente.setTitulo(tareaDTO.getTitulo());
        tareaExistente.setDescripcion(tareaDTO.getDescripcion());
        tareaExistente.setFechaVencimiento(tareaDTO.getFechaVencimiento());
        tareaExistente.setSector(sector);

        // Guardar y devolver la tarea editada
        return tareaRepository.save(tareaExistente);
    }


    public void eliminarTarea(Long id) {
        Tarea tareaExistente = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        tareaRepository.delete(tareaExistente);
    }
    public List<ListTareasDTO> getTareasByUsuario(String username) {
        List<Tarea> tareas = tareaRepository.findByUsuarioUsername(username);

        return tareas.stream().map(tarea -> {
            ListTareasDTO tareaDTO = new ListTareasDTO();
            tareaDTO.setIdTarea(tarea.getId());
            tareaDTO.setTitulo(tarea.getTitulo());
            tareaDTO.setDescripcion(tarea.getDescripcion());
            tareaDTO.setFechaVencimiento(tarea.getFechaVencimiento());
            tareaDTO.setUsuarioId(tarea.getUsuario().getId());
            tareaDTO.setEstado(tarea.getEstado());
            tareaDTO.setSectorId(tarea.getSector() != null ? tarea.getSector().getId() : null);
            tareaDTO.setSectorNombre(tarea.getSector() != null ? tarea.getSector().getNombre() : null);
            return tareaDTO;
        }).collect(Collectors.toList());
    }

    public TareaDTO obtenerTareaPorId(Long id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        TareaDTO dto = new TareaDTO();
        dto.setTitulo(tarea.getTitulo());
        dto.setDescripcion(tarea.getDescripcion());
        dto.setFechaVencimiento(tarea.getFechaVencimiento());
        dto.setSectorId(tarea.getSector().getId());
        dto.setUsuarioId(tarea.getUsuario().getId());


        return dto;
    }

    // --- FUNCIONES PARA A LAS PREGUNTAS DEL ENUNCIADO ---

    // 1. ¿Cuál es la tarea más cercana al usuario (pendiente)?
    public Tarea obtenerTareaMasCercana(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario != null) {
            return tareaRepository.findTareaMasCercana(usuario.getUbicacion());
        }
        return null;
    }

    // 2. ¿Cuántas tareas ha realizado cada usuario por sector?
    public List<Object[]> contarTareasPorSector(Long usuarioId) {
        return tareaRepository.contarTareasPorSectorUsuario(usuarioId);
    }

    // 3. ¿Sector con más tareas completadas en radio X (2km o 5km)?
    // El frontend nos enviará el radio en Kilómetros (2 o 5), aquí lo pasamos a Metros.
    public String sectorConMasTareasEnRadio(Long usuarioId, double radioKm) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario != null) {
            double radioMetros = radioKm * 1000; // Convertir km a metros para PostGIS
            return tareaRepository.findSectorConMasTareasEnRadio(usuario.getUbicacion(), radioMetros);
        }
        return null;
    }

    // 5. ¿En qué sectores geográficos se concentran la mayoría de las tareas pendientes?
    public List<Object[]> obtenerTareasPendientesPorSector() {
        return tareaRepository.findTareasPendientesPorSector();
    }

    // 6. ¿Cuál es la tarea pendiente más cercana a la ubicación del usuario?
    public Optional<Tarea> obtenerTareaPendienteMasCercanaAUsuario(Long usuarioId) {
        return tareaRepository.findTareaPendienteMasCercanaAUsuario(usuarioId);
    }

    // 7. ¿Cuántas tareas ha realizado cada usuario por sector?
    public List<Object[]> contarTareasCompletadasPorUsuarioYSector() {
        return tareaRepository.contarTareasCompletadasPorUsuarioYSector();
    }

    // 9. Promedio de distancia de tareas completadas (por usuario)
    public Double promedioDistancia(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario != null) {
            return tareaRepository.calcularPromedioDistanciaTareas(usuario.getUbicacion(), usuarioId);
        }
        return 0.0;
    }

    // 10. Promedio de distancia global entre tareas completadas y punto del usuario
    public Double promedioDistanciaGlobal() {
        return tareaRepository.calcularPromedioDistanciaTareasCompletadas();
    }

}