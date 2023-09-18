package com.example.aismartdrive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText ;
    private Button signupButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Linking the views
        setViewIds();

        backButton.setOnClickListener(view -> {
            // Navigate back to the MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        signupButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Perform signup authentication logic here
            if (isValidCredentials(email, password, confirmPassword)) {
                // Successful signup, navigate to next activity or perform necessary actions
                Toast.makeText(SignupActivity.this, "Signup successful",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Invalid credentials, show error message
                Toast.makeText(SignupActivity.this, "Invalid Credentials",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setViewIds() {
        emailEditText = findViewById(R.id.emailEditText);
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

        return isValid;
    }
}
