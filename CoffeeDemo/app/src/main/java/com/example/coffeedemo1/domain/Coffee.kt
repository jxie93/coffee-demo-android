package com.example.coffeedemo1.domain

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

internal enum class CoffeeCategory {
    HOT, ICED
}

@OptIn(ExperimentalSerializationApi::class)
internal class EntityConverters {
    private val json = Json { isLenient = true }
    @TypeConverter
    fun fromStringToList(input: String): List<String> = json.decodeFromString(input)
    @TypeConverter
    fun fromList(list: List<String>): String = json.encodeToString(list)
    @TypeConverter
    fun fromStringToUri(input: String): Uri = input.uriFromString
    @TypeConverter
    fun fromUri(uri: Uri): String = uri.toString()
}

@Entity
internal data class Coffee(
    @PrimaryKey val id: String,
    @ColumnInfo("title") val title: String,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("ingredients") val ingredients: List<String> = emptyList(),
    @ColumnInfo("image") val image: Uri,
    @ColumnInfo("category") val category: CoffeeCategory,
    @ColumnInfo("isPlaceholder") val isPlaceholder: Boolean,
    @ColumnInfo("isLiked") val isLiked: Boolean
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