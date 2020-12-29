package com.example.rmit_android_ass2.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CleaningResult implements Serializable {
    @DocumentId
    private String id;

    @ServerTimestamp
    private Timestamp dateCleaning;

    private double amount;

    public CleaningResult() {
    }

    public CleaningResult(double amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getDateCleaning() {
        return dateCleaning;
    }

    public void setDateCleaning(Timestamp dateCleaning) {
        this.dateCleaning = dateCleaning;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
