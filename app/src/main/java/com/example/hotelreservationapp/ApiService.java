package com.example.hotelreservationapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/hotels")  // replace with your actual endpoint
    Call<List<Hotel>> getHotels();
}
