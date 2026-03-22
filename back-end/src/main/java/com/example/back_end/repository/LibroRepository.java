package com.example.back_end.repository;

import com.example.back_end.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByFinIsNotNull();
    List<Libro> findByFinIsNull();
    List<Libro> findByFinIsNotNullOrderByFinDesc();

    @Query("SELECT l FROM Libro l WHERE " +
            "LOWER(l.titulo) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(l.autor) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(l.genero) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Libro> buscar(@Param("texto") String texto);
}
