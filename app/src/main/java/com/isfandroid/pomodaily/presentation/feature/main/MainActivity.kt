package com.isfandroid.pomodaily.presentation.feature.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.collection.isNotEmpty
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.ActivityMainBinding
import com.isfandroid.pomodaily.presentation.feature.onboarding.OnBoardingContainerFragment
import com.isfandroid.pomodaily.presentation.feature.pomodoro.PomodoroFragment
import com.isfandroid.pomodaily.presentation.feature.schedule.ScheduleFragment
import com.isfandroid.pomodaily.presentation.feature.settings.SettingsFragment
import com.isfandroid.pomodaily.presentation.feature.splash.SplashFragment
import com.isfandroid.pomodaily.presentation.feature.stats.StatsFragment
import com.isfandroid.pomodaily.presentation.feature.task.TasksFragment
import com.isfandroid.pomodaily.utils.Constant.APP_THEME_LIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val ACTION_NAVIGATE_TO_POMODORO = "ACTION_NAVIGATE_TO_POMODORO"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation Components
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.graph = navController.createGraph(
            startDestination = Splash
        ) {
            fragment<SplashFragment, Splash> {
                label = getString(R.string.txt_splash)
            }
            fragment<OnBoardingContainerFragment, OnBoarding> {
                label = getString(R.string.txt_on_boarding)
            }
            fragment<TasksFragment, Tasks> {
                label = getString(R.string.txt_tasks)
            }
            fragment<PomodoroFragment, Pomodoro> {
                label = getString(R.string.txt_pomodoro)
            }
            fragment<StatsFragment, Statistics> {
                label = getString(R.string.txt_statistics)
            }
            fragment<ScheduleFragment, Schedule> {
                label = getString(R.string.txt_schedule)
            }
            fragment<SettingsFragment, Settings> {
                label = getString(R.string.txt_settings)
            }
        }

        // Observe Data
        observeData()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == ACTION_NAVIGATE_TO_POMODORO) {
            if (navController.graph.nodes.isNotEmpty()) {
                val pomodoroRouteKey = Pomodoro::class.qualifiedName
                val currentDestinationRouteKey = navController.currentDestination?.route

                if (pomodoroRouteKey != null && currentDestinationRouteKey != pomodoroRouteKey) {
                    if (navController.graph.findNode(pomodoroRouteKey) != null) {
                        navController.navigate(Pomodoro) {
                            launchSingleTop = true
                        }
                    }
                }
            }
            intent.action = null
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appTheme.collectLatest {
                    if (it == APP_THEME_LIGHT) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
            }
        }
    }
}

// SCREENS
@Serializable
data object Splash

@Serializable
data object OnBoarding

@Serializable
data object Pomodoro

@Serializable
data object Schedule

@Serializable
data object Tasks

@Serializable
data object Statistics

@Serializable
data object Settings