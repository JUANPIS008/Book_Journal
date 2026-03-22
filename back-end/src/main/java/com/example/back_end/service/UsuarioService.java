package com.example.back_end.service;

import com.example.back_end.model.Usuario;
import com.example.back_end.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void encodeExistingPasswords() {
        List<Usuario> usuarios = repository.findAll();
        for (Usuario usuario : usuarios) {
            if (usuario.getPassword() != null && !usuario.getPassword().startsWith("$2a$")) {
                // Password is not BCrypt encoded, encode it
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
                repository.save(usuario);
            }
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = obtenerPorCorreo(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        return usuario;
    }

    public Usuario registrar(Usuario usuario) {
        // Encriptar contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return repository.save(usuario);
    }

    public Usuario login(String correo, String password) {
        Optional<Usuario> user = repository.findByCorreo(correo);

        // Validar contraseña encriptada con BCrypt
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user.get();
        }

        return null;
    }

    public Usuario obtenerPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Usuario obtenerPorCorreo(String correo) {
        Optional<Usuario> user = repository.findByCorreo(correo);
        return user.orElse(null);
    }

    public Usuario verificarRegistro(String correo) {
        return obtenerPorCorreo(correo);
    }

    public boolean estaRegistrado(String correo) {
        return repository.findByCorreo(correo).isPresent();
    }
}
