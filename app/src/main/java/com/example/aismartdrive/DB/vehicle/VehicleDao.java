package com.example.aismartdrive.DB.vehicle;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.aismartdrive.DB.vehicle.Vehicle;

import java.util.List;
@Dao
public interface VehicleDao {
    @Insert
    void insert(Vehicle vehicle);
    @Update
    void update(Vehicle vehicle);
    @Delete
    void delete(Vehicle vehicle);
    @Query("SELECT * FROM vehicles")
    LiveData<List<Vehicle>> getAllVehicles();
    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    LiveData<Vehicle> getVehicleById(int vehicleId);
}