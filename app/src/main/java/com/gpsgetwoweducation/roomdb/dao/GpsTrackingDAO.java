package com.gpsgetwoweducation.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.gpsgetwoweducation.roomdb.entity.GpsTrackingData;

import java.util.List;

@Dao
public interface GpsTrackingDAO {
    @Insert
    void insertGpsTracking(GpsTrackingData gpsTrackingData);
    @Delete
    void delete(GpsTrackingData data);
    @Query("SELECT * FROM tbl_gps_tracking")
    List<GpsTrackingData> getAllData();

}
