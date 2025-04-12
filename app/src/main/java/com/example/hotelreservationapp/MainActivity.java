package com.example.hotelreservationapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName;
    private TextView textGuestCount;
    private Button buttonDecrease, buttonIncrease, buttonSearch, buttonReset;
    private CalendarView calendarViewCheckIn, calendarViewCheckOut;
    private Calendar checkInCalendar, checkOutCalendar;
    private int guestCount = 1; // Default guest count

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.editTextName);
        textGuestCount = findViewById(R.id.textGuestCount);
        buttonDecrease = findViewById(R.id.buttonDecreaseGuest);
        buttonIncrease = findViewById(R.id.buttonIncreaseGuest);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonReset = findViewById(R.id.buttonReset);

        calendarViewCheckIn = findViewById(R.id.calendarCheckIn);
        calendarViewCheckOut = findViewById(R.id.calendarCheckOut);

        checkInCalendar = Calendar.getInstance();
        checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1); // Default: next day

        //  Prevent past date selection
        calendarViewCheckIn.setMinDate(System.currentTimeMillis() - 1000);
        calendarViewCheckOut.setMinDate(System.currentTimeMillis() - 1000);

        // Set default dates
        calendarViewCheckIn.setDate(checkInCalendar.getTimeInMillis());
        calendarViewCheckOut.setDate(checkOutCalendar.getTimeInMillis());

        textGuestCount.setText(String.valueOf(guestCount));

        buttonIncrease.setOnClickListener(v -> {
            if (guestCount < 10) {
                guestCount++;
                textGuestCount.setText(String.valueOf(guestCount));
            } else {
                Toast.makeText(this, "Maximum 10 guests allowed", Toast.LENGTH_SHORT).show();
            }
        });

        buttonDecrease.setOnClickListener(v -> {
            if (guestCount > 1) {
                guestCount--;
                textGuestCount.setText(String.valueOf(guestCount));
            } else {
                Toast.makeText(this, "At least 1 guest is required", Toast.LENGTH_SHORT).show();
            }
        });

        calendarViewCheckIn.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            checkInCalendar.set(year, month, dayOfMonth);
            if (!checkOutCalendar.after(checkInCalendar)) {
                checkOutCalendar.set(year, month, dayOfMonth);
                checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
                calendarViewCheckOut.setDate(checkOutCalendar.getTimeInMillis());
            }
        });

        calendarViewCheckOut.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar temp = Calendar.getInstance();
            temp.set(year, month, dayOfMonth);

            if (!temp.after(checkInCalendar)) {
                Toast.makeText(this, "Check-out must be after check-in", Toast.LENGTH_SHORT).show();
                calendarViewCheckOut.setDate(checkOutCalendar.getTimeInMillis());
            } else {
                checkOutCalendar.set(year, month, dayOfMonth);
            }
        });

        buttonSearch.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            } else if (!name.matches("^[a-zA-Z\\s]+$")) {
                Toast.makeText(this, "Please enter a valid name (letters only)", Toast.LENGTH_SHORT).show();
                return;
            }

            String checkIn = formatDate(checkInCalendar);
            String checkOut = formatDate(checkOutCalendar);

            SharedPreferences sharedPreferences  = getSharedPreferences("HotelPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences .edit();
            editor.putString("customerName", name);
            editor.putInt("guests", guestCount);
            editor.putString("check_in", checkIn);
            editor.putString("check_out", checkOut);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, HotelListActivity.class);
            intent.putExtra("customerName", name);
            intent.putExtra("guests", guestCount);
            intent.putExtra("checkinDate", checkIn);
            intent.putExtra("checkoutDate", checkOut);
            startActivity(intent);
        });

        buttonReset.setOnClickListener(v -> {
            editTextName.setText("");
            guestCount = 1;
            textGuestCount.setText(String.valueOf(guestCount));

            checkInCalendar = Calendar.getInstance();
            checkOutCalendar = Calendar.getInstance();
            checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);

            calendarViewCheckIn.setDate(checkInCalendar.getTimeInMillis());
            calendarViewCheckOut.setDate(checkOutCalendar.getTimeInMillis());

            Toast.makeText(this, "Form reset successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private String formatDate(Calendar calendar) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime());
    }
}
