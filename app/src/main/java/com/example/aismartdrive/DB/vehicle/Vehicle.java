package com.example.aismartdrive.DB.vehicle;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vehicles")
public class Vehicle {

    @PrimaryKey(autoGenerate = true)
    public int id;
    private String name;
    private String type;
    private String vehicleNumber;
    private String source;
    private String destination;
    private String currentLocation;
    private String fuelStatus;

    public Vehicle(String name, String type, String vehicleNumber, String source, String destination,
                   String currentLocation, String fuelStatus) {
        this.name = name;
        this.type = type;
        this.vehicleNumber = vehicleNumber;
        this.source = source;
        this.destination = destination;
        this.currentLocation = currentLocation;
        this.fuelStatus = fuelStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public String getFuelStatus() {
        return fuelStatus;
    }

    public void setFuelStatus(String fuelStatus) {
        this.fuelStatus = fuelStatus;
    }
}
