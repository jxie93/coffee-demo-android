package com.example.coffeedemo1.hilt

import android.content.Context
import androidx.room.Room
import com.example.coffeedemo1.BuildConfig
import com.example.coffeedemo1.db.AppDatabase
import com.example.coffeedemo1.services.api.*
import com.example.coffeedemo1.services.api.CoffeeApiService
import com.example.coffeedemo1.services.api.CoffeeDtoRepo
import com.example.coffeedemo1.services.api.CoffeeDtoRepoImpl
import com.example.coffeedemo1.services.api.CoffeeService
import com.example.coffeedemo1.services.api.CoffeeServiceImpl
import com.example.coffeedemo1.services.db.CoffeeDatabaseService
import com.example.coffeedemo1.services.db.CoffeeDatabaseServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ServicesModule {

    @Binds
    @Singleton
    abstract fun bindCoffeeDtoRepo(impl: CoffeeDtoRepoImpl): CoffeeDtoRepo

    @Binds
    @Singleton
    abstract fun bindCoffeeService(impl: CoffeeServiceImpl): CoffeeService

    @Binds
    @Singleton
    abstract fun bindCoffeeReviewService(impl: CoffeeReviewServiceImpl): CoffeeReviewService

    @Binds
    @Singleton
    abstract fun bindCoffeeDatabaseService(impl: CoffeeDatabaseServiceImpl): CoffeeDatabaseService

}