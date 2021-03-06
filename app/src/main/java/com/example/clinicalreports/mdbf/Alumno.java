package com.example.clinicalreports.mdbf;

import java.util.HashMap;

public class Alumno {

    private String nombre;
    private String uuid;
    private String noCtrl;
    private HashMap<String, Reporte> reportes;
    private String correo;
    private String password;
    private String profesor;

    public Alumno(){

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNoCtrl() {
        return noCtrl;
    }

    public void setNoCtrl(String noCtrl) {
        this.noCtrl = noCtrl;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    public String toString(){
        return nombre + " No Ctrl: " + noCtrl;
    }

    public HashMap<String, Reporte> getReportes() {
        return reportes;
    }

    public void setReportes(HashMap<String, Reporte> reportes) {
        this.reportes = reportes;
    }
}
