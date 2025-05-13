package com.isfandroid.pomodaily.presentation.feature.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.isNotEmpty
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.ActivityMainBinding
import com.isfandroid.pomodaily.presentation.feature.onboarding.OnBoardingContainerFragment
import com.isfandroid.pomodaily.presentation.feature.pomodoro.PomodoroFragment
import com.isfandroid.pomodaily.presentation.feature.splash.SplashFragment
import com.isfandroid.pomodaily.presentation.feature.task.TasksFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val ACTION_NAVIGATE_TO_POMODORO = "ACTION_NAVIGATE_TO_POMODORO"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean? ->
        if (!isGranted!!)
            Toast.makeText(
                this,
                "Unable to display Foreground service notification due to permission decline",
                Toast.LENGTH_LONG
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

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
        }
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
}

// SCREENS
@Serializable
data object Splash

@Serializable
data object OnBoarding

@Serializable
data object Pomodoro

@Serializable
data object Tasks