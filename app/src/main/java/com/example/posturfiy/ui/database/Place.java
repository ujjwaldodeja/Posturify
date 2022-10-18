package com.example.posturfiy.ui.database;

import java.util.ArrayList;
import java.util.Date;

public class Place {

    public static ArrayList<Place> arrayList = new ArrayList<>();
    public static final String PLACE_EDIT_EXTRA = "placeEdit";

    private int id;
    private String name;

    public Place(int id, String name, String coordinates) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
    }

    private String coordinates;

    public static Place getPlaceForId(int passedPlaceId) {
        for (Place place : arrayList) {
            if (place.id == passedPlaceId) {
                return place;
            }
        }
        return null;
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
