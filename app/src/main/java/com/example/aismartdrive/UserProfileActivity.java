package com.example.aismartdrive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.aismartdrive.DB.AppDatabase;
import com.example.aismartdrive.DB.user.User;
import com.example.aismartdrive.Utils.SharedPrefManager;

public class UserProfileActivity extends AppCompatActivity {

    private TextView nameTextView, emailTextView, dateOfBirthTextView, phoneNumberTextView, statusTextView;
    private AppDatabase appDatabase;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize Room database
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "user-database")
                .allowMainThreadQueries()
                .build();

        // Linking the views
        setViewIds();

        // Retrieve the user's information and display it
        User loggedInUser = getUserInformation();

        // Set the user's information in the TextViews
        nameTextView.setText(loggedInUser.getName());
        emailTextView.setText(loggedInUser.getEmail());
        dateOfBirthTextView.setText(loggedInUser.getDateOfBirth());
        phoneNumberTextView.setText(loggedInUser.getPhoneNumber());

        // Determine the user's status and display it
        String statusText = loggedInUser.isAdmin() ? "Status: Lender" : "Status: Customer";
        statusTextView.setText(statusText);


        backButton.setOnClickListener(view -> {
            // Navigate back to the MainActivity
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void setViewIds() {
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        dateOfBirthTextView = findViewById(R.id.dateOfBirthTextView);
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);
        statusTextView = findViewById(R.id.statusTextView);
        backButton = findViewById(R.id.backButton);
    }

    private User getUserInformation() {
        // Assuming the user is already logged in, retrieve user information based on the logged-in email
        String loggedInEmail = SharedPrefManager.getUserEmail(); // You need to implement this method

        // Retrieve user information from the database
        User user = appDatabase.userDao().getUserByEmail(loggedInEmail);
        return user;
    }
}
