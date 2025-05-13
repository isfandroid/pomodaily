package com.isfandroid.pomodaily.presentation.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.source.repository.SettingsRepository
import com.isfandroid.pomodaily.utils.Constant.NAV_DESTINATION_ON_BOARDING
import com.isfandroid.pomodaily.utils.Constant.NAV_DESTINATION_POMODORO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _navDestination = MutableSharedFlow<String>()
    val navDestination =_navDestination.asSharedFlow()

    init {
        checkNavDestination()
    }

    private fun checkNavDestination() {
        viewModelScope.launch {
            delay(1000)
            if (settingsRepository.isOnBoardingDone.first()) {
                _navDestination.emit(NAV_DESTINATION_POMODORO)
            } else {
                _navDestination.emit(NAV_DESTINATION_ON_BOARDING)
            }
        }
    }
}