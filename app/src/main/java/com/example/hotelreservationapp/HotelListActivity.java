package com.example.hotelreservationapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotelListActivity extends AppCompatActivity {

    private TextView summaryTextView;
    private RecyclerView recyclerView;
    private HotelAdapter hotelAdapter;
    private List<Hotel> hotelList;

    private String checkinDate;
    private String checkoutDate;
    private int numberOfGuests;
    private String customerName;

    private Hotel selectedHotel = null; // Store the selected hotel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_hotel_list);

        // Get data passed from previous screen
        Intent intent = getIntent();
        customerName = intent.getStringExtra("customerName");
        checkinDate = intent.getStringExtra("checkinDate");
        checkoutDate = intent.getStringExtra("checkoutDate");
        numberOfGuests = intent.getIntExtra("guests", 1);

        // Set up UI components
        summaryTextView = findViewById(R.id.userInfoText);
        recyclerView = findViewById(R.id.recyclerView);
        Button nextButton = findViewById(R.id.nextButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Display summary
        String summaryText = "Welcome " + customerName + "!\nBooking details: " + "\nCheck-in: " + checkinDate +
                "\nCheck-out: " + checkoutDate + "\nGuests: " + numberOfGuests;
        summaryTextView.setText(summaryText);

        // Fetch hotel data from API
        fetchHotels();

        // Next button action
        nextButton.setOnClickListener(v -> {
            if (selectedHotel != null) {
                Intent nextIntent = new Intent(HotelListActivity.this, ReservationDetailsActivity.class);
                nextIntent.putExtra("customerName", customerName);
                nextIntent.putExtra("checkinDate", checkinDate);
                nextIntent.putExtra("checkoutDate", checkoutDate);
                nextIntent.putExtra("guests", numberOfGuests);
                nextIntent.putExtra("selectedHotelName", selectedHotel.getName());
                nextIntent.putExtra("selectedHotelPrice", selectedHotel.getPrice());
                startActivity(nextIntent);
            } else {
                Toast.makeText(this, "Please select a hotel before proceeding.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchHotels() {
        HotelApi apiService = RetrofitClient.getRetrofitInstance().create(HotelApi.class);

        Call<List<Hotel>> call = apiService.getHotels();
        call.enqueue(new Callback<List<Hotel>>() {
            @Override
            public void onResponse(Call<List<Hotel>> call, Response<List<Hotel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hotelList = response.body();

                    hotelAdapter = new HotelAdapter(hotelList, hotel -> {
                        selectedHotel = hotel; // store selected hotel
                    });

                    recyclerView.setAdapter(hotelAdapter);
                } else {
                    Toast.makeText(HotelListActivity.this, "Failed to fetch hotels.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Hotel>> call, Throwable t) {
                Toast.makeText(HotelListActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("HotelListActivity", "API Error: Failed to fetch hotels." + ", Body: " + t.getMessage());
            }
        });
    }
}
