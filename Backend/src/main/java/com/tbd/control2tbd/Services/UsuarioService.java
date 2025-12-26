package com.tbd.control2tbd.Services;

import com.tbd.control2tbd.Entities.Usuario;
import com.tbd.control2tbd.Repositories.UsuarioRepository;
import com.tbd.control2tbd.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registrar un nuevo usuario y generar un token JWT
    public String registrarUsuario(Usuario usuario) {
        // Hashear la contraseña antes de guardar
        String hashedPassword = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(hashedPassword);

        Usuario usuarioRegistrado = usuarioRepository.save(usuario);

        // Generar el token JWT para el usuario registrado
        return jwtUtil.generarToken(usuarioRegistrado.getUsername());
    }

    // Login simple y generación de token JWT
    public String login(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
            // Generar y devolver el token JWT si las credenciales son correctas
            return jwtUtil.generarToken(usuario.getUsername());
        }
        return null; // Si las credenciales no son correctas, retornamos null
    }

    // Buscar usuario por ID
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
}
