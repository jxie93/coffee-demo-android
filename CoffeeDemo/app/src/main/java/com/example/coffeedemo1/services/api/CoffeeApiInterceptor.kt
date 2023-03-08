package com.example.coffeedemo1.services.api

import android.util.Log
import com.example.coffeedemo1.domain.CoffeeDto
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Call
import retrofit2.http.GET

internal class CoffeeApiInterceptor : Interceptor {

    companion object {
        internal const val HEADER_USER_AGENT = "User-Agent"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        Log.i("COFFEE!", "http request to -> ${request.url()}")

        val urlBuilder = request.url().newBuilder().apply {
            scheme("https")
//            setQueryParameter()
            //query params - some APIs require authentication
        }

        val builder = request.newBuilder().url(urlBuilder.build()).apply {
//            addHeader()
            //header fields - some APIs require this to prevent scraping, analytics, etc
        }

        return try {
            chain.proceed(builder.build())
        } catch (e: Exception) { //error response
            Log.e("COFFEE!", "http request error: $e")
            Response.Builder().apply {
                message(e.message.toString())
                request(request)
            }.build()
        }
    }

}