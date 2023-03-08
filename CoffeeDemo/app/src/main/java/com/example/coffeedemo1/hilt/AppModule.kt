package com.example.coffeedemo1.hilt

import android.content.Context
import androidx.room.Room
import com.example.coffeedemo1.BuildConfig
import com.example.coffeedemo1.db.AppDatabase
import com.example.coffeedemo1.services.api.*
import com.example.coffeedemo1.services.api.CoffeeService
import com.example.coffeedemo1.services.api.CoffeeServiceImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.ConnectionPool
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class AppModule {

    @Provides
    @Singleton
    fun provideJson() = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
    }

    @Provides
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
//            .cache(diskCache)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        builder: OkHttpClient.Builder,
    ): OkHttpClient {
        return builder
            .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor(CoffeeApiInterceptor())
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        val url = "https://${BuildConfig.API_URL}/"
        val asConverterFactory = json.asConverterFactory(MediaType.parse("application/json")!!)
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(asConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideCoffeeApiService(
        retrofit: Retrofit
    ): CoffeeApiService = retrofit.create(CoffeeApiService::class.java)

}