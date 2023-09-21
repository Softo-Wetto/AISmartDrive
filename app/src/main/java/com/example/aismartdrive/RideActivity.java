package com.example.aismartdrive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

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

        // You can also use sourceLatLng and destinationLatLng as needed
        startLiveDataUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add markers for source and destination
        LatLng sourceLatLng = getIntent().getParcelableExtra("sourceLatLng");
        LatLng destinationLatLng = getIntent().getParcelableExtra("destinationLatLng");

        if (sourceLatLng != null && destinationLatLng != null) {
            googleMap.addMarker(new MarkerOptions().position(sourceLatLng).title("Source"));
            googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));

            // Create a PolylineOptions object to define the car path
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(sourceLatLng)  // Starting point
                    .add(/* Add intermediate points here */)  // Add intermediate points as needed
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
        }, 0, 2000); // Update every 5 seconds (adjust as needed)
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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}

