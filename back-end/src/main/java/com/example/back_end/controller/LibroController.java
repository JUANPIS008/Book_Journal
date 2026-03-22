package com.example.back_end.controller;

import com.example.back_end.model.Libro;
import com.example.back_end.model.Usuario;
import com.example.back_end.service.LibroService;
import com.example.back_end.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/libros")
@CrossOrigin(origins = "*") // IMPORTANTE para tu frontend
public class LibroController {

    private final LibroService service;
    private final UsuarioService usuarioService;

    public LibroController(LibroService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    /**
     * Obtener el usuario autenticado actual
     */
    private Usuario obtenerUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        String correo = auth.getName();
        return usuarioService.obtenerPorCorreo(correo);
    }

    @GetMapping
    public ResponseEntity<List<Libro>> listar() {
        try {
            List<Libro> libros = service.listar();
            return ResponseEntity.ok(libros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtener(@PathVariable Long id) {
        try {
            Libro libro = service.obtener(id);
            if (libro != null) {
                return ResponseEntity.ok(libro);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> guardar(@RequestBody Libro libro) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    crearRespuestaError("Debe autenticarse para crear libros")
                );
            }

            // Asociar el libro al usuario autenticado
            libro.setUsuario(usuario);
            Libro libroGuardado = service.guardar(libro);
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exito", true);
            respuesta.put("mensaje", "Libro guardado correctamente");
            respuesta.put("libro", libroGuardado);
            
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearRespuestaError("Error al guardar el libro: " + e.getMessage())
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @RequestBody Libro libro) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    crearRespuestaError("Debe autenticarse para actualizar libros")
                );
            }

            Libro libroExistente = service.obtener(id);
            
            if (libroExistente == null) {
                return ResponseEntity.notFound().build();
            }

            // Verificar que el usuario es propietario del libro
            if (!libroExistente.getUsuario().getId().equals(usuario.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    crearRespuestaError("No tienes permiso para actualizar este libro")
                );
            }

            // Mantener la asociación con el usuario original
            libro.setUsuario(usuario);
            Libro libroActualizado = service.actualizar(id, libro);
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exito", true);
            respuesta.put("mensaje", "Libro actualizado correctamente");
            respuesta.put("libro", libroActualizado);
            
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearRespuestaError("Error al actualizar el libro: " + e.getMessage())
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    crearRespuestaError("Debe autenticarse para eliminar libros")
                );
            }

            Libro libroExistente = service.obtener(id);
            
            if (libroExistente == null) {
                return ResponseEntity.notFound().build();
            }

            // Verificar que el usuario es propietario del libro
            if (!libroExistente.getUsuario().getId().equals(usuario.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    crearRespuestaError("No tienes permiso para eliminar este libro")
                );
            }

            service.eliminar(id);
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exito", true);
            respuesta.put("mensaje", "Libro eliminado correctamente");
            
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearRespuestaError("Error al eliminar el libro: " + e.getMessage())
            );
        }
    }

    @GetMapping("/leidos")
    public ResponseEntity<List<Libro>> librosLeidos() {
        try {
            List<Libro> libros = service.librosLeidos();
            return ResponseEntity.ok(libros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/mis-libros")
    public ResponseEntity<Map<String, Object>> misLibros() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    crearRespuestaError("Debe autenticarse para ver sus libros")
                );
            }

            List<Libro> libros = service.obtenerLibrosPorUsuario(usuario.getId());
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exito", true);
            respuesta.put("total", libros.size());
            respuesta.put("libros", libros);
            respuesta.put("usuario", usuario);
            
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearRespuestaError("Error al obtener libros: " + e.getMessage())
            );
        }
    }

    /**
     * Endpoint alternativo para obtener libros del usuario por parámetro
     * @param usuarioId ID del usuario
     * @return Lista de libros del usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Map<String, Object>> librosPorUsuario(@PathVariable Long usuarioId) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    crearRespuestaError("Debe autenticarse")
                );
            }

            // El usuario solo puede ver sus propios libros
            if (!usuario.getId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    crearRespuestaError("No tienes permiso para ver los libros de otro usuario")
                );
            }

            List<Libro> libros = service.obtenerLibrosPorUsuario(usuarioId);
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("exito", true);
            respuesta.put("total", libros.size());
            respuesta.put("libros", libros);
            
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearRespuestaError("Error al obtener libros: " + e.getMessage())
            );
        }
    }

    // Método auxiliar para crear respuestas de error
    private Map<String, Object> crearRespuestaError(String mensaje) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exito", false);
        respuesta.put("mensaje", mensaje);
        return respuesta;
    }
}