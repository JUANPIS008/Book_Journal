package com.example.back_end.controller;

import com.example.back_end.model.Deseo;
import com.example.back_end.service.DeseoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deseos")
@CrossOrigin(origins = "*")
public class DeseoController {

    private final DeseoService service;

    public DeseoController(DeseoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Deseo> listar() {
        return service.listar();
    }

    @PostMapping
    public Deseo guardar(@RequestBody Deseo deseo) {
        return service.guardar(deseo);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}