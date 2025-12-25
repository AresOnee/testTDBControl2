package com.tbd.control2tbd.Controllers;

import com.tbd.control2tbd.DTOs.ListTareasDTO;
import com.tbd.control2tbd.DTOs.TareaDTO;
import com.tbd.control2tbd.Entities.Tarea;
import com.tbd.control2tbd.Entities.Usuario;
import com.tbd.control2tbd.Services.TareaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tareas")
@CrossOrigin("*")
public class TareaController {

    @Autowired
    private TareaService tareaService;

    // Crear Tarea
    @PostMapping
    public ResponseEntity<Tarea> crearTarea(@RequestBody TareaDTO tareaDTO) {
        try {
            Tarea tarea = tareaService.crearTarea(tareaDTO); // Llamada al servicio
            return ResponseEntity.status(HttpStatus.CREATED).body(tarea);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Listar todas (para el home)
    @GetMapping
    public List<Tarea> listarTodas() {
        return tareaService.obtenerTodas();
    }
    
    // Marcar como completada
    @PutMapping("/{id}/completar")
    public ResponseEntity<Tarea> completar(@PathVariable Long id) {
        return ResponseEntity.ok(tareaService.completarTarea(id));
    }

    @PutMapping("/{id}/editar")
    public ResponseEntity<Tarea> editarTarea(@PathVariable Long id, @RequestBody TareaDTO tareaDTO) {
        try {
            // Llamamos al servicio para editar la tarea
            Tarea tareaActualizada = tareaService.editarTarea(id, tareaDTO);
            return ResponseEntity.ok(tareaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}/eliminar")
    public ResponseEntity<Void> eliminarTarea(@PathVariable Long id) {
        tareaService.eliminarTarea(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener tarea por ID
    @GetMapping("/{id}")
    public ResponseEntity<TareaDTO> obtenerTarea(@PathVariable Long id) {
        TareaDTO dto = tareaService.obtenerTareaPorId(id);
        return ResponseEntity.ok(dto);
    }

    // --- ENDPOINTS PARA LAS PREGUNTAS ---

    // 1. Tarea más cercana pendiente
    @GetMapping("/cercana/{usuarioId}")
    public ResponseEntity<Tarea> tareaMasCercana(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(tareaService.obtenerTareaMasCercana(usuarioId));
    }

    // 2. Tareas por sector (estadística)
    @GetMapping("/por-sector/{usuarioId}")
    public List<Object[]> tareasPorSector(@PathVariable Long usuarioId) {
        return tareaService.contarTareasPorSector(usuarioId);
    }

    // 3. Sector top en radio (ej: /api/tareas/sector-top/1?radioKm=5)
    @GetMapping("/sector-top/{usuarioId}")
    public ResponseEntity<String> sectorTopRadio(@PathVariable Long usuarioId, @RequestParam double radioKm) {
        String nombreSector = tareaService.sectorConMasTareasEnRadio(usuarioId, radioKm);
        return ResponseEntity.ok(nombreSector != null ? nombreSector : "No encontrado");
    }

    // 4. Promedio distancia
    @GetMapping("/promedio-distancia/{usuarioId}")
    public ResponseEntity<Double> promedioDistancia(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(tareaService.promedioDistancia(usuarioId));
    }

    // 5. ¿En qué sectores geográficos se concentran la mayoría de las tareas pendientes?
    @GetMapping("/tareas-pendientes/sectores")
    public List<Object[]> getTareasPendientesPorSector() {
        return tareaService.obtenerTareasPendientesPorSector();
    }

    // 6. ¿Cuál es la tarea pendiente más cercana a la ubicación del usuario?
    @GetMapping("/cercanas/{usuarioId}")
    public Optional<Tarea> getTareaPendienteMasCercanaAUsuario(@PathVariable Long usuarioId) {
        return tareaService.obtenerTareaPendienteMasCercanaAUsuario(usuarioId);
    }

    // 7. ¿Cuántas tareas ha realizado cada usuario por sector?
    @GetMapping("/tareas-completadas/por-sector")
    public List<Object[]> getTareasCompletadasPorUsuarioYSector() {
        return tareaService.contarTareasCompletadasPorUsuarioYSector();
    }

    @GetMapping("/usuario")
    public List<ListTareasDTO> getTareasByUsuario(Authentication authentication) {
        String username = authentication.getName();
        return tareaService.getTareasByUsuario(username);
    }
}