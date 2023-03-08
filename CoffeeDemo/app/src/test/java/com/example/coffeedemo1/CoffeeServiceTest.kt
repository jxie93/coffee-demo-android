package com.example.coffeedemo1

import com.example.coffeedemo1.domain.*
import com.example.coffeedemo1.services.api.CoffeeDtoRepo
import com.example.coffeedemo1.services.api.CoffeeService
import com.example.coffeedemo1.services.api.CoffeeServiceImpl
import com.example.coffeedemo1.usecase.GetHotCoffeesUseCase
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoffeeServiceTest {

    @InjectMockKs
    internal lateinit var coffeeService: CoffeeServiceImpl

    @MockK
    internal lateinit var coffeeDao: CoffeeDao

    @MockK
    internal lateinit var coffeeDtoRepo: CoffeeDtoRepo

    private val testCoffeeDto = CoffeeDto(
        id = 0,
        title = "test_title",
        description = "test_desc",
        ingredients = emptyList(),
        image = ""
    )

    private val useCase
        get() = GetHotCoffeesUseCase(
            coffeeDtoRepo,
            coffeeDao
        )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun `check likeCoffee updates likedIds`() {
        // given

        // when
        coffeeService.likeCoffee("test_id")

        // then
        Assert.assertTrue(coffeeService.isCoffeeLiked("test_id"))
    }
}