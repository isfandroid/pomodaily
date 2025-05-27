package com.isfandroid.pomodaily.presentation.feature

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import com.isfandroid.pomodaily.presentation.MainViewModel
import com.isfandroid.pomodaily.presentation.feature.onboarding.OnBoardingContainerFragment
import com.isfandroid.pomodaily.presentation.feature.pomodoro.PomodoroFragment
import com.isfandroid.pomodaily.presentation.feature.schedule.ScheduleFragment
import com.isfandroid.pomodaily.presentation.feature.settings.SettingsFragment
import com.isfandroid.pomodaily.presentation.feature.stats.StatsFragment
import com.isfandroid.pomodaily.presentation.feature.task.TasksFragment
import com.isfandroid.pomodaily.utils.Constant.NAV_DESTINATION_ON_BOARDING
import com.isfandroid.pomodaily.utils.Constant.NAV_DESTINATION_POMODORO
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navDestination.collectLatest {
                    println("COLLECTED")
                    when (it) {
                        NAV_DESTINATION_POMODORO -> setupNavGraph(Pomodoro)
                        NAV_DESTINATION_ON_BOARDING -> setupNavGraph(OnBoarding)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun setupNavGraph(startDestination: Any) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.graph = navController.createGraph(
            startDestination = startDestination
        ) {
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
}

fun Activity.openAppSettings() {
    Intent(
        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

// SCREENS
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