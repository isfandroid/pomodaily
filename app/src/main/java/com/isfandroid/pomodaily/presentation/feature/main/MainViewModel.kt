package com.isfandroid.pomodaily.presentation.feature.main

import androidx.lifecycle.ViewModel
import com.isfandroid.pomodaily.data.source.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(settingsRepository: SettingsRepository): ViewModel() {
    val appTheme = settingsRepository.appTheme
}