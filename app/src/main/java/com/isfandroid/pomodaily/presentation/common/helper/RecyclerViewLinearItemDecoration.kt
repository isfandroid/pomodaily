package com.isfandroid.pomodaily.presentation.common.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewLinearItemDecoration(
    private val topSpace: Int,
    private val bottomSpace: Int,
    private val rightSpace: Int,
    private val leftSpace: Int,
    private val addTopSpacingForFirstItem: Boolean = true,
    private val addBottomSpacingForLastItem: Boolean = true,
    private val addRightSpacingForLastItem: Boolean = true,
    private val addLeftSpacingForFirstItem: Boolean = true,
): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)
        val lastPosition = parent.adapter?.itemCount?.minus(1)
        if (position == RecyclerView.NO_POSITION) return

        outRect.top = if (!addTopSpacingForFirstItem && position == 0) 0 else topSpace
        outRect.bottom = if (!addBottomSpacingForLastItem && position == (lastPosition ?: -1)) 0 else bottomSpace
        outRect.right = if (!addRightSpacingForLastItem && position == (lastPosition ?: -1)) 0 else rightSpace
        outRect.left = if (!addLeftSpacingForFirstItem && position == 0) 0 else leftSpace
    }
}