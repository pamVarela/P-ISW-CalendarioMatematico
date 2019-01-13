package com.example.admin.calendarioestudiante.model;

public class Tema {

    private String nombre;

    public Tema() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return  nombre ;
    }
}
