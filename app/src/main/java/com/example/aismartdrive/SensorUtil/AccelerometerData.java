package com.example.aismartdrive.SensorUtil;

import java.io.Serializable;

public class AccelerometerData implements Serializable {
    Long timeStamp;
    float acceXaxis;
    float acceYaxis;
    float acceZaxis;
    double magnitude;
    public AccelerometerData(Long timeStamp, float acceXaxis, float acceYaxis, float acceZaxis, double magnitude) {
        this.timeStamp = timeStamp;
        this.acceXaxis = acceXaxis;
        this.acceYaxis = acceYaxis;
        this.acceZaxis = acceZaxis;
        this.magnitude = magnitude;
    }
    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public float getAcceXaxis() {
        return acceXaxis;
    }

    public void setAcceXaxis(float acceXaxis) {
        this.acceXaxis = acceXaxis;
    }

    public float getAcceYaxis() {
        return acceYaxis;
    }

    public void setAcceYaxis(float acceYaxis) {
        this.acceYaxis = acceYaxis;
    }

    public float getAcceZaxis() {
        return acceZaxis;
    }

    public void setAcceZaxis(float acceZaxis) {
        this.acceZaxis = acceZaxis;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }
}
