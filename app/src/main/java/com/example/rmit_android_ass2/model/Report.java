package com.example.rmit_android_ass2.model;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class Report implements Serializable {
    @DocumentId
    private String id;
    private long follower;
    private String name;
    private double amount;

    public Report() {
    }

    public Report(String name, long follower, double amount) {
        this.follower = follower;
        this.name = name;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getFollower() {
        return follower;
    }

    public void setFollower(long follower) {
        this.follower = follower;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
