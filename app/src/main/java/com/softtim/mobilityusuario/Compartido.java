package com.softtim.mobilityusuario;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by softtim on 7/27/17.
 */

public class Compartido extends RealmObject implements Serializable {

    @PrimaryKey
    private  int idServ;

    private  int status;
    private  String userName;
    private  String placas;
    private  String marca;
    private  String modelo;
    private  String color;
    private  String año;
    private  String fotoUnidad;

    private  double lat,lng;

    public int getSatus() {
        return status;
    }

    public void setSatus(int status) {
        this.status = status;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getIdServ() {
        return idServ;
    }

    public void setIdServ(int idServ) {
        this.idServ = idServ;
    }

    public String getPlacas() {
        return placas;
    }

    public void setPlacas(String placas) {
        this.placas = placas;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAño() {
        return año;
    }

    public void setAño(String año) {
        this.año = año;
    }

    public String getFotoUnidad() {
        return fotoUnidad;
    }

    public void setFotoUnidad(String fotoUnidad) {
        this.fotoUnidad = fotoUnidad;
    }

}
