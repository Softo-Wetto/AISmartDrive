package com.example.aismartdrive.SensorUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.aismartdrive.DB.sensor.AccelerometerDao;
import com.example.aismartdrive.DB.sensor.AccelerometerData;
import com.example.aismartdrive.DB.sensor.LightData;
import com.example.aismartdrive.DB.sensor.ProximityData;
import com.example.aismartdrive.DB.sensor.TemperatureDao;
import com.example.aismartdrive.DB.sensor.TemperatureData;
import com.example.aismartdrive.Utils.MyApp;

import java.util.Arrays;

public class SensorService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor tempSensor = null;
    private Sensor acceSensor = null;
    private Sensor proxSensor = null;

    private Sensor ligSensor = null;

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }else {
            Toast.makeText(this, "Device does not have Temperature Sensor", Toast.LENGTH_SHORT).show();
        }

       if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            acceSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        else {
            Toast.makeText(this, "Device does not have Acceleration Sensor", Toast.LENGTH_SHORT).show();
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
            proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
        else {
            Toast.makeText(this, "Device does not have a Proximity Sensor", Toast.LENGTH_SHORT).show();
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            ligSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
        else {
            Toast.makeText(this, "Device does not have a light sensor", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, acceSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, ligSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (sensorManager!=null){
            sensorManager.unregisterListener(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
            float temp = sensorEvent.values[0];
            Log.d("Sensor data ", "Ambient temperature is: "+temp+" C");

            TemperatureData temperatureData = new TemperatureData(temp, sensorEvent.timestamp);

            // Save data into database
            TemperatureDao temperatureDao = MyApp.getAppDatabase().temperatureDao();
            AsyncTask.execute(()->{
                temperatureDao.insertTemperature(temperatureData);
            });
        }


        if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY){
            float distance = sensorEvent.values[0];
            Log.d("Sensor Data", "Proximity distance is: "+distance+"cm");

            ProximityData proximityData = new ProximityData(distance, sensorEvent.timestamp);

            Intent broadcastIntent = new Intent("VEHICLE_SENSOR_DATA");
            broadcastIntent.putExtra("proximityData", proximityData);
            sendBroadcast(broadcastIntent);
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT){
            float light = sensorEvent.values[0];
            Log.d("Sensor Data", "Light was recorded at: "+light);

            LightData lightData = new LightData(light, sensorEvent.timestamp);

            Intent broadcastIntent = new Intent("VEHICLE_SENSOR_DATA");
            broadcastIntent.putExtra("lightData", lightData);
            sendBroadcast(broadcastIntent);
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            double magnitude = Math.sqrt(x * x + y * y + z * z);
            //Log.d("Sensor data ", "Acceleration towards X, Y, and Z "+
                    //Arrays.toString(sensorEvent.values) +" and magnitude: "+magnitude);


            AccelerometerData accelerometerData = new
                    AccelerometerData(sensorEvent.timestamp, x, y, z, magnitude);

            // Save data into database
            AccelerometerDao accelerometerDao = MyApp.getAppDatabase().accelerometerDao();

            AsyncTask.execute(()->{
                accelerometerDao.insertAccelerometer(accelerometerData);
            });

            // Send data to the activity
            Intent broadcastIntent = new Intent("VEHICLE_SENSOR_DATA");
            broadcastIntent.putExtra("accelerometerData", accelerometerData);
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
