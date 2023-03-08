package com.example.coffeedemo1.usecase

import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.services.api.CoffeeDtoRepo
import com.example.coffeedemo1.services.api.CoffeeService
import javax.inject.Inject

internal class GetLikedHotCoffeesUseCase @Inject constructor(
    private val coffeeService: CoffeeService,
    private val coffeeDtoRepo: CoffeeDtoRepo
) {
    operator fun invoke(): List<Coffee> {
        val dtos = coffeeDtoRepo.getHotCoffeeDtos()
        val likedDtos = dtos.filter { coffeeService.isCoffeeLiked(it.id.toString()) }
        return likedDtos.map {
            Coffee(it, true).copy(isLiked = true)
        }
    }

}