package com.gpsgetwoweducation.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.gpsgetwoweducation.roomdb.entity.GpsResponseData;
import com.gpsgetwoweducation.roomdb.entity.GpsTrackingData;

import java.util.List;

@Dao
public interface GpsResponseDAO {
    @Insert
    void insertGpsResponseData(GpsResponseData gpsResponseData);

    @Query("SELECT * FROM tbl_gps_response_data")
    List<GpsResponseData> getAllData();

    @Query("DELETE FROM tbl_gps_response_data")
    void deleteAllData();

    @Query("SELECT * FROM tbl_gps_response_data ORDER BY id DESC LIMIT 1")
    GpsResponseData getLatestCoordinate();

}
