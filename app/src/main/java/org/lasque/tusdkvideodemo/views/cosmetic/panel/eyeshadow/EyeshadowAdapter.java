package org.lasque.tusdkvideodemo.views.cosmetic.panel.eyeshadow;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.views.cosmetic.BaseCosmeticAdapter;
import org.lasque.tusdkvideodemo.views.cosmetic.CosmeticTypes;

import java.util.List;

/**
 * TuSDK
 * org.lasque.tusdkvideodemo.views.cosmetic.panel.eyeshadow
 * droid-sdk-video-refresh
 *
 * @author H.ys
 * @Date 2020/10/20  17:09
 * @Copyright (c) 2020 tusdk.com. All rights reserved.
 */
class EyeshadowAdapter extends BaseCosmeticAdapter<CosmeticTypes.EyeshadowType, EyeshadowAdapter.EyeshadowViewHolder> {

    protected EyeshadowAdapter(List<CosmeticTypes.EyeshadowType> itemList, Context context) {
        super(itemList, context);
    }

    @Override
    protected EyeshadowViewHolder onChildCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EyeshadowViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_lipstrick_layout, parent,false));
    }

    @Override
    protected void onChildBindViewHolder(@NonNull final EyeshadowViewHolder holder, final int position, final CosmeticTypes.EyeshadowType item) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null){
                    mOnClickListener.onItemClick(position,holder,item);
                }
            }
        });
        Glide.with(mContext).load(
                item.mIconId
        ).apply(RequestOptions.circleCropTransform()).into(holder.mIcon);
        holder.mTitle.setText(item.mTitleId);
        if (mCurrentPos == position) {
            holder.mSelect.setVisibility(View.VISIBLE);
            holder.mIcon.setColorFilter(Color.parseColor("#ffcc00"));
        } else {
            holder.mSelect.setVisibility(View.GONE);
            holder.mIcon.clearColorFilter();
        }
    }

    public static class EyeshadowViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mTitle;
        public ImageView mSelect;

        public EyeshadowViewHolder(@NonNull View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.lsq_cosmetic_item_icon);
            mTitle = itemView.findViewById(R.id.lsq_cosmetic_item_title);
            mSelect = itemView.findViewById(R.id.lsq_cosmetic_item_sel);
        }
    }
}
