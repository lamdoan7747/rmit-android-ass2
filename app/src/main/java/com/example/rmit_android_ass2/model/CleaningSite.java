package com.example.rmit_android_ass2.model;

public class CleaningSite {
    private String name;
    private String address;
    private String lat;
    private String lon;
    private String owner;

    public CleaningSite() {
    }

    public CleaningSite(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public CleaningSite(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
