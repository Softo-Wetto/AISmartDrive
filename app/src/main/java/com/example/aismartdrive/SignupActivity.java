package com.example.aismartdrive;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.aismartdrive.DB.AppDatabase;
import com.example.aismartdrive.DB.user.User;
import com.example.aismartdrive.DB.user.UserDao;

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
                .allowMainThreadQueries()
                .build();
        userDao = appDatabase.userDao();

        // Linking the views
        setViewIds();

        adminCheckBox = findViewById(R.id.adminCheckBox);

        backButton.setOnClickListener(view -> {
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

            if (isValidCredentials(name, email, phoneNumber, dateOfBirth, password, confirmPassword)) {
                boolean isAdmin = adminCheckBox.isChecked();

                float rating = 0;
                User newUser = new User(name, email, phoneNumber, dateOfBirth, password, isAdmin, null, rating, null);
                userDao.insert(newUser);
                Toast.makeText(SignupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
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

    private boolean isValidCredentials(String name, String email, String phoneNumber, String dateOfBirth, String password, String confirmPassword) {
        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            isValid = false;
            nameEditText.setError("Name is required");
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValid = false;
            emailEditText.setError("Invalid email");
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            isValid = false;
            phoneNumberEditText.setError("Phone number is required");
        }

        if (TextUtils.isEmpty(dateOfBirth)) {
            isValid = false;
            dateOfBirthEditText.setError("Date of birth is required");
        }

        if (password.length() < 6) {
            isValid = false;
            passwordEditText.setError("Password must be at least 6 characters");
        }

        if (!password.equals(confirmPassword)) {
            isValid = false;
            confirmPasswordEditText.setError("Passwords do not match");
        }

        User existingUser = userDao.getUserByEmail(email);
        if (existingUser != null) {
            isValid = false;
            emailEditText.setError("Email is already registered");
        }

        return isValid;
    }
}



