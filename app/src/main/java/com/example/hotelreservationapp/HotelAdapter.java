package com.example.hotelreservationapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private List<Hotel> hotelList;
    private int selectedPosition = -1;
    private OnHotelSelectListener listener;

    // Interface for hotel selection callback
    public interface OnHotelSelectListener {
        void onHotelSelected(Hotel selectedHotel);
    }

    // Constructor with null-safe list initialization
    public HotelAdapter(List<Hotel> hotelList, OnHotelSelectListener listener) {
        this.hotelList = (hotelList != null) ? hotelList : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_item, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);

        holder.hotelName.setText(hotel.getName());
        holder.hotelPrice.setText("Price: $" + hotel.getPrice());
        holder.hotelAvailability.setText(hotel.isAvailable() ? "Available" : "Not Available");
        holder.hotelLocation.setText("Location: " + hotel.getLocation());

        holder.radioSelect.setChecked(position == selectedPosition);

        // Dim visually if hotel is not available
        if (!hotel.isAvailable()) {
            holder.itemView.setAlpha(0.5f);
            holder.radioSelect.setEnabled(false);
        } else {
            holder.itemView.setAlpha(1.0f);
            holder.radioSelect.setEnabled(true);
        }

        // Only allow selection if hotel is available
        holder.radioSelect.setOnClickListener(v -> {
            if (hotel.isAvailable()) {
                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
                listener.onHotelSelected(hotel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotelList != null ? hotelList.size() : 0;
    }

    // ViewHolder class for holding hotel item views
    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        TextView hotelName, hotelPrice, hotelAvailability, hotelLocation;
        RadioButton radioSelect;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelName = itemView.findViewById(R.id.hotelName);
            hotelPrice = itemView.findViewById(R.id.hotelPrice);
            hotelAvailability = itemView.findViewById(R.id.hotelAvailability);
            hotelLocation = itemView.findViewById(R.id.hotelLocation);
            radioSelect = itemView.findViewById(R.id.radioSelect);
        }
    }
}
