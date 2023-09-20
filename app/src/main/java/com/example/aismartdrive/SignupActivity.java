package com.example.aismartdrive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.aismartdrive.DB.AppDatabase;
import com.example.aismartdrive.DB.user.User;
import com.example.aismartdrive.DB.user.UserDao;
import com.example.aismartdrive.Utils.SharedPrefManager;

public class SignupActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneNumberEditText, dateOfBirthEditText, passwordEditText, confirmPasswordEditText;
    private Button signupButton, backButton;
    private AppDatabase appDatabase;
    private UserDao userDao;
    private CheckBox adminCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Room database and UserDao
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "user-database")
                .allowMainThreadQueries() // For simplicity; consider using AsyncTask or LiveData
                .build();
        userDao = appDatabase.userDao();

        // Linking the views
        setViewIds();

        // Initialize the CheckBox
        adminCheckBox = findViewById(R.id.adminCheckBox);

        backButton.setOnClickListener(view -> {
            // Navigate back to the MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        signupButton.setOnClickListener(view -> {
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String phoneNumber = phoneNumberEditText.getText().toString();
            String dateOfBirth = dateOfBirthEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Perform signup authentication logic here
            if (isValidCredentials(email, password, confirmPassword)) {
                // Check if the admin CheckBox is checked
                boolean isAdmin = adminCheckBox.isChecked();

                // Create a User object with the isAdmin parameter
                User newUser = new User(name, email, phoneNumber, dateOfBirth, password, isAdmin);

                // Insert the user into the database
                userDao.insert(newUser);

                // Successful signup, navigate to next activity or perform necessary actions
                Toast.makeText(SignupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                // Navigate to LoginActivity
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Invalid credentials, show error message
                Toast.makeText(SignupActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setViewIds() {
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmpasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        backButton = findViewById(R.id.backButton);
    }

    private boolean isValidCredentials(String email, String password, String confirmPassword) {
        // Perform validation logic here
        boolean isValid = true;

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValid = false;
            emailEditText.setError("Invalid email");
        }

        if (password.length() < 6) {
            isValid = false;
            passwordEditText.setError("Password must be at least 6 characters");
        }

        if (!password.equals(confirmPassword)) {
            isValid = false;
            confirmPasswordEditText.setError("Passwords do not match");
        }

        // Check if the email is already registered
        User existingUser = userDao.getUserByEmail(email);
        if (existingUser != null) {
            isValid = false;
            emailEditText.setError("Email is already registered");
        }

        return isValid;
    }
}



