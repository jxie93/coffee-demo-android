package com.example.coffeedemo1.usecase

import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.services.api.CoffeeDtoRepo
import javax.inject.Inject

internal class GetPrettyIngredientsTextUseCase {
    operator fun invoke(coffee: Coffee): String {
        if (coffee.ingredients.isEmpty()) return ""
        return coffee.ingredients.joinToString()
    }
}