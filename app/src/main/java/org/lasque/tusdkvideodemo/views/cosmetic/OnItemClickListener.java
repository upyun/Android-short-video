package org.lasque.tusdkvideodemo.views.cosmetic;

import androidx.recyclerview.widget.RecyclerView;

/**
 * TuSDK
 * org.lasque.tusdkdemohelper.tusdk.newUI
 * qiniu-PLDroidMediaStreamingDemo
 *
 * @author H.ys
 * @Date 2020/8/5  14:31
 * @Copyright (c) 2020 tusdk.com. All rights reserved.
 */
public interface OnItemClickListener<I,H extends RecyclerView.ViewHolder> {
    void onItemClick(int pos, H holder, I item);
}
