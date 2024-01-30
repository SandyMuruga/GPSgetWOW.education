package com.gpsgetwoweducation.sqlite.data;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gpsgetwoweducation.sqlite.App;
import com.gpsgetwoweducation.sqlite.model.LocationModel;
import com.gpsgetwoweducation.sqlite.model.LocationRepo;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "gps-location.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper() {
        super(App.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LocationRepo.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LocationModel.TABLE);
    }

}
