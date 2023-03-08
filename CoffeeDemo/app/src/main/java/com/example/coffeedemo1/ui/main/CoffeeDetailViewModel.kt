package com.example.coffeedemo1.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeedemo1.domain.Coffee
import com.example.coffeedemo1.domain.Review
import com.example.coffeedemo1.services.api.CoffeeReviewService
import com.example.coffeedemo1.services.api.CoffeeService
import com.example.coffeedemo1.usecase.GetHotCoffeeWithLikeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CoffeeDetailViewModel @Inject constructor(
    private val coffeeReviewService: CoffeeReviewService,
    private val getHotCoffeeUseCase: GetHotCoffeeWithLikeUseCase
) : ViewModel() {

    private val _coffeeEntityFlow = MutableStateFlow<Coffee?>(null)
    val coffeeEntityFlow = _coffeeEntityFlow.asStateFlow()

    fun loadCoffee(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val coffee = getHotCoffeeUseCase.invoke(id)
            coffee?.let {
                _coffeeEntityFlow.value = it
            }
            Log.i("COFFEE!", "CoffeeDetailViewModel loadCoffee -> $coffee")
        }
    }

    fun sendReview(review: Review) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                coffeeReviewService.sendReview(review)
            } catch (e: Exception) {
                Log.e("COFFEE!", "sendReview error! $e")
            }
        }
    }
}