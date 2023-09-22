package com.example.aismartdrive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.example.aismartdrive.DB.AppDatabase;
import com.example.aismartdrive.DB.user.User;

import com.example.aismartdrive.Utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;


public class UserProfileActivity extends AppCompatActivity {

    private TextView nameTextView, emailTextView, dateOfBirthTextView, phoneNumberTextView, statusTextView;
    private TextView vehicleNameTextView, ratingTextView, commentTextView; // Add TextViews for the new fields
    private AppDatabase appDatabase;
    private Button backButton;
    private RecyclerView reviewsRecyclerView;
    private ReviewsAdapter reviewsAdapter;
    private List<User> reviewsList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
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

        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsAdapter = new ReviewsAdapter(reviewsList);
        reviewsRecyclerView.setAdapter(reviewsAdapter);

        // Retrieve the user's information and display it
        User loggedInUser = getUserInformation();

        // Set the user's information in the TextViews along with labels
        nameTextView.setText("Name: " + loggedInUser.getName());
        emailTextView.setText("Email: " + loggedInUser.getEmail());
        dateOfBirthTextView.setText("Date of Birth: " + loggedInUser.getDateOfBirth());
        phoneNumberTextView.setText("Phone Number: " + loggedInUser.getPhoneNumber());

        String vehicleName = SharedPrefManager.getVehicleName();
        float userRating = SharedPrefManager.getUserRating();
        String userComment = SharedPrefManager.getUserComment();
        loggedInUser.setVehicleName(vehicleName);
        loggedInUser.setRating(userRating);
        loggedInUser.setComment(userComment);

        nameTextView.setText(loggedInUser.getName());

        // Display the vehicleName, rating, and comment in appropriate UI elements
        vehicleNameTextView.setText(loggedInUser.getVehicleName());
        ratingTextView.setText(String.valueOf(loggedInUser.getRating()));
        commentTextView.setText(loggedInUser.getComment());

        // Determine the user's status and display it
        String statusText = loggedInUser.isAdmin() ? "Status: Lender" : "Status: Customer";
        statusTextView.setText(statusText);

        // Retrieve all user reviews and populate the reviewsList
        List<User> userReviews = getUserReviews(loggedInUser.getEmail());
        reviewsList.addAll(userReviews);
        reviewsAdapter.notifyDataSetChanged();

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
        vehicleNameTextView = findViewById(R.id.vehicleNameTextView); // Initialize the new TextViews
        ratingTextView = findViewById(R.id.ratingTextView);
        commentTextView = findViewById(R.id.commentTextView);
        backButton = findViewById(R.id.backButton);
    }

    private User getUserInformation() {
        // Assuming the user is already logged in, retrieve user information based on the logged-in email
        String loggedInEmail = SharedPrefManager.getUserEmail();

        // Retrieve user information from the database
        User user = appDatabase.userDao().getUserByEmail(loggedInEmail);
        return user;
    }

    private List<User> getUserReviews(String userEmail) {
        // Retrieve all reviews for the user based on their email
        return appDatabase.userDao().getUserReviews(userEmail);
    }
}




