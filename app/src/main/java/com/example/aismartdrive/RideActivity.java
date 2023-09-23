package com.example.aismartdrive;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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

import java.util.Timer;
import java.util.TimerTask;

public class RideActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView sourceTextView, destinationTextView, distanceTextView, timeTextView, priceTextView;
    private MapView mapView;
    private GoogleMap googleMap;
    private double currentDistance = 0.0;
    private double currentTime = 0;
    private double currentPrice = 0.0;
    private boolean rideFinished = false;
    private Intent serviceIntent;
    private SensorDataReceiver dataReceiver;
    private Toast tooFastToast;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        //Accelerometer Sensor
        manageSensorServices();

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
        String sourceAddress = intent.getStringExtra("sourceAddress");
        String destinationAddress = intent.getStringExtra("destinationAddress");
        LatLng sourceLatLng = intent.getParcelableExtra("sourceLatLng");
        LatLng destinationLatLng = intent.getParcelableExtra("destinationLatLng");

        // Display source and destination addresses
        sourceTextView.setText("Source: " + sourceAddress);
        destinationTextView.setText("Destination: " + destinationAddress);

        Button finishButton = findViewById(R.id.finishButton);
        Button emergencyButton = findViewById(R.id.emergencyButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stop the timer when the "Finish Ride" button is clicked
                rideFinished = true;

                // Go to PaymentActivity with data
                goToPaymentActivity();
            }
        });

        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Stop the timer when the "Emergency" button is clicked
                rideFinished = true;

                // Go to EmergencyActivity with data
                goToEmergencyActivity();
            }
        });

        startLiveDataUpdates();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Enable zoom controls UI
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Add markers for source and destination
        LatLng sourceLatLng = getIntent().getParcelableExtra("sourceLatLng");
        LatLng destinationLatLng = getIntent().getParcelableExtra("destinationLatLng");

        if (sourceLatLng != null && destinationLatLng != null) {
            googleMap.addMarker(new MarkerOptions().position(sourceLatLng).title("Source"));
            googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));

            // Create a PolylineOptions object to define the car path
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

    private void startLiveDataUpdates() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!rideFinished) { // Check if the ride is finished
                    // Simulate live data updates
                    currentDistance += 0.1393; // Increment distance by 0.1 km (adjust as needed)
                    currentTime += 0.1; // Increment time by 1 minute (adjust as needed)
                    currentPrice += 0.389; // Increment price by $1.5 (adjust as needed)

                    // Update the TextViews with live data
                    runOnUiThread(new Runnable() {
                        @SuppressLint({"SetTextI18n", "DefaultLocale"})
                        @Override
                        public void run() {
                            distanceTextView.setText("Distance: " + String.format("%.1f", currentDistance) + " km");
                            timeTextView.setText("Time: " + String.format("%.0f", currentTime) + " mins");
                            priceTextView.setText("Price: $" + String.format("%.2f", currentPrice));
                        }
                    });
                }
            }
        }, 0, 2000); // Update every 2 seconds (adjust as needed)
    }

    private void goToPaymentActivity() {
        // Stop the timer when the ride is finished
        rideFinished = true;
        // Prepare data to send to PaymentActivity
        String vehicleName = getIntent().getStringExtra("vehicleName");

        Intent paymentIntent = new Intent(this, PaymentActivity.class);

        paymentIntent.putExtra("sourceAddress", sourceTextView.getText().toString().replace("Source: ", ""));
        paymentIntent.putExtra("destinationAddress", destinationTextView.getText().toString().replace("Destination: ", ""));
        paymentIntent.putExtra("distance", currentDistance);
        paymentIntent.putExtra("time", currentTime);
        paymentIntent.putExtra("price", currentPrice);
        paymentIntent.putExtra("vehicleName", vehicleName);

        startActivity(paymentIntent);
    }

    private void goToEmergencyActivity() {
        // Prepare data to send to EmergencyActivity
        Intent emergencyIntent = new Intent(this, EmergencyActivity.class);

        String vehicleName = getIntent().getStringExtra("vehicleName");

        emergencyIntent.putExtra("sourceAddress", sourceTextView.getText().toString().replace("Source: ", ""));
        emergencyIntent.putExtra("destinationAddress", destinationTextView.getText().toString().replace("Destination: ", ""));
        emergencyIntent.putExtra("distance", currentDistance);
        emergencyIntent.putExtra("time", currentTime);
        emergencyIntent.putExtra("price", currentPrice);

        // Also pass the sourceLatLng and destinationLatLng
        LatLng sourceLatLng = getIntent().getParcelableExtra("sourceLatLng");
        LatLng destinationLatLng = getIntent().getParcelableExtra("destinationLatLng");
        emergencyIntent.putExtra("sourceLatLng", sourceLatLng);
        emergencyIntent.putExtra("destinationLatLng", destinationLatLng);
        emergencyIntent.putExtra("vehicleName", vehicleName);

        startActivity(emergencyIntent);
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

