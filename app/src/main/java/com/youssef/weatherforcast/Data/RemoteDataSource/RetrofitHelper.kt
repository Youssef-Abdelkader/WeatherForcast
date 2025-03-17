package com.youssef.weatherforcast.Data.RemoteDataSource

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    val retrofitInstance =
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    val service : ApiService = retrofitInstance.create(ApiService::class.java)



}