package com.example.ridecalculator;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("fetchFuelPrice/")
    Call<FuelPrice> fetchFuelPrice(@Field("city") String city, @Field("fuel_type") String fuel_type);

    

}
