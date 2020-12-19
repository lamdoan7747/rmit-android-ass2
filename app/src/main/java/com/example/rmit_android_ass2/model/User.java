package com.example.rmit_android_ass2.model;

public class User{
    private String _id;
    private String fname;
    private String email;
    private String mobile;
    private String address;

    public User() {
    }

    public User(String fname) {
        this.fname = fname;
    }

    public User(String _id, String fname, String email) {
        this._id = _id;
        this.fname = fname;
        this.email = email;
    }

    public User(String fname, String email) {
        this.fname = fname;
        this.email = email;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
