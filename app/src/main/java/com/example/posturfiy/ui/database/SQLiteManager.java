package com.example.posturfiy.ui.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.posturfiy.ui.database.place.Place;
import com.example.posturfiy.ui.database.record.Record;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLiteManager extends SQLiteOpenHelper {

    private static SQLiteManager sqLiteManagers;

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    public SQLiteManager(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
    }

    public static SQLiteManager instanceOfDatabase(Context context) {
        if (sqLiteManagers == null) {
            sqLiteManagers = new SQLiteManager(context);
        }
        return sqLiteManagers;
    }

    public void dropTableIfExists() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '" + DatabaseConstants.CREATE_PLACE + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + DatabaseConstants.CREATE_RECORD + "'");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(DatabaseConstants.CREATE_PLACE);
        sqLiteDatabase.execSQL(DatabaseConstants.CREATE_RECORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        dropTableIfExists();
    }

    public void addPlaceToDatabase(Place place) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.ID_FIELD_PLACE, place.getId());
        contentValues.put(DatabaseConstants.NAME_FIELD_PLACE, place.getName());
        contentValues.put(DatabaseConstants.LATITUDE_FIELD_PLACE, place.getLatitude());
        contentValues.put(DatabaseConstants.LONGITUDE_FIELD_PLACE, place.getLongitude());
        contentValues.put(DatabaseConstants.DELETED_FIELD_PLACE, getStringFromDate(place.getDeleted()));

        sqLiteDatabase.insert(DatabaseConstants.TABLE_NAME_PLACE, null, contentValues);
    }

    public void addRecordToDatabase(Record record) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.ID_FIELD_RECORD, record.getId());
        contentValues.put(DatabaseConstants.FOREIGN_ID_FIELD_RECORD, record.getForeign_id());
        contentValues.put(DatabaseConstants.RESULT_FIELD_RECORD, record.getResult());
        contentValues.put(DatabaseConstants.TIMESTAMP_FIELD_RECORD, record.getTimestamp().toString());

        sqLiteDatabase.insert(DatabaseConstants.TABLE_NAME_RECORD, null, contentValues);
    }

    private String getStringFromDate(Date date) {
        if (date == null) {
            return null;
        }
        return dateFormat.format(date);
    }

    private Date getDateFromString(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void populateRecordListArray() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseConstants.TABLE_NAME_RECORD, null)) {
            if (result.getCount() != 0) {
                while (result.moveToNext()) {
                    int id = result.getInt(0);
                    int foreign = result.getInt(1);
                    String res = result.getString(2);
                    String timestampStr = result.getString(3);
                    Timestamp timestamp = new Timestamp(getDateFromString(timestampStr).getTime());
                    Record newRecord = new Record(id, foreign, res, timestamp);
                    Record.recordsList.add(newRecord);
                }
            }
        }
    }

    public void populatePlaceListArray() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + DatabaseConstants.TABLE_NAME_PLACE, null)) {
            if (result.getCount() != 0) {
                while (result.moveToNext()) {
                    int id = result.getInt(0);
                    String name = result.getString(1);
                    String latitude = result.getString(2);
                    String longitude = result.getString(3);
                    String deletedString = result.getString(4);
                    Date date = getDateFromString(deletedString);
                    Place place = new Place(id, name, latitude, longitude, date);
                    Place.arrayList.add(place);
                }
            }
        }
    }

    public void updatePlaceInDB(Place place) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.ID_FIELD_PLACE, place.getId());
        contentValues.put(DatabaseConstants.NAME_FIELD_PLACE, place.getName());
        contentValues.put(DatabaseConstants.LATITUDE_FIELD_PLACE, place.getLatitude());
        contentValues.put(DatabaseConstants.LONGITUDE_FIELD_PLACE, place.getLongitude());
        contentValues.put(DatabaseConstants.DELETED_FIELD_PLACE, getStringFromDate(place.getDeleted()));

        sqLiteDatabase.update(DatabaseConstants.TABLE_NAME_PLACE, contentValues, DatabaseConstants.ID_FIELD_PLACE + " =? ", new String[]{String.valueOf(place.getId())});
    }
}
