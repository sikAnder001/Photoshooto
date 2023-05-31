package com.photoshooto.ui.job.utility

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class HorizontalSpacesItemDecoration(
    private val space: Int,
    private val isGridLayoutManager: Boolean = false,
    @RecyclerView.Orientation private val orientation: Int = RecyclerView.VERTICAL
) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.left = space
            outRect.right = space / 2
        } else {
            outRect.left = space / 2
            outRect.right = space / 2
        }

        if (parent.getChildLayoutPosition(view) == state.itemCount - 1) {
            outRect.right = space
        }
    }
}