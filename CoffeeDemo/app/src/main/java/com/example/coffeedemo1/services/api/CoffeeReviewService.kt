package com.example.coffeedemo1.services.api

import android.util.Log
import com.example.coffeedemo1.domain.CoffeeDto
import com.example.coffeedemo1.domain.Review
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal interface CoffeeReviewService {
    suspend fun sendReview(review: Review)
}

internal class CoffeeReviewServiceImpl @Inject constructor(
    private val apiService: CoffeeApiService
): CoffeeReviewService {
    override suspend fun sendReview(review: Review): Unit = suspendCoroutine { continuation ->
        apiService.postReview(review).enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    //do nothing
                } else {
                    continuation.resumeWithException(
                        HttpException(response)
                    )
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}