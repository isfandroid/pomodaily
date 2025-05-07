package com.isfandroid.pomodaily.presentation.feature.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.FragmentSplashBinding
import com.isfandroid.pomodaily.presentation.feature.OnBoarding
import com.isfandroid.pomodaily.presentation.feature.Pomodoro
import com.isfandroid.pomodaily.utils.Constant.NAV_DESTINATION_ON_BOARDING
import com.isfandroid.pomodaily.utils.Constant.NAV_DESTINATION_POMODORO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment: Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navDestination.collectLatest {
                    when (it) {
                        NAV_DESTINATION_POMODORO -> {
                            findNavController().navigate(
                                route = Pomodoro,
                                navOptions = navOptions {
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                    anim {
                                        enter = R.anim.slide_in_right
                                        exit = R.anim.slide_out_left
                                        popEnter = R.anim.slide_in_left
                                        popExit = R.anim.slide_out_right
                                    }
                                },
                            )
                        }
                        NAV_DESTINATION_ON_BOARDING -> {
                            findNavController().navigate(
                                route = OnBoarding,
                                navOptions = navOptions {
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                    anim {
                                        enter = R.anim.slide_in_right
                                        exit = R.anim.slide_out_left
                                        popEnter = R.anim.slide_in_left
                                        popExit = R.anim.slide_out_right
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}