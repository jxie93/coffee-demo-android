package com.example.coffeedemo1.usecase

import com.example.coffeedemo1.domain.Coffee

internal class GetHotCoffeeUseCase {
    operator fun invoke(coffeeList: List<Coffee>) = coffeeList.filter { it.isHot }
}