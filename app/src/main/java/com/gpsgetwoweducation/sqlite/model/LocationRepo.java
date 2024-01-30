package com.gpsgetwoweducation.sqlite.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gpsgetwoweducation.sqlite.data.DatabaseManager;


public class LocationRepo {
    LocationModel customerModel;

    public LocationRepo() {
        customerModel = new LocationModel();
    }

    public static String createTable() {
        return "CREATE TABLE " + LocationModel.TABLE + "("
                + LocationModel.KEY_country_code + " TEXT ,"
                + LocationModel.KEY_customer_id + " TEXT ,"
                + LocationModel.KEY_user_ide + " TEXT ,"
                + LocationModel.KEY_latest_datetime + " TEXT ,"
                + LocationModel.KEY_latitude + " TEXT ,"
                + LocationModel.KEY_longitude + " TEXT )";
    }

    public int insert(LocationModel product) {
        int courseId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(LocationModel.KEY_country_code, product.getCountry_code());
        values.put(LocationModel.KEY_customer_id, product.getCustomer_id());
        values.put(LocationModel.KEY_user_ide, product.getUser_id());
        values.put(LocationModel.KEY_latest_datetime, product.getLatest_datetime());
        values.put(LocationModel.KEY_latitude, product.getLatitude());
        values.put(LocationModel.KEY_longitude, product.getLongitude());

        // Inserting Row
        courseId = (int) db.insert(LocationModel.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return courseId;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor res = db.rawQuery("select * from " + LocationModel.TABLE, null);
        return res;
    }

    public void delete() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.execSQL("delete from " + LocationModel.TABLE);
    }
}
