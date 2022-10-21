package com.example.posturfiy.ui.database.record;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Record {

    public static final List<Record> recordsList = new ArrayList<>();

    private int id;
    private int foreign_id;
    private String result;
    private Timestamp timestamp;

    public Record(int id, int foreign_id, String result, Timestamp timestamp) {
        this.id = id;
        this.foreign_id = foreign_id;
        this.result = result;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getForeign_id() {
        return foreign_id;
    }

    public void setForeign_id(int foreign_id) {
        this.foreign_id = foreign_id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
