package com.example.coffeedemo1.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.services.api.CoffeeService
import com.example.coffeedemo1.usecase.GetCoffeeUseCase
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class CoffeeDetailViewModel @Inject constructor(
    private val coffeeService: CoffeeService,
    private val getCoffeeUseCase: GetCoffeeUseCase
) : ViewModel() {

    private val _coffeeEntityFlow = MutableStateFlow<Coffee?>(null)
    val coffeeEntityFlow = _coffeeEntityFlow.asStateFlow()

    init {

    }

    fun loadCoffee(id: String) {
        viewModelScope.launch {
            val coffee = getCoffeeUseCase.invoke(id)
            coffee?.let {
                _coffeeEntityFlow.value = it
            }
            Log.i("COFFEE!", "CoffeeDetailViewModel loadCoffee -> $coffee")
        }
    }
}