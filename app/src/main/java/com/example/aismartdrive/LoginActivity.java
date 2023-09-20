package com.example.aismartdrive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.aismartdrive.DB.AppDatabase;
import com.example.aismartdrive.DB.user.User;
import com.example.aismartdrive.Utils.SharedPrefManager;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, backButton;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Room database
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "user-database")
                .allowMainThreadQueries() // For simplicity; consider using AsyncTask or LiveData
                .build();

        // Linking the views
        setViewIds();

        backButton.setOnClickListener(view -> {
            // Navigate back to the MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            // Perform login authentication logic here
            if (isValidCredentials(email, password)) {
                SharedPrefManager.setUserEmail(email);
                // Successful login, navigate to next activity
                Toast.makeText(LoginActivity.this, "Login successful",
                        Toast.LENGTH_SHORT).show();
                //Go to the VehicleList Page
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            } else {
                // Invalid credentials, show error message
                Toast.makeText(LoginActivity.this, "Invalid Credentials",
                        Toast.LENGTH_SHORT).show();
                emailEditText.setError("Invalid email or password");
                passwordEditText.setError("Invalid email or password");
            }
        });

    }

    private void setViewIds() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        backButton = findViewById(R.id.backButton);
    }

    private boolean isValidCredentials(String email, String password) {
        // Perform database query to check if the provided email and password match a user's credentials
        User user = appDatabase.userDao().getUserByEmail(email);

        // Check if the user exists and if the provided password matches the stored password
        if (user != null && user.getPassword().equals(password)) {
            return true; // Credentials are valid
        }

        return false; // Invalid credentials
    }
}
