package com.example.coffeedemo1.usecase

import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.domain.CoffeeDao
import com.example.coffeedemo1.services.api.CoffeeDtoRepo
import com.example.coffeedemo1.services.api.CoffeeService
import javax.inject.Inject

internal class GetLikedHotCoffeesUseCase @Inject constructor(
    private val coffeeService: CoffeeService,
    private val coffeeDtoRepo: CoffeeDtoRepo,
    private val coffeeDao: CoffeeDao
) {
    suspend operator fun invoke(): List<Coffee> {
        val dtos = coffeeDtoRepo.getHotCoffeeDtos()
        val likedDtos = dtos.filter { coffeeService.isCoffeeLiked(it.id.toString()) }
        val likedCoffeesFromDtos = likedDtos.map {
                Coffee(it, true).copy(isLiked = true)
            }.takeIf { it.isNotEmpty() }
        val likedLocalEntities = mutableListOf<Coffee>().apply {
            coffeeDao.getAll()
                .filter { coffeeService.isCoffeeLiked(it.id) }
                .forEach { localCoffee ->
                    add(localCoffee.copy(isLiked = true))
                }
        }
        return likedCoffeesFromDtos ?:
            likedLocalEntities //fallback to local
    }

}