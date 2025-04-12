package com.example.hotelreservationapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ReservationConfirmationActivity extends AppCompatActivity {

    private TextView textConfirmationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reservation_confirmation);

        textConfirmationMessage = findViewById(R.id.textConfirmationMessage);

        // Get confirmation number from Intent
        String confirmationNumber = getIntent().getStringExtra("confirmationNumber");

        if (confirmationNumber != null) {
            String message = "Thank you for your reservation, your reservation number is " + confirmationNumber + ".";
            textConfirmationMessage.setText(message);
        } else {
            textConfirmationMessage.setText("Reservation number not found.");
        }
    }
}
