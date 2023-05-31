package com.photoshooto.ui.job.utility

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class
SpacesItemDecoration(
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
        if (!isGridLayoutManager) {
            outRect.bottom = space
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space
                outRect.bottom = space
            } else {
                outRect.top = 0
                outRect.bottom = space
            }

            if (parent.getChildLayoutPosition(view) == state.itemCount - 1) {
                outRect.bottom = space * 2
            }
        } else {
            outRect.left = space / 2
            outRect.right = space / 2
            outRect.bottom = space

            if (state.itemCount > 6) {
                if (state.itemCount % 2 == 0) {
                    if (parent.getChildLayoutPosition(view) == state.itemCount - 1) {
                        outRect.bottom = space * 6
                    }
                    if (parent.getChildLayoutPosition(view) == state.itemCount - 2) {
                        outRect.bottom = space * 6
                    }
                } else {
                    if (parent.getChildLayoutPosition(view) == state.itemCount - 1) {
                        outRect.bottom = space * 6
                    }
                }
            }
        }
    }
}