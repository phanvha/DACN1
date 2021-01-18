package com.utt.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class Place {
    private int id;
    private String name;
    private String address;
    private LatLng latLng;

    public Place(int id, String name, String address, LatLng latLng) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latLng = latLng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
