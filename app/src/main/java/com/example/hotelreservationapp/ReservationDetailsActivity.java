package com.example.hotelreservationapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationDetailsActivity extends AppCompatActivity {

    private TextView textHotelName, textCheckInDate, textCheckOutDate, textPrice;
    private RecyclerView recyclerViewGuestDetails;
    private Button buttonSubmitReservation;
    private GuestDetailsAdapter adapter;
    private ArrayList<GuestInfo> guestList;

    private String hotelName, checkInDate, checkOutDate, customerName;
    private double price;
    private int guestCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_reservation);

        // Initialize Views
        textHotelName = findViewById(R.id.textHotelName);
        textCheckInDate = findViewById(R.id.textCheckIn);
        textCheckOutDate = findViewById(R.id.textCheckOut);
        textPrice = findViewById(R.id.textPrice);
        recyclerViewGuestDetails = findViewById(R.id.recyclerGuestDetails);
        buttonSubmitReservation = findViewById(R.id.buttonSubmitReservation);

        // Get data from Intent
        Intent intent = getIntent();
        hotelName = intent.getStringExtra("selectedHotelName");
        checkInDate = intent.getStringExtra("checkinDate");
        checkOutDate = intent.getStringExtra("checkoutDate");
        price = intent.getDoubleExtra("selectedHotelPrice", 0.0);
        guestCount = intent.getIntExtra("guests", 1);
        customerName = intent.getStringExtra("customerName");

        // Set text values
        textHotelName.setText("Hotel Name: " + hotelName);
        textCheckInDate.setText("Check-in Date: " + checkInDate);
        textCheckOutDate.setText("Check-out Date: " + checkOutDate);
        textPrice.setText("Price: $" + price);

        // Setup RecyclerView
        guestList = new ArrayList<>();
        for (int i = 0; i < guestCount; i++) {
            guestList.add(new GuestInfo("", "Male")); // Default gender: Male
        }

        adapter = new GuestDetailsAdapter(guestList);
        recyclerViewGuestDetails.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewGuestDetails.setAdapter(adapter);

        // Submit Button Click
        buttonSubmitReservation.setOnClickListener(v -> {
            boolean allFilled = true;

            for (GuestInfo guest : guestList) {
                if (guest.getName().trim().isEmpty()) {
                    allFilled = false;
                    break;
                }
            }

            if (!allFilled) {
                Toast.makeText(this, "Please fill all guest names.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call AsyncTask to send the POST request
            submitReservation();
        });
    }

    private void submitReservation() {
        // Prepare reservation details as a JSON object
        JSONObject reservationDetails = new JSONObject();
        try {
            reservationDetails.put("hotel_name", hotelName);
            reservationDetails.put("checkin", checkInDate);
            reservationDetails.put("checkout", checkOutDate);
            reservationDetails.put("price", price);
            reservationDetails.put("guest", guestCount);
            reservationDetails.put("name", customerName);

            // Add amount (if it's required by the API, ensure to provide it)
            reservationDetails.put("amount", price); // Assuming amount refers to the total cost of the reservation

            // Create the guest list as a JSON array
            JSONArray guestsArray = new JSONArray();

            // Check if all guest names are filled
            for (GuestInfo guest : guestList) {
                if (guest.getName().trim().isEmpty()) {
                    Toast.makeText(this, "Please fill in all guest names.", Toast.LENGTH_SHORT).show();
                    return; // Exit early if any name is missing
                }

                JSONObject guestObject = new JSONObject();
                guestObject.put("name", guest.getName()); // Ensure name is passed correctly
                guestObject.put("gender", guest.getGender());
                guestsArray.put(guestObject);
            }

            // Log to ensure guest names are correct
            Log.d("ReservationDetailsActivity", "Guest Names: " + guestsArray.toString());

            reservationDetails.put("guests_list", guestsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Convert the reservation details to RequestBody
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reservationDetails.toString());

        // Initialize Retrofit and get the service
        HotelApi apiService = RetrofitClient.getRetrofitInstance().create(HotelApi.class);

        // Call the POST method
        Call<String> call = apiService.postReservation(requestBody);

        // Enqueue the call for asynchronous execution
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Handle the successful response (confirmation number)
                    String confirmationNumber = response.body();
                    Toast.makeText(ReservationDetailsActivity.this, "Confirmation Number: " + confirmationNumber, Toast.LENGTH_LONG).show();

                    // Redirect to the reservation confirmation page
                    Intent confirmationIntent = new Intent(ReservationDetailsActivity.this, ReservationConfirmationActivity.class);
                    confirmationIntent.putExtra("confirmationNumber", confirmationNumber);
                    startActivity(confirmationIntent);
                    finish();
                } else {
                    Toast.makeText(ReservationDetailsActivity.this, "Failed to process reservation.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Handle the failure case
                Toast.makeText(ReservationDetailsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ReservationDetailsActivity", "API Error: Failed to submit reservation. " + t.getMessage());
            }
        });
    }
}

