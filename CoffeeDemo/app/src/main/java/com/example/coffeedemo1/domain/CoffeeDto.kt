package com.example.coffeedemo1.domain

import android.icu.text.CaseMap.Title
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CoffeeDto(
    val title: String,
    val description: String,
    val ingredients: List<String> = emptyList(),
    val image: String,
    val id: Int
)