package com.example.aismartdrive.DB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.aismartdrive.DB.sensor.AccelerometerDao;
import com.example.aismartdrive.DB.sensor.AccelerometerData;
import com.example.aismartdrive.DB.sensor.TemperatureDao;
import com.example.aismartdrive.DB.sensor.TemperatureData;
import com.example.aismartdrive.DB.vehicle.Vehicle;
import com.example.aismartdrive.DB.vehicle.VehicleDao;

@Database(entities = {Vehicle.class, AccelerometerData.class, TemperatureData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract VehicleDao vehicleDao();
    public abstract AccelerometerDao accelerometerDao();
    public abstract TemperatureDao temperatureDao();
}
