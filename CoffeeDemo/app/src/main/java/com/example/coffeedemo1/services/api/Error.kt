package com.example.coffeedemo1.services.api

import androidx.annotation.Keep

@Keep
sealed class CoffeeError(msg: String, cause: Exception? = null) : Exception(msg, cause) {

    @Keep
    data class NoCoffeeFound(override val cause: Exception? = null) :
        Error("No coffee data found at the API", cause)

    @Keep
    data class InvalidCoffeeCategory(override val cause: Exception? = null) :
        Error("Invalid coffee category!", cause)
}