package com.example.coffeedemo1.services.api

import com.example.coffeedemo1.domain.CoffeeDto
import retrofit2.Call
import retrofit2.http.GET

internal interface CoffeeApiService {
    @GET("/coffee/hot")
    fun getHotCoffeeList(): Call<List<CoffeeDto>>

    @GET("/coffee/iced")
    fun getIcedCoffeeList(): Call<List<CoffeeDto>>
}