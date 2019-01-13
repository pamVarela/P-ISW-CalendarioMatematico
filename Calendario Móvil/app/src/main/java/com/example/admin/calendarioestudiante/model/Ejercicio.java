package com.example.admin.calendarioestudiante.model;

public class Ejercicio {

    private String nombre;
    private String problema;
    private String planteamiento;
    private String solucion;
    private String fecha;

    public Ejercicio() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public String getPlanteamiento() {
        return planteamiento;
    }

    public void setPlanteamiento(String planteamiento) {
        this.planteamiento = planteamiento;
    }

    public String getSolucion() {
        return solucion;
    }

    public void setSolucion(String solucion) {
        this.solucion = solucion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return  nombre + '\'' + problema + '\'' + planteamiento + '\'' + solucion + '\'' + fecha ;
    }
}
