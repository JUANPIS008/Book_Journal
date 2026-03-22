package com.example.back_end.repository;

import com.example.back_end.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByFinIsNotNull();
    List<Libro> findByFinIsNull(); 
    List<Libro> findByFinIsNotNullOrderByFinDesc();
    List<Libro> findByUsuarioId(Long usuarioId);
}
