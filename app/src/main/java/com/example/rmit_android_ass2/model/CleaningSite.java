package com.example.rmit_android_ass2.model;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.List;

public class CleaningSite {
    @DocumentId
    private String _id;
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private String owner;
    private ArrayList<User> followers;

    public CleaningSite() {
    }

    public CleaningSite(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public ArrayList<User> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<User> followers) {
        this.followers = followers;
    }
}
