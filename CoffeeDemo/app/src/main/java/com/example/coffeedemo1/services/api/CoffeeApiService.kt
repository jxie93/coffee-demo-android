package com.example.coffeedemo1.services.api

import com.example.coffeedemo1.domain.CoffeeDto
import com.example.coffeedemo1.domain.Review
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

internal interface CoffeeApiService {
    @GET("/coffee/hot")
    fun getHotCoffeeList(): Call<List<CoffeeDto>>

    @GET("/coffee/iced")
    fun getIcedCoffeeList(): Call<List<CoffeeDto>>

    @POST("/coffee/review")
    fun postReview(@Body review: Review): Call<Void>

}