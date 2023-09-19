package com.example.aismartdrive.DB.sensor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TemperatureDao {

    @Insert
    void insertTemperature(TemperatureData temperatureData);

    @Query("SELECT * FROM temperatureData")
    LiveData<List<TemperatureData>> getAllTemperatureData();

    @Query("DELETE FROM temperatureData")
    void deleteAllTemperatureData();

}
