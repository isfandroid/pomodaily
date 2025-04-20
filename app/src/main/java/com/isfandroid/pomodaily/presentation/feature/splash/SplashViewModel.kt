package com.isfandroid.pomodaily.presentation.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.source.repository.PrefsRepository
import com.isfandroid.pomodaily.utils.Constant.NAV_DESTINATION_ON_BOARDING
import com.isfandroid.pomodaily.utils.Constant.NAV_DESTINATION_SCHEDULE
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val prefsRepository: PrefsRepository
): ViewModel() {

    private val _navigateToDestination = MutableSharedFlow<String>()
    val navigateToDestination = _navigateToDestination.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(1000)
            checkNavigationDestination()
        }
    }

    private fun checkNavigationDestination() {
        viewModelScope.launch {
            val isOnboardingDone = prefsRepository.isOnBoardingDone.first()
            if (isOnboardingDone) {
                _navigateToDestination.emit(NAV_DESTINATION_SCHEDULE)
            } else {
                _navigateToDestination.emit(NAV_DESTINATION_ON_BOARDING)
            }
        }
    }
}