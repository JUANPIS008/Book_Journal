package com.example.back_end.controller;

import com.example.back_end.model.Usuario;
import com.example.back_end.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody Usuario usuario) {
        try {
            if (usuario.getCorreo() == null || usuario.getCorreo().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    crearRespuesta(false, "El correo es requerido", null)
                );
            }
            
            if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    crearRespuesta(false, "La contraseña es requerida", null)
                );
            }

            // Verificar si el usuario ya existe
            if (service.estaRegistrado(usuario.getCorreo())) {
                return ResponseEntity.badRequest().body(
                    crearRespuesta(false, "El correo ya está registrado", null)
                );
            }

            Usuario nuevoUsuario = service.registrar(usuario);
            return ResponseEntity.ok(crearRespuesta(true, "Registro exitoso", nuevoUsuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearRespuesta(false, "Error al registrar: " + e.getMessage(), null)
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioAutenticado = service.login(usuario.getCorreo(), usuario.getPassword());
            
            if (usuarioAutenticado != null) {
                return ResponseEntity.ok(crearRespuesta(true, "Login exitoso", usuarioAutenticado));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    crearRespuesta(false, "Credenciales incorrectas", null)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearRespuesta(false, "Error al iniciar sesión", null)
            );
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPerfil(@PathVariable Long id) {
        try {
            Usuario usuario = service.obtenerPorId(id);
            
            if (usuario != null) {
                return ResponseEntity.ok(crearRespuesta(true, "Usuario encontrado", usuario));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearRespuesta(false, "Error al obtener perfil", null)
            );
        }
    }

    @PostMapping("/verificar-registro")
    public ResponseEntity<Map<String, Object>> verificarRegistro(@RequestBody Map<String, String> request) {
        try {
            String correo = request.get("correo");
            
            if (correo == null || correo.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    crearRespuesta(false, "El correo es requerido", null)
                );
            }

            Usuario usuario = service.verificarRegistro(correo);
            
            if (usuario != null) {
                return ResponseEntity.ok(crearRespuesta(true, "Usuario registrado", usuario));
            } else {
                return ResponseEntity.ok(crearRespuesta(false, "Usuario no encontrado", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearRespuesta(false, "Error al verificar registro", null)
            );
        }
    }

    @GetMapping("/por-correo/{correo}")
    public ResponseEntity<Map<String, Object>> obtenerPorCorreo(@PathVariable String correo) {
        try {
            Usuario usuario = service.obtenerPorCorreo(correo);
            
            if (usuario != null) {
                return ResponseEntity.ok(crearRespuesta(true, "Usuario encontrado", usuario));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearRespuesta(false, "Error al obtener usuario", null)
            );
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> obtenerUserActual() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    crearRespuesta(false, "No autenticado", null)
                );
            }

            String correo = auth.getName();
            Usuario usuario = service.obtenerPorCorreo(correo);
            
            if (usuario != null) {
                return ResponseEntity.ok(crearRespuesta(true, "Usuario encontrado", usuario));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearRespuesta(false, "Error al obtener usuario actual", null)
            );
        }
    }

    // Método auxiliar para crear respuestas consistentes
    private Map<String, Object> crearRespuesta(boolean exito, String mensaje, Usuario usuario) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", exito);
        respuesta.put("mensaje", mensaje);
        respuesta.put("usuario", usuario);
        return respuesta;
    }
}
