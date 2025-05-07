package com.isfandroid.pomodaily.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.color.MaterialColors
import com.isfandroid.pomodaily.R

object Helper {

    // UI - SNACKBAR
    fun showSnackbar(
        view: View,
        message: String,
        isError: Boolean,
        duration: Int = Snackbar.LENGTH_SHORT,
        actionText: String? = null,
        onActionClick: (() -> Unit)? = null
    ) {
        val actualActionText = actionText ?: "dismiss"
        val backgroundColor: Int
        val textColor: Int

        if (isError) {
            backgroundColor = MaterialColors.getColor(view, com.google.android.material.R.attr.colorError)
            textColor = MaterialColors.getColor(view, R.attr.colorOnError)
        } else {
            backgroundColor = MaterialColors.getColor(view, R.attr.colorSuccess)
            textColor = MaterialColors.getColor(view, R.attr.colorOnSuccess)
        }

        Snackbar.make(view, message, duration)
            .setBackgroundTint(backgroundColor)
            .setAction(actualActionText) {
                onActionClick?.invoke()
            }
            .setActionTextColor(textColor)
            .show()
    }
}