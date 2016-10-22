package com.whynotit.MobiTaxi.Models;

/**
 * Created by Harzallah on 17/04/2016.
 */
public class Distance {

    String idTaxi;
    double distanceToMe;

    public Distance() {
    }

    public Distance(String idTaxi, double distance) {
        this.idTaxi = idTaxi;
        this.distanceToMe = distance;
    }

    public String getIdTaxi() {
        return idTaxi;
    }

    public void setIdTaxi(String idTaxi) {
        this.idTaxi = idTaxi;
    }

    public double getDistanceToMe() {
        return distanceToMe;
    }

    public void setDistanceToMe(double distanceToMe) {
        this.distanceToMe = distanceToMe;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "idTaxi='" + idTaxi + '\'' +
                ", distanceToMe=" + distanceToMe +
                '}';
    }
}
