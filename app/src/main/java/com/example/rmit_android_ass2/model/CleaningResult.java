package com.example.rmit_android_ass2.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.util.Date;

public class CleaningResult implements Serializable {
    @DocumentId
    private int id;
    private FieldValue dateCleaning;
    private Double amount;

    public CleaningResult() {
    }

    public CleaningResult(FieldValue dateCleaning, Double amount) {
        this.dateCleaning = dateCleaning;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FieldValue getDateCleaning() {
        return dateCleaning;
    }

    public void setDateCleaning(FieldValue dateCleaning) {
        this.dateCleaning = dateCleaning;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
