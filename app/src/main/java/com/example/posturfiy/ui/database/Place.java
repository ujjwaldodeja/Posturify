package com.example.posturfiy.ui.database;

import java.util.ArrayList;
import java.util.Date;

public class Place {

    public static ArrayList<Place> arrayList = new ArrayList<>();
    public static final String PLACE_EDIT_EXTRA = "placeEdit";

    private int id;
    private String name;
    private String coordinates;

    private Date deleted;

    public Place(int id, String name, String coordinates, Date deleted) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.deleted = deleted;
    }

    public Place(int id, String name, String coordinates) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        deleted = null;
    }

    public static Place getPlaceForId(int passedPlaceId) {
        for (Place place : arrayList) {
            if (place.id == passedPlaceId) {
                return place;
            }
        }
        return null;
    }

    public static ArrayList<Place> nonDeletedPlaces() {
        ArrayList<Place> nonDeleted = new ArrayList<>();

        for (Place p : arrayList) {
            if (p.getDeleted() == null) {
                nonDeleted.add(p);
            }
        }
        return nonDeleted;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
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

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
