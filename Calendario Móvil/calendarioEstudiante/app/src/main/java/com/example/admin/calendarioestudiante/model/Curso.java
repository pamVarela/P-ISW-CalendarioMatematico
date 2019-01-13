package com.example.admin.calendarioestudiante.model;

public class Curso {

    private String codigo;
    private String nombre;
    private int periodo;
    private int notifica;
    private int anno;


    public Curso() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public int getNotifica() {
        return notifica;
    }

    public void setNotifica(int notifica) {
        this.notifica = notifica;
    }

    @Override
    public String toString() {
        return codigo + "\n" + nombre + "\n Periodo:" + periodo + "-" + anno;
    }
}
