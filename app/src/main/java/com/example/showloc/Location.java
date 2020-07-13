package com.example.showloc;

import com.google.firebase.firestore.GeoPoint;

public class Location {
    private String gravW,gravH,tempR,humidR,comment;
    private float markerColor;
    private GeoPoint geopoint;

    public Location(){}

    public String getGravW() {
        return gravW;
    }

    public void setGravW(String gravW) {
        this.gravW = gravW;
    }

    public String getGravH() {
        return gravH;
    }

    public void setGravH(String gravH) {
        this.gravH = gravH;
    }

    public String getTempR() {
        return tempR;
    }

    public void setTempR(String tempR) {
        this.tempR = tempR;
    }

    public String getHumidR() {
        return humidR;
    }

    public void setHumidR(String humidR) {
        this.humidR = humidR;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getMarkerColor() {
        return markerColor;
    }

    public void setMarkerColor(float markerColor) {
        this.markerColor = markerColor;
    }

    public GeoPoint getGeopoint() {
        return geopoint;
    }

    public void setGeopoint(GeoPoint geopoint) {
        this.geopoint = geopoint;
    }
}
