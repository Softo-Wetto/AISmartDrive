package com.example.aismartdrive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aismartdrive.Utils.SharedPrefManager;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                // Successful login, navigate to next activity
                Toast.makeText(LoginActivity.this, "Login successful",
                        Toast.LENGTH_SHORT).show();
                SharedPrefManager.setLoginState(true);
                if (email.equals("ad@ad.com")){
                    SharedPrefManager.setAdmin(true);
                }else {
                    SharedPrefManager.setAdmin(false);
                }
                //Go to the VehicleList Page
                Intent intent = new Intent(this, VehicleListActivity.class);
                startActivity(intent);
            } else {
                // Invalid credentials, show error message
                Toast.makeText(LoginActivity.this, "Invalid Credentials",
                        Toast.LENGTH_SHORT).show();
                emailEditText.setError("Invalid email");
                passwordEditText.setError("Invalid password");
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
        // Perform validation logic here
        // Return true if credentials are valid, false otherwise
        return (email.equals("ex@ex.com") && password.equals("ex123")) ||
                (email.equals("ad@ad.com") && password.equals("ad123"));
    }
}
