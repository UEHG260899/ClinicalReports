package com.example.clinicalreports.mdbf;

public class Reporte {

    private String nomDu;
    private String telefono;
    private String nomAn;
    private String especie;
    private String fecha;
    private String diagnostico;
    private String tratamiento;
    private String latitud;
    private String longitud;
    private String uuid;
    private String imagen;


    public String getNomDu() {
        return nomDu;
    }

    public void setNomDu(String nomDu) {
        this.nomDu = nomDu;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNomAn() {
        return nomAn;
    }

    public void setNomAn(String nomAn) {
        this.nomAn = nomAn;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String toString(){
        return "Reporte del animal: " + nomAn;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
