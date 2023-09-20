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
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.example.aismartdrive.DB.AppDatabase;
import com.example.aismartdrive.DB.sensor.TemperatureDao;
import com.example.aismartdrive.DB.sensor.TemperatureData;
import com.example.aismartdrive.DB.user.User;
import com.example.aismartdrive.DB.vehicle.Vehicle;
import com.example.aismartdrive.DB.vehicle.VehicleDao;
import com.example.aismartdrive.Utils.MyApp;
import com.example.aismartdrive.Utils.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VehicleDetailsActivity extends AppCompatActivity {

    TextView tvVehicleName, tvVehicleType, vehicleNumber, source, destination, currentLocation, fuelStatus;
    Button btnUpdate, btnDelete;
    VehicleDao vehicleDao;
    int vehicleId;
    Vehicle vehicle;
    private Button backButton, bookButton;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        // Initialize Room database
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "user-database")
                .allowMainThreadQueries() // For simplicity; consider using AsyncTask or LiveData
                .build();

        setViewIds();
        bindViews();
        initialisation();
        populatingVehicleDetails();
        analyseTemperatureData();
        analyseAccelerometerData();
        manageRoleBasedFeatures();

        backButton.setOnClickListener(view -> {
            // Navigate back to the VehicleList
            Intent intent = new Intent(this, VehicleListActivity.class);
            startActivity(intent);
        });
        bookButton.setOnClickListener(view -> {
            // Navigate back to the VehicleList
            Intent intent = new Intent(this, LocationActivity.class);
            startActivity(intent);
        });
    }

    private void analyseTemperatureData() {

        // Define the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        // Initialising the chart view with the AnyChart variable
        AnyChartView tempChartView = findViewById(R.id.tempChart);
        // Adding the progress bar for loading the chart
        tempChartView.setProgressBar(findViewById(R.id.progress_bar));

        // Creating an instance of the line chart.
        Cartesian tempLineChart = AnyChart.line();
        tempLineChart.animation(true); // Setting Animation for the chart
        tempLineChart.padding(10d, 20d, 5d, 20d);

        // Setting the tap on value bar with X-value (Cross between the temperature and corresponding time)
        tempLineChart.crosshair().enabled(true);
        tempLineChart.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        // Setting a point for tapping on the chart.
        tempLineChart.tooltip().positionMode(TooltipPositionMode.POINT);

        tempLineChart.title("Temperature analysis over the time.");
        tempLineChart.yAxis(0).title("Temperature (Degree celcius)");
        tempLineChart.xAxis(0).title("Timestamp");
        tempLineChart.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        tempLineChart.legend().enabled(true);
        tempLineChart.legend().fontSize(13d);
        tempLineChart.legend().padding(0d, 0d, 10d, 0d);

        // Initialising data for the chart
        List<DataEntry> seriesData = new ArrayList<>();
        Set set = Set.instantiate(); // Initiating AnyChart set to synch the custom data with AnyChart data
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }"); // Map for mapping values in X-Axis (time) with Y-axis (temperature)

        // Creating series for first car. For multiple car, OR multiple lines of data, we need to repeat all the following 'series1' codes for each of them
        Line series1 = tempLineChart.line(series1Mapping);
        series1.name(tvVehicleName.getText().toString());
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        tempChartView.setChart(tempLineChart); // adding the chart to the chart view

        TemperatureDao temperatureDao = MyApp.getAppDatabase().temperatureDao();
        LiveData<List<TemperatureData>> temperatureLiveData = temperatureDao.getAllTemperatureData();
        temperatureLiveData.observe(this, temperatureDataList -> {
            // Handle the list of vehicles here
            for(TemperatureData temperatureData: temperatureDataList){
                Date date = new Date(temperatureData.getTimeStamp());
                // converting our data into AnyChart customdata and adding them to seriesData
                seriesData.add(new CustomDataEntry(dateFormat.format(date), temperatureData.getTemp()));
            }

            // Setting the series data into the set method of AnyChart library.
            set.data(seriesData);

        });
    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value) {
            super(x, value);
        }
    }

    private void analyseAccelerometerData() {

    }

    private void manageRoleBasedFeatures() {
        User loggedInUser = getUserInformation();

        if (loggedInUser != null && loggedInUser.isAdmin()) {
            // User is an admin, show admin features
            btnDelete.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.VISIBLE);

            btnUpdate.setOnClickListener(view -> {
                updateTheVehicle();
            });
            btnDelete.setOnClickListener(view -> {
                deleteTheVehicle();
            });
        } else {
            // User is not an admin, hide admin features
            btnUpdate.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }
    }

    private User getUserInformation() {
        // Retrieve the logged-in user's email from shared preferences
        String loggedInEmail = SharedPrefManager.getUserEmail();

        // Retrieve user information from the database using the email
        User user = appDatabase.userDao().getUserByEmail(loggedInEmail);

        return user;
    }


    private void populatingVehicleDetails() {

        if (getIntent().getIntExtra("vehicleId", -1) != -1){
            vehicleId = getIntent().getIntExtra("vehicleId", -1);
            retrievingAndSettingVehicleData(vehicleId);
            handlingUpdateEvent();
            handlingDeleteEvent();
        }else {
            Toast.makeText(this, "No Vehicle Id Found", Toast.LENGTH_SHORT).show();
        }

    }

    private void handlingUpdateEvent() {
        btnUpdate.setOnClickListener(view -> {
            if (vehicle != null) {
                updateTheVehicle();
            }
        });
    }

    private void handlingDeleteEvent() {
        btnDelete.setOnClickListener(view -> {
            if (vehicle != null) {
                deleteTheVehicle();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void retrievingAndSettingVehicleData(int vehicleId) {
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
    }

    private void initialisation() {
        vehicleDao = MyApp.getAppDatabase().vehicleDao();
    }

    private void bindViews() {
        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvVehicleType = findViewById(R.id.tvVehicleType);
        vehicleNumber = findViewById(R.id.vehicleNumberTextView);
        source = findViewById(R.id.sourceTextView);
        destination = findViewById(R.id.destinationTextView);
        currentLocation = findViewById(R.id.currentLocationTextView);
        fuelStatus = findViewById(R.id.fuelStatusTextView);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
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
        bookButton = findViewById(R.id.bookButton);
    }
}
