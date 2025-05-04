package com.isfandroid.pomodaily.presentation.feature.onboarding

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
import androidx.viewpager2.widget.ViewPager2
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.FragmentOnBoardingContainerBinding
import com.isfandroid.pomodaily.presentation.common.adapter.ViewPagerFragmentsAdapter
import com.isfandroid.pomodaily.presentation.feature.Pomodoro
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnBoardingContainerFragment: Fragment() {

    private var _binding: FragmentOnBoardingContainerBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<OnBoardingViewModel>()

    private lateinit var pageChangeCallback: ViewPager2.OnPageChangeCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.vpContainer.unregisterOnPageChangeCallback(pageChangeCallback)
        _binding = null
    }

    private fun initViews() {
        val fragments = listOf(
            OnBoardingFragment(1),
            OnBoardingFragment(2),
            OnBoardingFragment(3),
        )
        val adapter = ViewPagerFragmentsAdapter(
            fragments,
            this
        )

        with(binding) {
            pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    when (position) {
                        // OnBoarding Start
                        0 -> {
                            viewDotIndicatorStart.setBackgroundResource(R.drawable.oval_primary)
                            viewDotIndicatorMiddle.setBackgroundResource(R.drawable.oval_onsurface)
                            viewDotIndicatorEnd.setBackgroundResource(R.drawable.oval_onsurface)

                            btnSkip.visibility = View.VISIBLE
                            btnGetStarted.visibility = View.INVISIBLE
                        }
                        // OnBoarding End
                        fragments.lastIndex -> {
                            viewDotIndicatorStart.setBackgroundResource(R.drawable.oval_onsurface)
                            viewDotIndicatorMiddle.setBackgroundResource(R.drawable.oval_onsurface)
                            viewDotIndicatorEnd.setBackgroundResource(R.drawable.oval_primary)

                            btnSkip.visibility = View.INVISIBLE
                            btnGetStarted.visibility = View.VISIBLE
                        }
                        // OnBoarding Middle
                        else -> {
                            viewDotIndicatorStart.setBackgroundResource(R.drawable.oval_onsurface)
                            viewDotIndicatorMiddle.setBackgroundResource(R.drawable.oval_primary)
                            viewDotIndicatorEnd.setBackgroundResource(R.drawable.oval_onsurface)

                            btnSkip.visibility = View.VISIBLE
                            btnGetStarted.visibility = View.INVISIBLE
                        }
                    }
                }
            }
            vpContainer.adapter = adapter
            vpContainer.registerOnPageChangeCallback(pageChangeCallback)

            btnSkip.setOnClickListener {
                viewModel.finishOnBoarding()
            }

            btnGetStarted.setOnClickListener {
                viewModel.finishOnBoarding()
            }
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isOnBoardingFinished.collectLatest {
                    if (it) {
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
                }
            }
        }
    }
}