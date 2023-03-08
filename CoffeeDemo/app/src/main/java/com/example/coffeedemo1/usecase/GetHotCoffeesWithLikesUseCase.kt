package com.example.coffeedemo1.usecase

import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.services.api.CoffeeDtoRepo
import com.example.coffeedemo1.services.api.CoffeeService
import javax.inject.Inject

internal class GetHotCoffeesWithLikesUseCase @Inject constructor(
    private val getLikedHotCoffeesUseCase: GetLikedHotCoffeesUseCase,
    private val getHotCoffeesUseCase: GetHotCoffeesUseCase
) {
    suspend operator fun invoke(): List<Coffee> {
        val rawHotCoffeesData = getHotCoffeesUseCase.invoke()
        val likedHotCoffeesData = getLikedHotCoffeesUseCase.invoke()
        return mutableListOf<Coffee>().apply {
            rawHotCoffeesData.toMutableList().forEach {
                val isLiked = it.id in likedHotCoffeesData.map { likedHot -> likedHot.id }
                add(it.copy(isLiked = isLiked))
            }
        }
    }

}