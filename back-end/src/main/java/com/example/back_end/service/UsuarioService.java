package com.example.back_end.service;

import com.example.back_end.model.Usuario;
import com.example.back_end.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario registrar(Usuario usuario) {
        return repository.save(usuario);
    }

    public Usuario login(String correo, String password) {
        Optional<Usuario> user = repository.findByCorreo(correo);

        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user.get();
        }

        return null;
    }

    public Usuario obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Usuario actualizar(Usuario usuario) {
        return repository.save(usuario);
    }
}