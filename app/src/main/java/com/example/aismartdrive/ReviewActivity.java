package com.example.aismartdrive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.aismartdrive.DB.user.User;
import com.example.aismartdrive.Utils.SharedPrefManager;

public class ReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitButton;
    private String userEmail; // Use userEmail instead of vehicleName
    private String vehicleName; // Added for vehicleName

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Retrieve the userEmail from Shared Preferences
        userEmail = SharedPrefManager.getUserEmail();

        // Retrieve the vehicleName from the intent extras
        vehicleName = getIntent().getStringExtra("vehicleName");

        ratingBar = findViewById(R.id.ratingBar);
        commentEditText = findViewById(R.id.commentEditText);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user's rating and comment
                float userRating = ratingBar.getRating();
                String userComment = commentEditText.getText().toString();

                // Check if the rating and comment are not empty
                if (userRating == 0.0) {
                    // Display an error message for an empty rating
                    Toast.makeText(ReviewActivity.this, "Please provide a rating.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(userComment)) {
                    // Display an error message for an empty comment
                    Toast.makeText(ReviewActivity.this, "Please provide a comment.", Toast.LENGTH_SHORT).show();
                } else {
                    // Load the user data from Shared Preferences
                    User user = SharedPrefManager.loadUser();

                    SharedPrefManager.setVehicleName(vehicleName);
                    SharedPrefManager.setUserRating(userRating);
                    SharedPrefManager.setUserComment(userComment);

                    // Update the user object with rating, comment, and vehicleName
                    user.setRating(userRating);
                    user.setComment(userComment);
                    user.setVehicleName(vehicleName); // Set the vehicleName

                    // Save the updated user object in Shared Preferences
                    SharedPrefManager.saveUser(user);

                    // Display a thank you message in a toast
                    Toast.makeText(ReviewActivity.this, "Thank you for using our service!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ReviewActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish(); // Finish the current activity to prevent going back to it.
                }
            }
        });
    }
}



