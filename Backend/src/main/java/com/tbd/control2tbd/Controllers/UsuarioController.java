package com.tbd.control2tbd.Controllers;

import com.tbd.control2tbd.DTOs.AuthResponse;
import com.tbd.control2tbd.DTOs.UsuarioRegistroDTO;
import com.tbd.control2tbd.Entities.Usuario;
import com.tbd.control2tbd.Repositories.UsuarioRepository;
import com.tbd.control2tbd.Services.UsuarioService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody UsuarioRegistroDTO dto) {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(dto.getUsername());
        nuevoUsuario.setPassword(dto.getPassword());

        // Convertir lat/lon a Point
        Point punto = geometryFactory.createPoint(new Coordinate(dto.getLongitud(), dto.getLatitud()));
        nuevoUsuario.setUbicacion(punto);

        // Llamamos al servicio para registrar el usuario y generar el token
        String token = usuarioService.registrarUsuario(nuevoUsuario);

        // Obtener el usuario guardado para tener el ID
        Usuario usuarioGuardado = usuarioRepository.findByUsername(dto.getUsername());

        // Devolvemos el token con el ID
        return ResponseEntity.ok(new AuthResponse(usuarioGuardado.getId(), dto.getUsername(), token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioRegistroDTO dto) {
        // Llamamos al servicio para intentar autenticar y obtener el token
        String token = usuarioService.login(dto.getUsername(), dto.getPassword());

        if (token != null) {
            // Obtener el usuario para tener el ID
            Usuario usuario = usuarioRepository.findByUsername(dto.getUsername());
            // Si el login es exitoso, devolvemos el token con el ID
            return ResponseEntity.ok(new AuthResponse(usuario.getId(), dto.getUsername(), token));
        }
        // Si el login falla, devolvemos un error 401 (Unauthorized)
        return ResponseEntity.status(401).build();
    }

}