package com.example.aismartdrive;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aismartdrive.DB.sensor.AccelerometerData;
import com.example.aismartdrive.SensorUtil.SensorService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class EmergencyActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {
    private TextView sourceTextView, destinationTextView, distanceTextView, timeTextView, priceTextView;
    private MapView mapView;
    private GoogleMap googleMap;
    private String sourceAddress, destinationAddress;
    private double distance, time, price;
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private TextView movementTextView, compassTextView;
    private boolean isMovingForward = false;
    private boolean isMovingBackward = false;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private Intent serviceIntent;
    private SensorDataReceiver dataReceiver;
    private Toast tooFastToast;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        //Accelerometer Sensor
        manageSensorServices();
        //Gyroscope Sensor:
        movementTextView = findViewById(R.id.movementTextView);
        compassTextView = findViewById(R.id.compassTextView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscopeSensor != null) {
            sensorManager.registerListener((SensorEventListener) this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        sourceTextView = findViewById(R.id.sourceTextView);
        destinationTextView = findViewById(R.id.destinationTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        timeTextView = findViewById(R.id.timeTextView);
        priceTextView = findViewById(R.id.priceTextView);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Retrieve extras from the Intent
        Intent intent = getIntent();
        sourceAddress = intent.getStringExtra("sourceAddress");
        destinationAddress = intent.getStringExtra("destinationAddress");
        distance = intent.getDoubleExtra("distance", 0.0);
        time = intent.getDoubleExtra("time", 0.0);
        price = intent.getDoubleExtra("price", 0.0);

        // Display source and destination addresses
        sourceTextView.setText("Source: " + sourceAddress);
        destinationTextView.setText("Destination: " + destinationAddress);
        distanceTextView.setText("Distance: " + String.format("%.1f", distance) + " km");
        timeTextView.setText("Time: " + String.format("%.0f", time) + " mins");
        priceTextView.setText("Price: $" + String.format("%.2f", price));

        Button finishRideButton = findViewById(R.id.finishRideButton);
        finishRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass the information to PaymentActivity
                goToPaymentActivity();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Enable zoom controls UI
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Retrieve and add markers for source and destination
        Intent intent = getIntent();
        LatLng sourceLatLng = intent.getParcelableExtra("sourceLatLng");
        LatLng destinationLatLng = intent.getParcelableExtra("destinationLatLng");

        if (sourceLatLng != null && destinationLatLng != null) {
            googleMap.addMarker(new MarkerOptions().position(sourceLatLng).title("Source"));
            googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));

            // Create a PolylineOptions object to define the path
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(sourceLatLng)  // Starting point
                    .add(destinationLatLng)  // Destination point
                    .color(Color.BLUE)  // Color of the path
                    .width(5);  // Width of the path

            // Add the Polyline to the map
            googleMap.addPolyline(polylineOptions);

            // Move camera to fit both markers and the path
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(sourceLatLng);
            builder.include(destinationLatLng);
            LatLngBounds bounds = builder.build();
            int padding = 100;  // Padding in pixels
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.animateCamera(cameraUpdate);
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void manageSensorServices() {
        // Start the FallDetectionService (Existing code)
        serviceIntent = new Intent(this, SensorService.class);
        startService(serviceIntent);

        // Register the BroadcastReceiver (New Code)
        dataReceiver = new SensorDataReceiver();
        IntentFilter filter = new IntentFilter("VEHICLE_SENSOR_DATA");
        registerReceiver(dataReceiver, filter);
    }
    private class SensorDataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("VEHICLE_SENSOR_DATA")) {
                AccelerometerData accelerometerData = (AccelerometerData) intent.getSerializableExtra("accelerometerData");
                if (accelerometerData != null) {
                    double magnitude = accelerometerData.getMagnitude();
                    if (magnitude < 9.81) {
                        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.white, null));
                        hideTooFastViews();
                    } else {
                        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.red, null));
                        showTooFastViews();
                        displayTooFastToast();
                    }
                }
            }
        }
    }
    private void showTooFastViews() {
        // Show the "Vehicle Moving Too Fast" TextView
        TextView tooFastTextView = findViewById(R.id.tooFastTextView);
        if (tooFastTextView != null) {
            tooFastTextView.setVisibility(View.VISIBLE);
        }
    }

    private void hideTooFastViews() {
        // Hide the "Vehicle Moving Too Fast" TextView
        TextView tooFastTextView = findViewById(R.id.tooFastTextView);
        if (tooFastTextView != null) {
            tooFastTextView.setVisibility(View.GONE);
        }

        // Cancel the displayed toast if it's currently shown
        if (tooFastToast != null) {
            tooFastToast.cancel();
        }
    }

    private void displayTooFastToast() {
        // Display a toast message indicating that the vehicle is moving too fast
        if (tooFastToast != null) {
            tooFastToast.cancel();
        }
        tooFastToast = Toast.makeText(this, "Vehicle is moving too fast", Toast.LENGTH_SHORT);
        tooFastToast.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Extract the x and y rotation values
            float xRotation = sensorEvent.values[0];
            float yRotation = sensorEvent.values[1];

            // Threshold values for considering movement
            float threshold = 0.5f;

            // Check x-rotation for forward/backward movement
            if (xRotation > threshold) {
                isMovingForward = true;
                isMovingBackward = false;
                movementTextView.setText("Vehicle is moving backward");
            } else if (xRotation < -threshold) {
                isMovingForward = false;
                isMovingBackward = true;
                movementTextView.setText("Vehicle is moving forward");
            } else {
                isMovingForward = false;
                isMovingBackward = false;
            }

            // Check y-rotation for left/right movement
            if (yRotation > threshold) {
                isMovingLeft = true;
                isMovingRight = false;
                movementTextView.setText("Vehicle is moving right");
            } else if (yRotation < -threshold) {
                isMovingLeft = false;
                isMovingRight = true;
                movementTextView.setText("Vehicle is moving left");
            } else {
                isMovingLeft = false;
                isMovingRight = false;
            }

            // Determine the compass direction based on x and y rotation
            String compassDirection = "";

            if (isMovingForward && !isMovingBackward) {
                compassDirection += "S"; // South
            } else if (!isMovingForward && isMovingBackward) {
                compassDirection += "N"; // North
            }

            if (isMovingLeft && !isMovingRight) {
                compassDirection += "E"; // East
            } else if (!isMovingLeft && isMovingRight) {
                compassDirection += "W"; // West
            }

            // Display the compass direction
            compassTextView.setText("Direction: " + compassDirection);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Not used in this example
    }

    private void goToPaymentActivity() {
        String vehicleName = getIntent().getStringExtra("vehicleName");
        // Prepare data to send to PaymentActivity
        Intent paymentIntent = new Intent(this, PaymentActivity.class);

        // Pass the source, destination, distance, time, and price to PaymentActivity
        paymentIntent.putExtra("sourceAddress", sourceAddress);
        paymentIntent.putExtra("destinationAddress", destinationAddress);
        paymentIntent.putExtra("distance", distance);
        paymentIntent.putExtra("time", time);
        paymentIntent.putExtra("price", price);
        paymentIntent.putExtra("vehicleName", vehicleName);

        startActivity(paymentIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        unregisterReceiver(dataReceiver);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
