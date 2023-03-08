package com.example.coffeedemo1.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.services.api.CoffeeService
import com.example.coffeedemo1.usecase.GetHotCoffeeWithLikeUseCase
import com.example.coffeedemo1.usecase.GetHotCoffeesWithLikesUseCase
import com.example.coffeedemo1.usecase.GetLikedHotCoffeesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
internal class CoffeeListViewModel @Inject constructor(
    private val coffeeService: CoffeeService,
    private val getHotCoffeeWithLikeUseCase: GetHotCoffeeWithLikeUseCase,
    private val getLikedHotCoffeesUseCase: GetLikedHotCoffeesUseCase,
    private val getHotCoffeesWithLikesUseCase: GetHotCoffeesWithLikesUseCase
) : ViewModel() {

    enum class DisplayCategory {
        HOT, LIKED
    }

    private var hotCoffeeData = emptyList<Coffee>()

    private val _displayDataFlow = MutableStateFlow<List<Coffee>>(emptyList())
    val displayDataFlow = _displayDataFlow.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _displayCategory = MutableStateFlow(DisplayCategory.HOT)
    val displayCategory = _displayCategory.asStateFlow()

    private var remoteDataJob: Job? = null
    private var localDataJob: Job? = null

    init {
        //get local data
        localDataJob?.cancel()
        localDataJob = viewModelScope.launch(Dispatchers.IO) {
            coffeeService.loadHotCoffeeLocal()
        }

        //get remote data
        remoteDataJob?.cancel()
        remoteDataJob = viewModelScope.launch(Dispatchers.IO) {
            coffeeService.loadHotCoffee()
        }

        //observe main data flow
        coffeeService.coffeeFlow
            .combine(_displayCategory) { data, category -> Pair(data, category) }
            .onEach {(rawData, category) ->
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }

                withContext(Dispatchers.IO) {
                    val likedHotCoffees = getLikedHotCoffeesUseCase.invoke()
                    //apply like data
                    hotCoffeeData = getHotCoffeesWithLikesUseCase.invoke()

                    when(category) {
                        DisplayCategory.HOT -> _displayDataFlow.value = hotCoffeeData
                        DisplayCategory.LIKED -> _displayDataFlow.value = likedHotCoffees
                    }
                }

                Log.i("COFFEE!", "coffeeFlow x${rawData.size} -> x${_displayDataFlow.value.size}, category $category")
        }.launchIn(viewModelScope)
    }

    fun likeOrUnlikeCoffee(id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val coffee = getHotCoffeeWithLikeUseCase.invoke(id) ?: return@withContext
                if (coffee.isLiked) {
                    coffeeService.unlikeCoffee(id)
                } else {
                    coffeeService.likeCoffee(id)
                }
                _displayDataFlow.value = when (_displayCategory.value) {
                    DisplayCategory.HOT -> getHotCoffeesWithLikesUseCase.invoke()
                    DisplayCategory.LIKED -> getLikedHotCoffeesUseCase.invoke()
                }
            }
        }
    }

    fun changeDisplayCategory(category: DisplayCategory) {
        _displayCategory.value = category
    }

    fun reloadForCurrentCategory() {
        Log.i("COFFEE!", "reloadForCurrentCategory ${_displayCategory.value}")

        when (_displayCategory.value) {
            DisplayCategory.HOT -> reloadHotCoffee()
            DisplayCategory.LIKED -> {
                //do nothing
            }
        }
    }

    private fun reloadIcedCoffee() {
        remoteDataJob?.cancel()
        remoteDataJob = viewModelScope.launch(Dispatchers.IO) {
            coffeeService.loadIcedCoffee()
        }
        _isLoading.value = true
    }

    private fun reloadHotCoffee() {
        remoteDataJob?.cancel()
        remoteDataJob = viewModelScope.launch(Dispatchers.IO) {
            coffeeService.loadHotCoffee()
        }
        _isLoading.value = true
    }

}