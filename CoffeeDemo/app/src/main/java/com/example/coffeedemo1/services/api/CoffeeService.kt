package com.example.coffeedemo1.services.api

import android.util.Log
import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.domain.CoffeeDao
import com.squareup.picasso.Picasso
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

internal interface CoffeeService {
    val coffeeFlow: SharedFlow<List<Coffee>>
    suspend fun loadAll()
    suspend fun loadIcedCoffee()
    suspend fun loadHotCoffee()
    suspend fun loadHotCoffeeLocal()
    fun likeCoffee(id: String)
    fun unlikeCoffee(id: String)
    fun isCoffeeLiked(id: String): Boolean
}

internal class CoffeeServiceImpl @Inject constructor(
    private val coffeeDtoRepo: CoffeeDtoRepo,
    private val coffeeDao: CoffeeDao
): CoffeeService {

    override val coffeeFlow: SharedFlow<List<Coffee>>
        get() = _coffeeFlow.asSharedFlow()

    private var likedIds: MutableSet<String> = mutableSetOf()

    private var _coffeeFlow: MutableSharedFlow<List<Coffee>> = MutableSharedFlow(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private fun List<Coffee>.preloadImages() {
        forEach {
            Picasso.get().load(it.image).fetch()
        }
    }

    override fun isCoffeeLiked(id: String): Boolean {
        return id in likedIds
    }

    override fun likeCoffee(id: String) {
        likedIds.add(id)
    }

    override fun unlikeCoffee(id: String) {
        likedIds.remove(id)
    }

    private fun updateLocalData(entities: List<Coffee>) {
        if (entities.isNotEmpty()) {
            coffeeDao.deleteAll()
            coffeeDao.insertAndReplaceAll(entities)
        }
    }

    override suspend fun loadAll() {
        try {
            val icedCoffeeList = coffeeDtoRepo.fetchIcedCoffees()
            val hotCoffeeList = coffeeDtoRepo.fetchHotCoffees()
            val entities = icedCoffeeList.map { Coffee(it, false) } +
                    hotCoffeeList.map { Coffee(it, true) }
            _coffeeFlow.emit(entities)
            entities.preloadImages()
            updateLocalData(entities)
        } catch (e: Exception) {
            Log.e("COFFEE!", CoffeeError.NoCoffeeFound(e).toString())
        }
    }

    override suspend fun loadIcedCoffee() {
        try {
            val coffeeList = coffeeDtoRepo.fetchIcedCoffees()
            val entities = coffeeList.map { Coffee(it, false) }
            _coffeeFlow.emit(entities)
            entities.preloadImages()
            updateLocalData(entities)
        } catch (e: Exception) {
            Log.e("COFFEE!", CoffeeError.NoCoffeeFound(e).toString())
        }
    }

    override suspend fun loadHotCoffee() {
        try {
            val coffeeList = coffeeDtoRepo.fetchHotCoffees()
            val entities = coffeeList.map { Coffee(it, true) }
            _coffeeFlow.emit(entities)
            entities.preloadImages()
            updateLocalData(entities)
        } catch (e: Exception) {
            Log.e("COFFEE!", CoffeeError.NoCoffeeFound(e).toString())
        }
    }

    override suspend fun loadHotCoffeeLocal() {
        Log.i("DEBUG", "local -> ${coffeeDao.getAll()}")
        _coffeeFlow.emit(coffeeDao.getAll())
    }
}