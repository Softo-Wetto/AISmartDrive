package com.example.aismartdrive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import java.io.IOException;
import java.util.List;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private EditText sourceEditText, destinationEditText;
    private Button calculateRouteButton, bookingButton, backButton;
    private GoogleMap googleMap;
    private MapView mapView; // Declare the MapView instance here

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        String vehicleName = getIntent().getStringExtra("vehicleName");

        if (vehicleName != null) {
            TextView vehicleNameTextView = findViewById(R.id.vehicleNameTextView);
            vehicleNameTextView.setText(vehicleName);
        }

        mapView = findViewById(R.id.mapView);
        sourceEditText = findViewById(R.id.sourceEditText);
        destinationEditText = findViewById(R.id.destinationEditText);
        calculateRouteButton = findViewById(R.id.calculateRouteButton);

        bookingButton = findViewById(R.id.bookingButton); // Initialize the button
        bookingButton.setVisibility(View.GONE); // Initially hide the button
        backButton = findViewById(R.id.backButton);


        // Initialize the mapView and get a reference to it
        MapView mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        calculateRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sourceAddress = sourceEditText.getText().toString();
                String destinationAddress = destinationEditText.getText().toString();

                // Use geocoding to convert source and destination addresses to LatLng
                LatLng sourceLatLng = getLatLngFromAddress(sourceAddress);
                LatLng destinationLatLng = getLatLngFromAddress(destinationAddress);

                // Calculate and display the route
                if (sourceLatLng != null && destinationLatLng != null) {
                    drawRoute(sourceLatLng, destinationLatLng);
                    // Show the "booking" button
                    bookingButton.setVisibility(View.VISIBLE);
                }
            }
        });
        bookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the source and destination information
                String sourceAddress = sourceEditText.getText().toString();
                String destinationAddress = destinationEditText.getText().toString();
                // Get the source and destination coordinates
                LatLng sourceLatLng = getLatLngFromAddress(sourceAddress);
                LatLng destinationLatLng = getLatLngFromAddress(destinationAddress);

                // Retrieve the vehicleName from the previous activity
                String vehicleName = getIntent().getStringExtra("vehicleName");

                // Create an Intent to start RideActivity
                Intent intent = new Intent(LocationActivity.this, RideActivity.class);
                // Put the source and destination information as extras in the intent
                intent.putExtra("sourceAddress", sourceAddress);
                intent.putExtra("destinationAddress", destinationAddress);
                // Put the source and destination coordinates as extras in the intent
                intent.putExtra("sourceLatLng", sourceLatLng);
                intent.putExtra("destinationLatLng", destinationLatLng);
                // Put the vehicleName as an extra in the intent
                intent.putExtra("vehicleName", vehicleName);
                // Start RideActivity with the intent
                startActivity(intent);
            }
        });
        backButton.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    // Helper method to convert address to LatLng
    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (!addresses.isEmpty()) {
                Address firstAddress = addresses.get(0);
                return new LatLng(firstAddress.getLatitude(), firstAddress.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper method to draw the route on the map
    private void drawRoute(LatLng sourceLatLng, LatLng destinationLatLng) {
        if (googleMap != null) {
            googleMap.clear();

            // Add markers for source and destination
            googleMap.addMarker(new MarkerOptions().position(sourceLatLng).title("Source"));
            googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));

            // Create and display the route
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(sourceLatLng)
                    .add(destinationLatLng)
                    .color(getResources().getColor(R.color.red))
                    .width(5);
            googleMap.addPolyline(polylineOptions);

            // Move camera to fit both markers and route
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(sourceLatLng);
            builder.include(destinationLatLng);
            LatLngBounds bounds = builder.build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100);
            googleMap.animateCamera(cameraUpdate);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}

