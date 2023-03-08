package com.example.coffeedemo1.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.domain.CoffeeDao
import com.example.coffeedemo1.domain.EntityConverters

@Database(entities = [Coffee::class], version = 1)
@TypeConverters(EntityConverters::class)
internal abstract class AppDatabase: RoomDatabase() {
    abstract fun coffeeDao(): CoffeeDao
}