package com.example.aismartdrive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.aismartdrive.DB.AppDatabase;
import com.example.aismartdrive.DB.user.User;
import com.example.aismartdrive.Utils.SharedPrefManager;

public class HomeActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private Button vehicleListButton, userProfileButton;

    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Room database
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "user-database")
                .allowMainThreadQueries() // For simplicity; consider using AsyncTask or LiveData
                .build();

        // Linking the views
        setViewIds();

        // Retrieve the user's information from the database
        User loggedInUser = getUserInformation();

        // Display the user's name in the TextView
        userNameTextView.setText(loggedInUser.getName());

        // Set up click listeners for buttons
        vehicleListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to VehicleListActivity
                Intent intent = new Intent(HomeActivity.this, VehicleListActivity.class);
                startActivity(intent);
            }
        });

        userProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to UserProfileActivity
                Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setViewIds() {
        userNameTextView = findViewById(R.id.userNameTextView);
        vehicleListButton = findViewById(R.id.vehicleListButton);
        userProfileButton = findViewById(R.id.userProfileButton);
    }

    private User getUserInformation() {
        // Assuming the user is already logged in, retrieve user information based on the logged-in email
        String loggedInEmail = SharedPrefManager.getUserEmail(); // You need to implement this method

        // Retrieve user information from the database
        User user = appDatabase.userDao().getUserByEmail(loggedInEmail);

        return user;
    }
}