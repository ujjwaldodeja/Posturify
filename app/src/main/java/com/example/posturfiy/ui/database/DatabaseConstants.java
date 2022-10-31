package com.example.posturfiy.ui.database;

public class DatabaseConstants {

    public static final String DATABASE_NAME = "PosturifyDB";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME_PLACE = "Place";
    public static final String TABLE_NAME_RECORD = "Record";

    public static final String ID_FIELD_PLACE = "pid";
    public static final String NAME_FIELD_PLACE = "name";
    public static final String LATITUDE_FIELD_PLACE = "latitude";
    public static final String LONGITUDE_FIELD_PLACE = "longitude";

    public static final String ID_FIELD_RECORD = "rid";
    public static final String FOREIGN_ID_FIELD_RECORD = "foreign_pid";
    public static final String RESULT_FIELD_RECORD = "result";
    public static final String TIMESTAMP_FIELD_RECORD = "timestamp";

    public static final String CREATE_PLACE = "CREATE TABLE "
            + TABLE_NAME_PLACE + "("
            + ID_FIELD_PLACE + " INTEGER PRIMARY KEY AUTOINCREMENT,  "
            + NAME_FIELD_PLACE + " TEXT, "
            + LATITUDE_FIELD_PLACE + " TEXT, "
            + LONGITUDE_FIELD_PLACE + " TEXT)";

    public static final String CREATE_RECORD = "CREATE TABLE "
            + TABLE_NAME_RECORD + "("
            + ID_FIELD_RECORD + " INTEGER PRIMARY KEY AUTOINCREMENT,  "
            + FOREIGN_ID_FIELD_RECORD + " INT, "
            + RESULT_FIELD_RECORD + " TEXT, "
            + TIMESTAMP_FIELD_RECORD + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY(" + FOREIGN_ID_FIELD_RECORD + ") REFERENCES "
            + TABLE_NAME_PLACE + "(" + ID_FIELD_PLACE + "))";

    public static final String DELETE_PLACE = "DELETE FROM "
            + TABLE_NAME_PLACE + " WHERE pid = ";


}
