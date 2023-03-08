package com.example.coffeedemo1.domain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
internal interface CoffeeDao {
    @Query("SELECT * FROM coffee")
    fun getAll(): List<Coffee>

    @Insert(onConflict = REPLACE)
    fun insertAndReplace(coffee: Coffee)

    @Insert(onConflict = REPLACE)
    fun insertAndReplaceAll(coffee: List<Coffee>)

    @Delete
    fun delete(coffee: Coffee)

    @Query("DELETE FROM coffee")
    fun deleteAll()
}