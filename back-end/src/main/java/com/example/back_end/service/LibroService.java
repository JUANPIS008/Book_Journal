package com.example.back_end.service;

import com.example.back_end.model.Libro;
import com.example.back_end.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {

    private final LibroRepository repository;

    public LibroService(LibroRepository repository) {
        this.repository = repository;
    }

    public List<Libro> listar() {
        return repository.findAll();
    }

    public Libro guardar(Libro libro) {
        return repository.save(libro);
    }

    public Libro actualizar(Long id, Libro libro) {
        libro.setId(id);
        return repository.save(libro);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public Libro obtener(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Libro> librosLeidos() {
        return repository.findByFinIsNotNull();
    }

    public List<Libro> buscar(String texto) {
        return repository.buscar(texto);
    }
    
}
