package com.example.saveloc;

public class Location {
    String longi,lati,gravW,gravH,tempR,humidR,locDesc,comment;
    float markerColor;

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

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

    public String getLocDesc() {
        return locDesc;
    }

    public void setLocDesc(String locDesc) {
        this.locDesc = locDesc;
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
}
