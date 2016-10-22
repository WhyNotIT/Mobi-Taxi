package com.whynotit.MobiTaxi.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Harzallah on 16/04/2016.
 */
public class Localisation {

    @SerializedName("idtaxi")
    String idTaxi;
    @SerializedName("long")
    double longitude;
    @SerializedName("lat")
    double latitude;
    String date;


    public Localisation() {
    }

    public Localisation(String idtaxi, Double lat, Double aLong) {
        this.idTaxi = idtaxi;
        this.latitude = lat;
        this.longitude = aLong;
    }

    public String getIdTaxi() {
        return idTaxi;
    }

    public void setIdTaxi(String idTaxi) {
        this.idTaxi = idTaxi;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LatLng getLatLong ()
    {
        return new LatLng(latitude,longitude);
    }

    @Override
    public String toString() {
        return "Localisation{" +
                "idTaxi='" + idTaxi + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", date=" + date +
                '}';
    }
}
