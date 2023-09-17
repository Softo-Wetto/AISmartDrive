package com.example.aismartdrive.DB;

import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = {Vehicle.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract VehicleDao vehicleDao();
}
