package com.example.coffeedemo1.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Review (
    @SerialName("name") val name: String? = null,
    @SerialName("date") val date: String? = null,
    @SerialName("comment") val comment: String? = null,
    @SerialName("rating") val rating: Int? = null,
)
