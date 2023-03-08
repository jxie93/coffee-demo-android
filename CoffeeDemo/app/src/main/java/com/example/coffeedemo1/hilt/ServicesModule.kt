package com.example.coffeedemo1.hilt

import com.example.coffeedemo1.services.api.*
import com.example.coffeedemo1.services.api.CoffeeApiService
import com.example.coffeedemo1.services.api.CoffeeDtoRepo
import com.example.coffeedemo1.services.api.CoffeeDtoRepoImpl
import com.example.coffeedemo1.services.api.CoffeeService
import com.example.coffeedemo1.services.api.CoffeeServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

}