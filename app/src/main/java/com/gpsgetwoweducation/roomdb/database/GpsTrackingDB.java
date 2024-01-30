package com.gpsgetwoweducation.roomdb.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.gpsgetwoweducation.roomdb.dao.GpsResponseDAO;
import com.gpsgetwoweducation.roomdb.dao.GpsTrackingDAO;
import com.gpsgetwoweducation.roomdb.entity.GpsResponseData;
import com.gpsgetwoweducation.roomdb.entity.GpsTrackingData;

@Database(entities = {GpsTrackingData.class, GpsResponseData.class}, version = 2, exportSchema = false)
public abstract class GpsTrackingDB extends RoomDatabase {
    public static final String DATABASE_NAME = "gps_tracking_db";

    public abstract GpsTrackingDAO getGpsTrackingDAO();

    public abstract GpsResponseDAO getGpsResponseDAO();

    private static GpsTrackingDB mInstance;

    public static GpsTrackingDB getInstance(Context context) {
        if (mInstance == null) {
            synchronized (GpsTrackingDB.class) {
                mInstance = Room.databaseBuilder(context, GpsTrackingDB.class, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return mInstance;
    }
}
