package com.example.hotelreservationapp;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface HotelApi {

    // Define the endpoint to get the list of hotels
    @GET("api/hotels/")
    Call<List<Hotel>> getHotels();

    // Define the endpoint to post reservation details
    @POST("api/reservation/")
    Call<String> postReservation(@Body RequestBody reservationDetails);
}
