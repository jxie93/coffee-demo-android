package com.example.coffeedemo1.usecase

import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.domain.CoffeeDao
import com.example.coffeedemo1.services.api.CoffeeDtoRepo
import com.example.coffeedemo1.services.api.CoffeeService
import javax.inject.Inject

internal class GetHotCoffeesUseCase @Inject constructor(
    private val coffeeDtoRepo: CoffeeDtoRepo,
    private val coffeeDao: CoffeeDao
) {
    suspend operator fun invoke(): List<Coffee> {
        val dtos = coffeeDtoRepo.getHotCoffeeDtos()
        return dtos.map { Coffee(it, true) }
            .takeIf { it.isNotEmpty() } ?:
            coffeeDao.getAll().filter { it.isHot } //fallback to local
    }

}