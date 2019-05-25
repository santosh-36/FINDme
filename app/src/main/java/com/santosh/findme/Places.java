package com.santosh.findme;

public class Places {
    String address;
    Double latitude;
    Double longitude;
    String title;
    String uid;
    String dist;

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public Places(){}

    public Places(String address, Double latitude, Double longitude, String title, String uid, String dist) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.uid = uid;
        this.dist = dist;
    }
}
