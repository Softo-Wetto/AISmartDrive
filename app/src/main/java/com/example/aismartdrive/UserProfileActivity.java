package com.example.aismartdrive;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.aismartdrive.DB.AppDatabase;
import com.example.aismartdrive.DB.user.User;
import com.example.aismartdrive.Utils.SharedPrefManager;

public class UserProfileActivity extends AppCompatActivity {

    private TextView nameTextView, emailTextView, dateOfBirthTextView, phoneNumberTextView;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize Room database
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "user-database")
                .allowMainThreadQueries() // For simplicity; consider using AsyncTask or LiveData
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
    }

    private void setViewIds() {
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        dateOfBirthTextView = findViewById(R.id.dateOfBirthTextView);
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);
    }

    private User getUserInformation() {
        // Assuming the user is already logged in, retrieve user information based on the logged-in email
        String loggedInEmail = SharedPrefManager.getUserEmail(); // You need to implement this method

        // Retrieve user information from the database
        User user = appDatabase.userDao().getUserByEmail(loggedInEmail);

        return user;
    }
}
