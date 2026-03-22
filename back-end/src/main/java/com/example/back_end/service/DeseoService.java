package com.example.back_end.service;

import com.example.back_end.model.Deseo;
import com.example.back_end.repository.DeseoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeseoService {

    private final DeseoRepository repository;

    public DeseoService(DeseoRepository repository) {
        this.repository = repository;
    }

    public List<Deseo> listar() {
        return repository.findAll();
    }

    public Deseo guardar(Deseo deseo) {
        return repository.save(deseo);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}