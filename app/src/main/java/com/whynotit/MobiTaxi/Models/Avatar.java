package com.whynotit.MobiTaxi.Models;

import android.graphics.Color;

/**
 * Created by Harzallah on 28/04/2016.
 */
public class Avatar {

    private int id;
    private String nom;
    private String url;
    private String couleur;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return '{' +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", url='" + url + '\'' +
                ", couleur='" + couleur + '\'' +
                '}';
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public int getCouleurInt ()
    {
        return Color.parseColor(couleur);
    }
}
