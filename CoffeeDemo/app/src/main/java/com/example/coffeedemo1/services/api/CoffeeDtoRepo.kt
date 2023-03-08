package com.example.coffeedemo1.services.api

import com.example.coffeedemo1.domain.CoffeeDto
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal interface CoffeeDtoRepo{
    suspend fun fetchHotCoffees(): List<CoffeeDto>
    suspend fun fetchIcedCoffees(): List<CoffeeDto>
    suspend fun getHotCoffeeDto(id: Int): CoffeeDto?
    suspend fun getIcedCoffeeDto(id: Int): CoffeeDto?
}

internal class CoffeeDtoRepoImpl @Inject constructor(
    private val apiService: CoffeeApiService
): CoffeeDtoRepo {
    var hotCoffeeDtoList: List<CoffeeDto>? = emptyList()
    var icedCoffeeDtoList: List<CoffeeDto>? = emptyList()

    override suspend fun getHotCoffeeDto(id: Int): CoffeeDto? =
        hotCoffeeDtoList?.firstOrNull { it.id == id }

    override suspend fun getIcedCoffeeDto(id: Int): CoffeeDto? =
        icedCoffeeDtoList?.firstOrNull { it.id == id }

    override suspend fun fetchHotCoffees(): List<CoffeeDto> = suspendCoroutine { continuation ->
        apiService.getHotCoffeeList().enqueue(object : retrofit2.Callback<List<CoffeeDto>?> {
            override fun onResponse(
                call: Call<List<CoffeeDto>?>,
                response: Response<List<CoffeeDto>?>
            ) {
                if (response.isSuccessful) {
                    hotCoffeeDtoList = response.body() ?: emptyList()
                    continuation.resume(hotCoffeeDtoList!!)
                } else {
                    continuation.resumeWithException(
                        HttpException(response)
                    )
                }
            }

            override fun onFailure(call: Call<List<CoffeeDto>?>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }

    override suspend fun fetchIcedCoffees(): List<CoffeeDto> = suspendCoroutine { continuation ->
        apiService.getIcedCoffeeList().enqueue(object : retrofit2.Callback<List<CoffeeDto>?> {
            override fun onResponse(
                call: Call<List<CoffeeDto>?>,
                response: Response<List<CoffeeDto>?>
            ) {
                if (response.isSuccessful) {
                    icedCoffeeDtoList = response.body() ?: emptyList()
                    continuation.resume(icedCoffeeDtoList!!)
                } else {
                    continuation.resumeWithException(
                        HttpException(response)
                    )
                }
            }

            override fun onFailure(call: Call<List<CoffeeDto>?>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}