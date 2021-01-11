package org.lasque.tusdkvideodemo.views.cosmetic.panel.eyelash;

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
 * org.lasque.tusdkvideodemo.views.cosmetic.panel.eyelash
 * droid-sdk-video-refresh
 *
 * @author H.ys
 * @Date 2020/10/20  15:39
 * @Copyright (c) 2020 tusdk.com. All rights reserved.
 */
public class EyelashAdapter extends BaseCosmeticAdapter<CosmeticTypes.EyelashType, EyelashAdapter.EyelashViewHolder> {

    protected EyelashAdapter(List<CosmeticTypes.EyelashType> itemList, Context context) {
        super(itemList, context);
    }

    @Override
    protected EyelashViewHolder onChildCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EyelashViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_lipstrick_layout,parent,false));
    }

    @Override
    protected void onChildBindViewHolder(@NonNull final EyelashViewHolder holder, final int position, final CosmeticTypes.EyelashType item) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null){
                    mOnClickListener.onItemClick(position,holder,item);
                }
            }
        });
        Glide.with(mContext).load(item.mIconId).apply(RequestOptions.circleCropTransform()).into(holder.mIcon);
        holder.mTitle.setText(item.mTitleId);
        if (mCurrentPos == position){
            holder.mSelect.setVisibility(View.VISIBLE);
            holder.mIcon.setColorFilter(Color.parseColor("#ffcc00"));
        } else {
            holder.mSelect.setVisibility(View.GONE);
            holder.mIcon.clearColorFilter();
        }
    }

    static class EyelashViewHolder extends RecyclerView.ViewHolder{
        public ImageView mIcon;
        public TextView mTitle;
        public ImageView mSelect;

        public EyelashViewHolder(@NonNull View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.lsq_cosmetic_item_icon);
            mTitle = itemView.findViewById(R.id.lsq_cosmetic_item_title);
            mSelect = itemView.findViewById(R.id.lsq_cosmetic_item_sel);
        }
    }
}
