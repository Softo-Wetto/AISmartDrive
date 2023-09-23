package com.example.aismartdrive;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentActivity extends AppCompatActivity {

    private EditText nameEditText, cardNumberEditText, expirationDateEditText, cvvEditText;
    private Button finishPaymentButton;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        nameEditText = findViewById(R.id.nameEditText);
        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        expirationDateEditText = findViewById(R.id.expirationDateEditText);
        cvvEditText = findViewById(R.id.cvvEditText);
        finishPaymentButton = findViewById(R.id.finishPaymentButton);

        // Get references to the TextViews in the layout
        TextView sourceAddressTextView = findViewById(R.id.sourceAddressTextView);
        TextView destinationAddressTextView = findViewById(R.id.destinationAddressTextView);
        TextView distanceTextView = findViewById(R.id.distanceTextView);
        TextView timeTextView = findViewById(R.id.timeTextView);
        TextView priceTextView = findViewById(R.id.priceTextView);

        // Retrieve data from the intent extras
        Intent intent = getIntent();
        String sourceAddress = intent.getStringExtra("sourceAddress");
        String destinationAddress = intent.getStringExtra("destinationAddress");
        double distance = intent.getDoubleExtra("distance", 0.0);
        double time = intent.getDoubleExtra("time", 0.0);
        double price = intent.getDoubleExtra("price", 0.0);
        String vehicleName = intent.getStringExtra("vehicleName"); // Retrieve vehicleName

        // Populate the TextViews with the retrieved data
        sourceAddressTextView.setText(sourceAddress);
        destinationAddressTextView.setText(destinationAddress);
        distanceTextView.setText(String.format("%.1f km", distance));
        timeTextView.setText(String.format("%.0f mins", time));
        priceTextView.setText(String.format("$%.2f", price));

        TextView vehicleNameTextView = findViewById(R.id.vehicleNameTextView);
        vehicleNameTextView.setText(vehicleName);

        finishPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = nameEditText.getText().toString();
                String cardNumber = cardNumberEditText.getText().toString();
                String expirationDate = expirationDateEditText.getText().toString();
                String cvv = cvvEditText.getText().toString();
                if (isValidPaymentInformation(fullName, cardNumber, expirationDate, cvv)) {
                    Intent reviewIntent = new Intent(PaymentActivity.this, ReviewActivity.class);
                    reviewIntent.putExtra("vehicleName", vehicleName);
                    startActivity(reviewIntent);
                } else {
                    Toast.makeText(PaymentActivity.this, "Invalid payment information. Please check and try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isValidPaymentInformation(String fullName, String cardNumber, String expirationDate, String cvv) {
        return !fullName.isEmpty() && !cardNumber.isEmpty() && !expirationDate.isEmpty() && !cvv.isEmpty();
    }
}
