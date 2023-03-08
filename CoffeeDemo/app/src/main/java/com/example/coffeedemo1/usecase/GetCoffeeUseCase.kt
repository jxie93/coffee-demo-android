package com.example.coffeedemo1.usecase

import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.services.api.CoffeeDtoRepo
import javax.inject.Inject

internal class GetCoffeeUseCase @Inject constructor(
    private val coffeeDtoRepo: CoffeeDtoRepo
) {
    suspend operator fun invoke(id: String): Coffee? {
        id.toIntOrNull()?.let {
            coffeeDtoRepo.getHotCoffeeDto(it)?.let { dto ->
                return Coffee(dto, true)
            }
            coffeeDtoRepo.getIcedCoffeeDto(it)?.let { dto ->
                return Coffee(dto, false)
            }
        }
        return null
    }
}