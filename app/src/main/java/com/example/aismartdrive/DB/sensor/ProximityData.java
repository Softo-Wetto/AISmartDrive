package com.example.aismartdrive.DB.sensor;


import java.io.Serializable;

public class ProximityData implements Serializable {


    long Timestamp;

    float distance;

    public ProximityData (float distance, long Timestamp){
        this.distance = distance;
        this.Timestamp = Timestamp;
    }
    public float getDistance() {
        return distance;
    }
    public void setDistance(float distance) {
        this.distance = distance;
    }

    public long getTimeStamp() {
        return Timestamp;
    }

    public void setTimeStamp(long TimeStamp) {
        this.Timestamp = TimeStamp;
    }
}
