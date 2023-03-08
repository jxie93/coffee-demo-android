package com.example.coffeedemo1.usecase

import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.services.api.CoffeeDtoRepo
import com.example.coffeedemo1.services.api.CoffeeService
import javax.inject.Inject

internal class GetHotCoffeeWithLikeUseCase @Inject constructor(
    private val getHotCoffeesUseCase: GetHotCoffeesUseCase,
    private val coffeeService: CoffeeService
) {
    operator fun invoke(id: String): Coffee? {
        val coffees = getHotCoffeesUseCase.invoke()
        return coffees.firstOrNull { it.id == id }?.copy(
            isLiked = coffeeService.isCoffeeLiked(id)
        )
    }

}