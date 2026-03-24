package com.example.back_end.model;

import jakarta.persistence.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String correo;
    private String password;

    private String fechaNacimiento;
    private String generoFavorito;
    private String promedioLectura;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }      

    public String getFechaNacimiento() { return fechaNacimiento; }  
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getGeneroFavorito() { return generoFavorito; }
    public void setGeneroFavorito(String generoFavorito) { this.generoFavorito = generoFavorito; }  

    public String getPromedioLectura() { return promedioLectura; }
    public void setPromedioLectura(String promedioLectura) { this.promedioLectura = promedioLectura; }

}