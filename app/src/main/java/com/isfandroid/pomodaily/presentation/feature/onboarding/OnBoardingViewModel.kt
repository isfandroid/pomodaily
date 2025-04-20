package com.isfandroid.pomodaily.presentation.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.source.repository.PrefsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnBoardingViewModel @Inject constructor(
    private val prefsRepository: PrefsRepository
): ViewModel() {

    private val _navDirection = MutableSharedFlow<String>()
    val navDirection = _navDirection.asSharedFlow()

    fun finishOnBoardingAndNavigate(destination: String) {
        viewModelScope.launch {
            prefsRepository.setIsOnBoardingDone(true)
            _navDirection.emit(destination)
        }
    }
}