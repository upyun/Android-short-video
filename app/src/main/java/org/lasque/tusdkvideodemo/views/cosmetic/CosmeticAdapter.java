package org.lasque.tusdkvideodemo.views.cosmetic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.upyun.shortvideo.R;

import java.util.List;

/**
 * TuSDK
 * org.lasque.tusdkvideodemo.views.cosmetic
 * droid-sdk-video-refresh
 *
 * @author H.ys
 * @Date 2020/10/21  10:07
 * @Copyright (c) 2020 tusdk.com. All rights reserved.
 */
public class CosmeticAdapter extends BaseCosmeticAdapter<CosmeticTypes.Types, CosmeticAdapter.CosmeticViewHolder> {

    private CosmeticPanelController mController;

    private int mParentWidth;

    public CosmeticAdapter(List<CosmeticTypes.Types> itemList, Context context, CosmeticPanelController controller) {
        super(itemList, context);
        this.mController = controller;
    }

    @Override
    protected CosmeticViewHolder onChildCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_cosmetic_layout, parent, false);
        CosmeticViewHolder holder = new CosmeticViewHolder(view);
        mParentWidth = parent.getMeasuredWidth();
        mParentWidth = View.MeasureSpec.getSize(mParentWidth);
        return holder;
    }

    @Override
    protected void onChildBindViewHolder(@NonNull final CosmeticViewHolder holder, final int position, final CosmeticTypes.Types item) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null){
                    mOnClickListener.onItemClick(position,holder,item);
                }
            }
        });
        Glide.with(mContext).load(item.mIconId).into(holder.mIcon);
        holder.mTitle.setText(item.mTitleId);
        holder.mPanel.removeAllViews();
        if (mCurrentPos == position){
            View targetView = mController.getPanel(item).getPanel();
            if (targetView.getParent() != null){
                RelativeLayout layout = (RelativeLayout) targetView.getParent();
                layout.removeAllViews();
            }
            holder.mPanel.addView(targetView);
            holder.mPanel.setVisibility(View.VISIBLE);
        } else {
            holder.mPanel.setVisibility(View.GONE);
        }
    }

    public static class CosmeticViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIcon;
        public TextView mTitle;
        public RelativeLayout mPanel;

        public CosmeticViewHolder(@NonNull View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.lsq_cosmetic_item_icon);
            mTitle = itemView.findViewById(R.id.lsq_cosmetic_item_title);
            mPanel = itemView.findViewById(R.id.lsq_cosmetic_panel);
        }
    }
}
