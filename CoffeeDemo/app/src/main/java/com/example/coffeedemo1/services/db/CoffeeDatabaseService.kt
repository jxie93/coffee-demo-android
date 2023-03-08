package com.example.coffeedemo1.services.db

import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.domain.CoffeeDao
import javax.inject.Inject

internal interface CoffeeDatabaseService {
    fun getCoffee(id: String): Coffee?
    fun getAllHotCoffees(): List<Coffee>
    fun getAllIcedCoffees(): List<Coffee>
    fun getAllCoffees(): List<Coffee>
    fun saveCoffee(coffee: Coffee)
    fun saveCoffees(coffees: List<Coffee>)
}

internal class CoffeeDatabaseServiceImpl @Inject constructor(
    private val coffeeDao: CoffeeDao
): CoffeeDatabaseService {

    override fun getCoffee(id: String): Coffee? =
        coffeeDao.getAll().firstOrNull { it.id == id }

    override fun getAllHotCoffees(): List<Coffee> =
        coffeeDao.getAll().filter { it.isHot }

    override fun getAllIcedCoffees(): List<Coffee> =
        coffeeDao.getAll().filter { !it.isHot }

    override fun getAllCoffees(): List<Coffee> = coffeeDao.getAll()

    override fun saveCoffee(coffee: Coffee) {
        coffeeDao.insertAndReplace(coffee)
    }

    override fun saveCoffees(coffees: List<Coffee>) {
        coffeeDao.insertAndReplaceAll(coffees)
    }
}