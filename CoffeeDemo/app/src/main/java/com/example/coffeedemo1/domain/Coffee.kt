package com.example.coffeedemo1.domain

import android.net.Uri
import java.util.UUID

internal enum class CoffeeCategory {
    HOT, ICED
}

internal data class Coffee(
    val title: String,
    val description: String,
    val ingredients: List<String> = emptyList(),
    val image: Uri,
    val id: String,
    val category: CoffeeCategory,
    val isPlaceholder: Boolean,
    val isLiked: Boolean
) {

    companion object {
        val placeholders = List(8) {
            Coffee(
                title = "",
                description = "",
                image = "".uriFromString,
                id = UUID.randomUUID().toString(),
                category = CoffeeCategory.HOT,
                isPlaceholder = true,
                isLiked = false
            )
        }
    }

    val isHot
        get() = category == CoffeeCategory.HOT

    constructor(dto: CoffeeDto, isHot: Boolean) : this(
        title = dto.title,
        description = dto.description,
        ingredients = dto.ingredients,
        image = dto.image.uriFromString,
        id = dto.id.toString(),
        category = if (isHot) CoffeeCategory.HOT else CoffeeCategory.ICED,
        isPlaceholder = false,
        isLiked = false
    )
}

val String?.uriFromString: Uri
    get() = try {
        Uri.parse(this)
    } catch (ex: Exception) {
        Uri.EMPTY
    }