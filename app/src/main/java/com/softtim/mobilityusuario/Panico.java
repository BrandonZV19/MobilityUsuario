package com.softtim.mobilityusuario;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Anahi on 23/05/2017.
 */
public class Panico extends RealmObject implements Serializable {

    @PrimaryKey
    private int IDalerta;

    private String fecha;
    private String nombre;
    private double latitud;
    private double longitud;

    public int getIDalerta() {
        return IDalerta;
    }

    public void setIDalerta(int IDalerta) {
        this.IDalerta = IDalerta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


}
