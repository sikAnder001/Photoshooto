package com.photoshooto.ui.job.utility;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public abstract class EndlessPaginationScrollListener extends RecyclerView.OnScrollListener {

    private final int LAYOUT_MANAGER_TYPE_LINEAR = 0;
    private final int LAYOUT_MANAGER_TYPE_STAGGERED_GRID = 1;
    public int PAGINATION_PAGE_SIZE = 4;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private int mLayoutType;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (null == mLayoutManager) {
            this.mLayoutManager = recyclerView.getLayoutManager();
            if (mLayoutManager instanceof LinearLayoutManager) {
                mLayoutType = LAYOUT_MANAGER_TYPE_LINEAR;
                mLinearLayoutManager = (LinearLayoutManager) mLayoutManager;
                mLayoutManager.setItemPrefetchEnabled(false);
            } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                mLayoutType = LAYOUT_MANAGER_TYPE_STAGGERED_GRID;
                mStaggeredGridLayoutManager = (StaggeredGridLayoutManager) mLayoutManager;
                mStaggeredGridLayoutManager.setItemPrefetchEnabled(false);
            }
        }

        onScrolledUpOrDown(dy > 0);

        int visibleItemCount = mLayoutManager.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();

        int firstVisibleItemPosition;

        switch (mLayoutType) {

            case LAYOUT_MANAGER_TYPE_LINEAR:

                firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();

                if ((firstVisibleItemPosition + visibleItemCount) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGINATION_PAGE_SIZE
                ) {
                    requestNewPage();
                }
                break;

            case LAYOUT_MANAGER_TYPE_STAGGERED_GRID:

                int[] firstVisibleItemPositions = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(null);

                firstVisibleItemPosition = firstVisibleItemPositions[0];

                if ((firstVisibleItemPosition + visibleItemCount) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGINATION_PAGE_SIZE
                ) {
                    requestNewPage();
                }
                break;

        }
    }

    protected void requestNewPage() {

    }

    protected void onScrolledUpOrDown(boolean toUp) {

    }
}
