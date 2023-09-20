package com.example.aismartdrive;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aismartdrive.DB.vehicle.Vehicle;
import com.example.aismartdrive.DB.vehicle.VehicleDao;
import com.example.aismartdrive.DB.sensor.AccelerometerData;
import com.example.aismartdrive.SensorUtil.SensorService;
import com.example.aismartdrive.Utils.MyApp;
import com.example.aismartdrive.Utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class VehicleListActivity extends AppCompatActivity {
    ArrayList<Vehicle> vehicleList;
    private RecyclerView recyclerView;
    private Button btnAddNewVehicle, backButton;
    private VehicleAdapter vehicleAdapter;
    private Intent serviceIntent;
    private SensorDataReceiver dataReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        viewBinding();
        initialising();
        getDataFromDatabase();
        handleClickOnVehicleItem();
        manageRoleBasedFeatures();
        manageSensorServices();
        getDataFromDatabase();
        setViewIds();

        backButton.setOnClickListener(view -> {
            // Navigate back to the MainActivity
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void setViewIds() {
        backButton = findViewById(R.id.backButton);
    }

    private void viewBinding() {
        recyclerView = findViewById(R.id.recyclerView);
        btnAddNewVehicle = findViewById(R.id.btnAddVehicle);
    }

    private void initialising() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vehicleList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(vehicleList);
        recyclerView.setAdapter(vehicleAdapter);
    }

    private void handleClickOnVehicleItem() {
        vehicleAdapter.setOnItemClickListener(position -> {
            // Get the clicked vehicle
            Vehicle clickedVehicle = vehicleList.get(position);

            // Create an Intent to navigate to VehicleDetailsActivity
            Intent intent = new Intent(VehicleListActivity.this, VehicleDetailsActivity.class);

            // Pass necessary data to the VehicleDetailsActivity
            intent.putExtra("vehicleName", clickedVehicle.getName());
            intent.putExtra("vehicleType", clickedVehicle.getType());
            intent.putExtra("vehicleNumber", clickedVehicle.getVehicleNumber());
            intent.putExtra("source", clickedVehicle.getSource());
            intent.putExtra("destination", clickedVehicle.getDestination());
            intent.putExtra("currentLocation", clickedVehicle.getCurrentLocation());
            intent.putExtra("fuelStatus", clickedVehicle.getFuelStatus());

            Vehicle vehicle = vehicleList.get(position);
            intent.putExtra("vehicleId",vehicle.id);

            // Start the VehicleDetailsActivity
            startActivity(intent);
        });
    }

    private void manageRoleBasedFeatures() {
        //Add Vehicle Button Show or don't show based on user role
        if (SharedPrefManager.isAdmin()) {
            btnAddNewVehicle.setVisibility(View.VISIBLE);
            btnAddNewVehicle.setOnClickListener(view -> {
                manageNewVehicleFunctionality();
            });
        } else {
            btnAddNewVehicle.setVisibility(View.GONE);
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void manageSensorServices() {
        // Start the FallDetectionService (Existing code)
        serviceIntent = new Intent(this, SensorService.class);
        startService(serviceIntent);

        // Register the BroadcastReceiver (New Code)
        dataReceiver = new SensorDataReceiver(); // Initialising the variable
        IntentFilter filter = new IntentFilter("VEHICLE_SENSOR_DATA"); //creating an instance of the filter that returns the sensor data
        registerReceiver(dataReceiver, filter); // registering the filter with our dataReceiver
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dataReceiver);
    }

    private class SensorDataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("VEHICLE_SENSOR_DATA")) {
                // Receive the sensor data and update the UI if required.
                AccelerometerData accelerometerData = (AccelerometerData) intent.getSerializableExtra("accelerometerData");
                // Making changes in the UI based on sensor data
                if (accelerometerData != null && accelerometerData.getMagnitude() < 9.81){
                    getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.white, null));
                }else {
                    // Toast.makeText(VehicleListActivity.this, "Danger!!!", Toast.LENGTH_SHORT).show();
                    getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.red, null));
                }
            }
        }
    }

    private void manageNewVehicleFunctionality() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView =
                getLayoutInflater().inflate(R.layout.dialog_add_vehicle, null);
        builder.setView(dialogView);
        EditText editTextVehicleName =
                dialogView.findViewById(R.id.editTextVehicleName);
        EditText editTextVehicleType =
                dialogView.findViewById(R.id.editTextVehicleType);
        EditText editTextVehicleNumber =
                dialogView.findViewById(R.id.editTextVehicleNumber);
        EditText editTextSource =
                dialogView.findViewById(R.id.editTextSource);
        EditText editTextDestination =
                dialogView.findViewById(R.id.editTextDestination);
        EditText editTextCurrentLocation =
                dialogView.findViewById(R.id.editTextCurrentLocation);
        EditText editTextFuelStatus =
                dialogView.findViewById(R.id.editTextFuelStatus);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String vehicleName = editTextVehicleName.getText().toString();
            String vehicleType = editTextVehicleType.getText().toString();
            String vehicleNumber = editTextVehicleNumber.getText().toString();
            String source = editTextSource.getText().toString();
            String destination = editTextDestination.getText().toString();
            String currentLocation = editTextCurrentLocation.getText().toString();
            String fuelStatus = editTextFuelStatus.getText().toString();

            // Add the new vehicle to the list
            Vehicle vehicle = new Vehicle(vehicleName, vehicleType, vehicleNumber, source, destination, currentLocation, fuelStatus);

            VehicleDao vehicleDao = MyApp.getAppDatabase().vehicleDao();
            AsyncTask.execute(() -> { // Asynctask uses a different thread asynchronously to insert the data. It prevents blocking the UI thread so that user can continue interacting with the app.
                vehicleDao.insert(vehicle);
            });

            // Refresh the RecyclerView
            //vehicleAdapter.notifyDataSetChanged();
        });
        AlertDialog dialog = builder.create();
        builder.setNegativeButton("Cancel", null);
        dialog.show();
    }

    private void getDataFromDatabase() {
        // Retrieve all vehicles asynchronously using LiveData
        VehicleDao vehicleDao = MyApp.getAppDatabase().vehicleDao();
        LiveData<List<Vehicle>> vehiclesLiveData = vehicleDao.getAllVehicles();
        vehiclesLiveData.observe(this, vehicles -> {
            // Handle the list of vehicles here
            vehicleList.clear(); // Removes all the existing data
            vehicleList.addAll(vehicles); // Adding all the data from Database
            vehicleAdapter.notifyDataSetChanged();
        });
    }
}
