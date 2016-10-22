package com.whynotit.MobiTaxi.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Harzallah on 03/06/2016.
 */
public class Taxist {

    String id;
    @SerializedName("numero")
    String taxiNumero;
    @SerializedName("matricule")
    String taxiMatricule;
    String nom;
    String telephone;
    @SerializedName("nombreplaces")
    String nombrePlaces;
    String note;
    String marque;
    String type;
    int climatise;

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isClimatise() {
        return climatise == 1 ? true : false;
    }

    public void setClimatise(int climatise) {
        this.climatise = climatise;
    }

    public Taxist() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaxiNumero() {
        return taxiNumero;
    }

    public void setTaxiNumero(String taxiNumero) {
        this.taxiNumero = taxiNumero;
    }

    public String getTaxiMatricule() {
        return taxiMatricule;
    }

    public void setTaxiMatricule(String taxiMatricule) {
        this.taxiMatricule = taxiMatricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(String nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
