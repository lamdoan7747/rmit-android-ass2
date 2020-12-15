package com.example.rmit_android_ass2.model;

public class CleaningSite {
    private String name;
    private String address;
    private Double lat;
    private Double lng;
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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
