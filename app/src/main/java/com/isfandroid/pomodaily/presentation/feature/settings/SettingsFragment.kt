package com.isfandroid.pomodaily.presentation.feature.settings

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
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.FragmentSettingsBinding
import com.isfandroid.pomodaily.utils.Helper.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment: Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        with(binding) {
            // App Bar
            appBar.tvTitle.text = getString(R.string.txt_settings)
            appBar.btnBack.setOnClickListener { findNavController().navigateUp() }
            
            // Settings - Timer
            itemSettingsPomodoroDuration.tvTitle.text = getString(R.string.txt_pomodoro_duration)
            itemSettingsPomodoroDuration.tvValue.visibility = View.VISIBLE
            itemSettingsPomodoroDuration.root.setOnClickListener {
                showUpdateBottomSheet(
                    title = getString(R.string.txt_pomodoro_duration),
                    formTitle = getString(R.string.txt_minutes),
                    isNumber = true,
                    numValue = viewModel.pomodoroDuration.value,
                    onSubmit = {
                        viewModel.setPomodoroDuration(it as Int)
                        showSnackbar(
                            view = binding.root,
                            message = getString(R.string.txt_msg_settings_updated),
                            isError = false
                        )
                    }
                )
            }
            
            itemSettingsBreakDuration.tvTitle.text = getString(R.string.txt_break_duration)
            itemSettingsBreakDuration.tvValue.visibility = View.VISIBLE
            itemSettingsBreakDuration.root.setOnClickListener {
                showUpdateBottomSheet(
                    title = getString(R.string.txt_break_duration),
                    formTitle = getString(R.string.txt_minutes),
                    isNumber = true,
                    numValue = viewModel.breakDuration.value,
                    onSubmit = {
                        viewModel.setBreakDuration(it as Int)
                        showSnackbar(
                            view = binding.root,
                            message = getString(R.string.txt_msg_settings_updated),
                            isError = false
                        )
                    }
                )
            }
            
            itemSettingsLongBreakDuration.tvTitle.text = getString(R.string.txt_long_break_duration)
            itemSettingsLongBreakDuration.tvValue.visibility = View.VISIBLE
            itemSettingsLongBreakDuration.root.setOnClickListener {
                showUpdateBottomSheet(
                    title = getString(R.string.txt_long_break_duration),
                    formTitle = getString(R.string.txt_minutes),
                    isNumber = true,
                    numValue = viewModel.longBreakDuration.value,
                    onSubmit = {
                        viewModel.setLongBreakDuration(it as Int)
                        showSnackbar(
                            view = binding.root,
                            message = getString(R.string.txt_msg_settings_updated),
                            isError = false
                        )
                    }
                )
            }
            
            itemSettingsLongBreakInterval.tvTitle.text = getString(R.string.txt_long_break_interval)
            itemSettingsLongBreakInterval.tvValue.visibility = View.VISIBLE
            itemSettingsLongBreakInterval.root.setOnClickListener {
                showUpdateBottomSheet(
                    title = getString(R.string.txt_long_break_interval),
                    formTitle = getString(R.string.txt_minutes),
                    isNumber = true,
                    numValue = viewModel.longBreakInterval.value,
                    onSubmit = {
                        viewModel.setLongBreakInterval(it as Int)
                        showSnackbar(
                            view = binding.root,
                            message = getString(R.string.txt_msg_settings_updated),
                            isError = false
                        )
                    }
                )
            }

            itemSettingsAutoStartPomodoros.tvTitle.text = getString(R.string.txt_auto_start_pomodoros)
            itemSettingsAutoStartPomodoros.switchValue.visibility = View.VISIBLE
            itemSettingsAutoStartPomodoros.switchValue.setOnClickListener {
                viewModel.setAutoStartPomodoros(!viewModel.autoStartPomodoros.value)
            }

            itemSettingsAutoStartBreaks.tvTitle.text = getString(R.string.txt_auto_start_breaks)
            itemSettingsAutoStartBreaks.switchValue.visibility = View.VISIBLE
            itemSettingsAutoStartBreaks.switchValue.setOnClickListener {
                viewModel.setAutoStartBreaks(!viewModel.autoStartBreaks.value)
            }
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Settings - Timer
                launch {
                    viewModel.pomodoroDuration.collectLatest {
                        binding.itemSettingsPomodoroDuration.tvValue.text = getString(R.string.txt_value_minutes, it)
                    }
                }
                launch {
                    viewModel.breakDuration.collectLatest {
                        binding.itemSettingsBreakDuration.tvValue.text = getString(R.string.txt_value_minutes, it)
                    }
                }
                launch {
                    viewModel.longBreakDuration.collectLatest {
                        binding.itemSettingsLongBreakDuration.tvValue.text = getString(R.string.txt_value_minutes, it)
                    }
                }
                launch {
                    viewModel.longBreakInterval.collectLatest {
                        binding.itemSettingsLongBreakInterval.tvValue.text = it.toString()
                    }
                }
                launch {
                    viewModel.autoStartPomodoros.collectLatest {
                        binding.itemSettingsAutoStartPomodoros.switchValue.isChecked = it
                    }
                }
                launch {
                    viewModel.autoStartBreaks.collectLatest {
                        binding.itemSettingsAutoStartBreaks.switchValue.isChecked = it
                    }
                }
            }
        }
    }

    private fun showUpdateBottomSheet(
        title: String,
        formTitle: String,
        isNumber: Boolean = false,
        numValue: Int? = null,
        isMultipleChoices: Boolean = false,
        multipleChoicesItems: Array<String>? = null,
        multipleChoicesValue: String? = null,
        onSubmit: (Any) -> Unit
    ) {
        SettingsFormsBottomSheet(
            title = title,
            formTitle = formTitle,
            showNumberForm = isNumber,
            numberFormValue = numValue,
            showMultipleChoicesForm = isMultipleChoices,
            multipleChoicesFormValue = multipleChoicesValue,
            multipleChoicesItems = multipleChoicesItems,
            onSubmit = onSubmit
        ).show(childFragmentManager, SettingsFormsBottomSheet.TAG)
    }
}