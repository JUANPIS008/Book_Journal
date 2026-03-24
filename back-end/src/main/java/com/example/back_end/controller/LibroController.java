package com.example.back_end.controller;

import com.example.back_end.model.Libro;
import com.example.back_end.service.LibroService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
@CrossOrigin(origins = "*")
public class LibroController {

    private final LibroService service;

    public LibroController(LibroService service) {
        this.service = service;
    }

    @GetMapping
    public List<Libro> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Libro obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public Libro guardar(@RequestBody Libro libro) {
        return service.guardar(libro);
    }

    @PutMapping("/{id}")
    public Libro actualizar(@PathVariable Long id, @RequestBody Libro libro) {
        return service.actualizar(id, libro);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping("/leidos")
    public List<Libro> librosLeidos() {
        return service.librosLeidos();
    }

    @GetMapping("/buscar")
    public List<Libro> buscar(@RequestParam String texto) {
        return service.buscar(texto);
    }
}