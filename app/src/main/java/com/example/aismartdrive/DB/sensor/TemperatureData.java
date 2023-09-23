package com.example.aismartdrive.DB.sensor;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "temperatureData")
public class TemperatureData {

    @PrimaryKey
    long timeStamp;
    float temp;

    public TemperatureData(float temp, long timeStamp) {
        this.temp = temp;
        this.timeStamp = timeStamp;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
