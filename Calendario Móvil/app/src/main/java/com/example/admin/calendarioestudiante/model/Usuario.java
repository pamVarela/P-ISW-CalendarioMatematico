package com.example.admin.calendarioestudiante.model;

import java.util.ArrayList;

public class Usuario {

    private String correo;
    private String nombre;
    private ArrayList<Curso> cursos;
    private int notifica;
    private  int tipo;

    public Usuario() { }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(ArrayList<Curso> cursos) {
        this.cursos = cursos;
    }


    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getNotifica() {
        return notifica;
    }

    public void setNotifica(int notifica) {
        this.notifica = notifica;
    }

    @Override
    public String toString() {

        return correo + '\'' + nombre + '\'' + cursos + '\'' + tipo;
    }
}


