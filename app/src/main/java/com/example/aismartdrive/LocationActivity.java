package com.example.aismartdrive;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private TextView locationTextView;
    private LocationManager locationManager;
    private GoogleMap googleMap;
    private MapView mapView;
    private LatLng sourceLatLng; // Replace with your source coordinates
    private LatLng destinationLatLng; // Replace with your destination coordinates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        locationTextView = findViewById(R.id.locationTextView);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Initialize the mapView and get a reference to it
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Request location updates from the GPS provider
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Handle permissions here if needed
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop receiving location updates when the activity is paused
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Handle location changes and display the latitude and longitude
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Update the UI with the location information
        locationTextView.setText("Latitude: " + latitude + "\nLongitude: " + longitude);

        // Add a log statement to verify that this method is called
        Log.d("LocationActivity", "Location updated: " + latitude + ", " + longitude);

        // You can update the source coordinates here as needed
        sourceLatLng = new LatLng(latitude, longitude);
    }

    // Implement the OnMapReadyCallback
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add a marker for the source location
        if (sourceLatLng != null) {
            googleMap.addMarker(new MarkerOptions().position(sourceLatLng).title("Source"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sourceLatLng));
        }

        // Add a marker for the destination location
        if (destinationLatLng != null) {
            googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Handle status changes if needed
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Handle when the location provider is enabled
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Handle when the location provider is disabled
    }
}

