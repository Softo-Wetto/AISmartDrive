package com.example.aismartdrive.SensorUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Arrays;

public class SensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor tempSensor = null; // Setting the sensor as null at the beginning.
    private Sensor acceleroSensor = null; // Setting the sensor as null at the beginning.
    private static final float TEMP_THRESHOLD = 40; // in degree celsius

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }else {
            Toast.makeText(this, "No Temperature sensor found", Toast.LENGTH_SHORT).show();
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            acceleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }else {
            Toast.makeText(this, "No Temperature sensor found", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (tempSensor != null) {
            sensorManager.registerListener((SensorEventListener) this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (tempSensor != null) {
            sensorManager.registerListener(this, acceleroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
            float tempData = sensorEvent.values[0];
            Log.d("Sensor data ", "Ambient temperature is: "+tempData+" C");
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            double magnitude = Math.sqrt(x * x + y * y + z * z);
            Log.d("Sensor data ", "Acceleration towards X, Y, and Z "+ Arrays.toString(sensorEvent.values) +" and magnitude: "+magnitude);

            AccelerometerData accelerometerData = new AccelerometerData(sensorEvent.timestamp, x, y, z, magnitude);
            Intent broadcastIntent = new Intent("VEHICLE_SENSOR_DATA");
            broadcastIntent.putExtra("accelerometerData", accelerometerData);
            sendBroadcast(broadcastIntent);
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
