package org.lasque.tusdkvideodemo.views.cosmetic;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * TuSDK
 * org.lasque.tusdkvideodemo.views.cosmetic
 * droid-sdk-video-refresh
 *
 * @author H.ys
 * @Date 2020/10/16  16:12
 * @Copyright (c) 2020 tusdk.com. All rights reserved.
 */
public abstract class BaseCosmeticAdapter<T,H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {

    protected List<T> mItemList;
    protected Context mContext;
    protected OnItemClickListener<T,H> mOnClickListener;
    protected int mCurrentPos = -1;

    protected abstract H onChildCreateViewHolder(@NonNull ViewGroup parent, int viewType);
    protected abstract void onChildBindViewHolder(@NonNull H holder, int position, T item);

    protected BaseCosmeticAdapter(List<T> itemList,Context context){
        this.mItemList = itemList;
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener<T,H> onItemClickListener){
        this.mOnClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onChildCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull H holder, int position) {
        onChildBindViewHolder(holder,position,mItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public int getCurrentPos(){
        return mCurrentPos;
    }

    public void setCurrentPos(int pos){
        int lastPos = mCurrentPos;
        notifyItemChanged(lastPos);
        mCurrentPos = pos;
        if (mCurrentPos != -1){
            notifyItemChanged(mCurrentPos);
        }
    }
}
