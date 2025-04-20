package com.isfandroid.pomodaily.presentation.feature

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.ActivityMainBinding
import com.isfandroid.pomodaily.presentation.feature.onboarding.OnBoardingContainerFragment
import com.isfandroid.pomodaily.presentation.feature.splash.SplashFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Navigation Components
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.graph = navController.createGraph(
            startDestination = Splash
        ) {
            fragment<SplashFragment, Splash> {
                label = getString(R.string.txt_splash)
            }
            fragment<OnBoardingContainerFragment, OnBoarding> {
                label = getString(R.string.txt_on_boarding)
            }
        }
    }
}

// SCREENS
@Serializable
data object Splash

@Serializable
data object OnBoarding