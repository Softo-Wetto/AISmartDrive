package com.example.aismartdrive.DB.sensor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AccelerometerDao {

    @Insert
    void insertAccelerometer(AccelerometerData accelerometerData);

    @Query("SELECT * FROM accelerometerData")
    LiveData<List<AccelerometerData>> getAllAccelerometerData();

    @Query("DELETE FROM accelerometerData")
    void deleteAllAccelerometerData();

}
