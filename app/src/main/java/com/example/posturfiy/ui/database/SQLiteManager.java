package com.example.posturfiy.ui.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteManager extends SQLiteOpenHelper {

    private static SQLiteManager sqLiteManagers;

    private static final String DATABASE_NAME = "PosturifyDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Place";
    private static final String COUNTER = "COUNTER";

    private static final String ID_FIELD = "id";
    private static final String NAME_FIELD = "name";
    private static final String COORDS_FILED = "coordinates";

    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteManager instanceOfDatabase(Context context) {
        if (sqLiteManagers == null) {
            sqLiteManagers = new SQLiteManager(context);
        }
        return sqLiteManagers;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder sql;
        sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append("(")
                .append(COUNTER)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(ID_FIELD)
                .append(" INT, ")
                .append(NAME_FIELD)
                .append(" TEXT, ")
                .append(COORDS_FILED)
                .append(" TEXT)");
        sqLiteDatabase.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addPlaceToDatabase(Place place) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, place.getId());
        contentValues.put(NAME_FIELD, place.getName());
        contentValues.put(COORDS_FILED, place.getCoordinates());

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void populateRecordListArray() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null)) {
            if (result.getCount() != 0) {
                while (result.moveToNext()) {
                    int id = result.getInt(1);
                    String name = result.getString(2);
                    String coords = result.getString(3);
                    Place place = new Place(id, name, coords);
                    Place.arrayList.add(place);
                }
            }
        }
    }

    public void updatePlaceInDB(Place place) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, place.getId());
        contentValues.put(NAME_FIELD, place.getName());
        contentValues.put(COORDS_FILED, place.getCoordinates());

        sqLiteDatabase.update(TABLE_NAME, contentValues, ID_FIELD + " ?= ", new String[]{String.valueOf(place.getId())});
    }
}
