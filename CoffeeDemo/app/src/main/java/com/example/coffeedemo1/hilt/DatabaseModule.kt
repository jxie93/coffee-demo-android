package com.example.coffeedemo1.hilt

import android.content.Context
import androidx.room.Room
import com.example.coffeedemo1.BuildConfig
import com.example.coffeedemo1.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "DB_${BuildConfig.APPLICATION_ID}"
    ).build()

    @Provides
    @Singleton
    fun provideCoffeeDao(
        db: AppDatabase
    ) = db.coffeeDao()

}