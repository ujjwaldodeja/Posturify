package com.example.posturfiy.ui.database.place;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Place {

    public static ArrayList<Place> arrayList = new ArrayList<>();
    public static final String PLACE_EDIT_EXTRA = "placeEdit";

    private int id;
    private String name;
    private String latitude;
    private String longitude;

    public Place(int id, String name, String latitude, String longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Place getPlaceForId(int passedPlaceId) {
        for (Place place : arrayList) {
            if (place.id == passedPlaceId) {
                return place;
            }
        }
        return null;
    }

    public static boolean ifHasDuplicates(int id) {
        for (Place p : arrayList) {
            if (p.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getPlacesNames() {
        List<String> list = new ArrayList<>();
        for (Place p : arrayList) {
            list.add(p.getName());
        }
        return list;
    }

    public static void removeById(int id) {
        arrayList.removeIf(p -> p.getId() == id);
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
