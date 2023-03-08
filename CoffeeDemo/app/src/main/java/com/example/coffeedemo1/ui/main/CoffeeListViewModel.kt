package com.example.coffeedemo1.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.services.api.CoffeeService
import com.example.coffeedemo1.usecase.GetHotCoffeeUseCase
import com.example.coffeedemo1.usecase.GetIcedCoffeeUseCase
import com.example.coffeedemo1.usecase.GetLikedCoffeeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
internal class CoffeeListViewModel @Inject constructor(
    private val coffeeService: CoffeeService
) : ViewModel() {

    enum class DisplayCategory {
        HOT, ICED, LIKED
    }

    private val getHotCoffeeUseCase = GetHotCoffeeUseCase()
    private val getIcedCoffeeUseCase = GetIcedCoffeeUseCase()
    private val getLikedCoffeeUseCase = GetLikedCoffeeUseCase()

    private var hotCoffeeData = emptyList<Coffee>()
    private var icedCoffeeData = emptyList<Coffee>()

    private val _displayDataFlow = MutableStateFlow<List<Coffee>>(emptyList())
    val displayDataFlow = _displayDataFlow.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _displayCategory = MutableStateFlow(DisplayCategory.HOT)
    val displayCategory = _displayCategory.asStateFlow()

    private var reloadDataJob: Job? = null

    init {
        //get data
        reloadDataJob?.cancel()
        reloadDataJob = viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            coffeeService.loadAll()
        }

        //observe main data flow
        coffeeService.coffeeFlow
            .combine(_displayCategory) { data, category -> Pair(data, category) }
            .onEach {(data, category) ->
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }

                val hotDataOnly = data.filterNot { it.isLiked }.all { it.isHot }
                val coldDataOnly = data.filterNot { it.isLiked }.all { !it.isHot }

                if (data.isNotEmpty()) {
                    if (!coldDataOnly) hotCoffeeData = getHotCoffeeUseCase.invoke(data)
                    if (!hotDataOnly) icedCoffeeData = getIcedCoffeeUseCase.invoke(data)
                }

                //apply like

                //TODO add liked data

                when(category) {
                    DisplayCategory.HOT -> _displayDataFlow.value = hotCoffeeData
                    DisplayCategory.ICED -> _displayDataFlow.value = icedCoffeeData
                    DisplayCategory.LIKED ->
                        _displayDataFlow.value = getLikedCoffeeUseCase.invoke(_displayDataFlow.value)
                }

                Log.i("COFFEE!", "coffeeFlow liked x${getLikedCoffeeUseCase.invoke(
                    _displayDataFlow.value
                ).size}")


                Log.i("COFFEE!", "coffeeFlow x${data.size} -> x${_displayDataFlow.value.size}, category $category")
        }.launchIn(viewModelScope)
    }

    fun likeOrUnlikeCoffee(id: String) {

//        val coffee = hotCoffeeData.firstOrNull { it.id == id } ?:
//            icedCoffeeData.firstOrNull { it.id == id } ?: return
//        val coffeeUpdate = coffee.copy(isLiked = !coffee.isLiked)
//        if (coffeeUpdate.isLiked) {
//            likedIds.remove(coffee.id)
//        } else {
//            likedIds.add(coffee.id)
//        }
//
//        //TODO liked ids
//        _displayDataFlow.value = _displayDataFlow.value.toMutableList().apply {
//            val updateIndex = indexOfFirst { it.id == coffee.id }
//            this[updateIndex] = coffeeUpdate
//        }

//        if (coffeeUpdate.isHot) {
//            val index = hotCoffeeData.indexOf(coffee)
//            val dataUpdate = hotCoffeeData.toMutableList()
//            dataUpdate[index] = coffeeUpdate
//            hotCoffeeData = dataUpdate
//            _coffeeDataFlow.value = hotCoffeeData
//        } else {
//            val index = icedCoffeeData.indexOf(coffee)
//            val dataUpdate = icedCoffeeData.toMutableList()
//            dataUpdate[index] = coffeeUpdate
//            icedCoffeeData = dataUpdate
//            _coffeeDataFlow.value = icedCoffeeData
//        }
    }

    fun changeDisplayCategory(category: DisplayCategory) {
        _displayCategory.value = category
    }

    fun reloadForCurrentCategory() {
        Log.i("COFFEE!", "reloadForCurrentCategory ${_displayCategory.value}")

        when (_displayCategory.value) {
            DisplayCategory.HOT -> reloadHotCoffee()
            DisplayCategory.ICED -> reloadIcedCoffee()
            DisplayCategory.LIKED -> {
                //do nothing
            }
        }
    }

    private fun reloadIcedCoffee() {
        reloadDataJob?.cancel()
        reloadDataJob = viewModelScope.launch(Dispatchers.IO) {
            coffeeService.loadIcedCoffee()
        }
        _isLoading.value = true
    }

    private fun reloadHotCoffee() {
        reloadDataJob?.cancel()
        reloadDataJob = viewModelScope.launch(Dispatchers.IO) {
            coffeeService.loadHotCoffee()
        }
        _isLoading.value = true
    }

}