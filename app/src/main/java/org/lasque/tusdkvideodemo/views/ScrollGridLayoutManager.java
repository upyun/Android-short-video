package org.lasque.tusdkvideodemo.views;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;

/**
 * TuSDK
 * $desc$
 *
 * @author H.ys
 * @Date $data$ $time$
 * @Copyright (c) 2019 tusdk.com. All rights reserved.
 */
public class ScrollGridLayoutManager extends GridLayoutManager {

    private boolean isScrollEnabled = true;

    public ScrollGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public ScrollGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {

        return isScrollEnabled && super.canScrollVertically();
    }
}
