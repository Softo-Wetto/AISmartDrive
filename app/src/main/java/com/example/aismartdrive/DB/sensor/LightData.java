package com.example.aismartdrive.DB.sensor;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "lightData")
public class LightData implements Serializable {
    @PrimaryKey
    long TimeStamp;

    float light;

    public LightData(float light, Long TimeStamp ){
        this.light = light;
        this.TimeStamp = TimeStamp;
    }
    public float getLight() {
        return light;
    }

    public void setLight(float light) {
        this.light = light;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long Timestamp) {
        this.TimeStamp = Timestamp;
    }
}
