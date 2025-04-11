package com.example.hotelreservationapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GuestDetailsAdapter extends RecyclerView.Adapter<GuestDetailsAdapter.ViewHolder> {

    private final List<GuestInfo> guestList;

    public GuestDetailsAdapter(List<GuestInfo> guestList) {
        this.guestList = guestList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textGuestNumber;
        EditText editGuestName;
        RadioGroup radioGender;
        RadioButton radioMale, radioFemale;

        public ViewHolder(View itemView) {
            super(itemView);
            textGuestNumber = itemView.findViewById(R.id.textGuestNumber);
            editGuestName = itemView.findViewById(R.id.editGuestName);
            radioGender = itemView.findViewById(R.id.radioGender);
            radioMale = itemView.findViewById(R.id.radioMale);
            radioFemale = itemView.findViewById(R.id.radioFemale);
        }
    }

    @NonNull
    @Override
    public GuestDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.guest_detail_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestDetailsAdapter.ViewHolder holder, int position) {
        GuestInfo guest = guestList.get(position);

        holder.textGuestNumber.setText("Guest " + (position + 1));
        holder.editGuestName.setHint("Guest Name");
        holder.editGuestName.setText(guest.getName());

        // Set gender
        if ("Male".equalsIgnoreCase(guest.getGender())) {
            holder.radioMale.setChecked(true);
        } else if ("Female".equalsIgnoreCase(guest.getGender())) {
            holder.radioFemale.setChecked(true);
        }

        // Listen for name changes
        holder.editGuestName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                guest.setName(holder.editGuestName.getText().toString());
            }
        });

        // Listen for gender selection
        holder.radioGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioMale) {
                guest.setGender("Male");
            } else if (checkedId == R.id.radioFemale) {
                guest.setGender("Female");
            }
        });
    }

    @Override
    public int getItemCount() {
        return guestList.size();
    }

    public List<GuestInfo> getGuestList() {
        return guestList;
    }
}

