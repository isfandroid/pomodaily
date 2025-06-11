package com.isfandroid.pomodaily.presentation.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.source.repository.GeneralRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val repository: GeneralRepository
): ViewModel() {

    private val _isOnBoardingFinished = MutableSharedFlow<Boolean>()
    val isOnBoardingFinished = _isOnBoardingFinished.asSharedFlow()

    fun finishOnBoarding() {
        viewModelScope.launch {
            repository.setIsOnBoardingDone(true)
            _isOnBoardingFinished.emit(true)
        }
    }
}