package com.example.aismartdrive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aismartdrive.DB.Vehicle;
import com.example.aismartdrive.DB.VehicleDao;
import com.example.aismartdrive.Utils.MyApp;

public class VehicleDetailsActivity extends AppCompatActivity {

    TextView tvVehicleName, tvVehicleType, vehicleNumber, source, destination, currentLocation, fuelStatus;
    Button btnUpdate, btnDelete;
    VehicleDao vehicleDao;
    int vehicleId;
    Vehicle vehicle;

    private Button backButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvVehicleType = findViewById(R.id.tvVehicleType);
        vehicleNumber = findViewById(R.id.vehicleNumberTextView);
        source = findViewById(R.id.sourceTextView);
        destination = findViewById(R.id.destinationTextView);
        currentLocation = findViewById(R.id.currentLocationTextView);
        fuelStatus = findViewById(R.id.fuelStatusTextView);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        vehicleDao = MyApp.getAppDatabase().vehicleDao();

        if (getIntent().getIntExtra("vehicleId", -1) != -1){
            vehicleId = getIntent().getIntExtra("vehicleId", -1);

            vehicleDao.getVehicleById(vehicleId).observe(this, dbVehicle -> {
                if (dbVehicle != null) {
                    this.vehicle = dbVehicle;
                    tvVehicleName.setText("Vehicle Name: " + this.vehicle.getName());
                    tvVehicleType.setText("Vehicle Type: " + this.vehicle.getType());
                    vehicleNumber.setText("Vehicle Number: " + this.vehicle.getVehicleNumber());
                    source.setText("Source: " + this.vehicle.getSource());
                    destination.setText("Destination: " + this.vehicle.getDestination());
                    currentLocation.setText("Current Location: " + this.vehicle.getCurrentLocation());
                    fuelStatus.setText("Fuel Status: " + this.vehicle.getFuelStatus() + "%");
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (vehicle != null) {
                        updateTheVehicle();
                    }
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (vehicle != null) {
                        deleteTheVehicle();
                    }
                }
            });

        } else {
            Toast.makeText(this, "No Vehicle Id Found",
                    Toast.LENGTH_SHORT).show();
        }


        setViewIds();

        backButton.setOnClickListener(view -> {
            // Navigate back to the VehicleList
            Intent intent = new Intent(this, VehicleListActivity.class);
            startActivity(intent);
        });
    }

    private void updateTheVehicle() {
        // Creating the view to create the dialog. We are re-using the dialog we created in Week-4 to add new vehicle.
        View dialogView =
                getLayoutInflater().inflate(R.layout.dialog_add_vehicle, null);
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

        // Pre-set the current vehicle name and type to these edittext views.
        editTextVehicleName.setText(vehicle.getName());
        editTextVehicleType.setText(vehicle.getType());
        editTextVehicleNumber.setText(vehicle.getVehicleNumber());
        editTextSource.setText(vehicle.getSource());
        editTextDestination.setText(vehicle.getDestination());
        editTextCurrentLocation.setText(vehicle.getCurrentLocation());
        editTextFuelStatus.setText(vehicle.getFuelStatus());

        //Creating the dialog builder to create the pop up dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        //Setting and implementing the update operation
        builder.setPositiveButton("Update", (dialog, which) -> {
            String vehicleName = editTextVehicleName.getText().toString();
            String vehicleType = editTextVehicleType.getText().toString();
            String vehicleNumber = editTextVehicleNumber.getText().toString();
            String source = editTextSource.getText().toString();
            String destination = editTextDestination.getText().toString();
            String currentLocation = editTextCurrentLocation.getText().toString();
            String fuelStatus = editTextFuelStatus.getText().toString();

            //update the values
            vehicle.setName(vehicleName);
            vehicle.setType(vehicleType);
            vehicle.setVehicleNumber(vehicleNumber);
            vehicle.setSource(source);
            vehicle.setDestination(destination);
            vehicle.setCurrentLocation(currentLocation);
            vehicle.setFuelStatus(fuelStatus);
            AsyncTask.execute(() -> {
                vehicleDao.update(vehicle);
            });
        });
        // Setting the update cancellation
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Do nothing or handle any other actions
            dialog.cancel();
        });
        //Creating and showing the dialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteTheVehicle() {
        AsyncTask.execute(() -> {
            vehicleDao.delete(vehicle);
            finish();
        });
    }

    private void setViewIds() {
        backButton = findViewById(R.id.backButton);
    }
}
