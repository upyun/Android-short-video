/**
 * TuSDKVideoDemo
 * MovieAlbumAdapter.java
 *
 * @author  loukang
 * @Date  Oct 9, 2017 10:43:14 AM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */
package com.upyun.shortvideo.album;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * 视频选择列表适配器
 */

public class MovieAlbumAdapter extends RecyclerView.Adapter<MovieAlbumAdapter.ViewHolder>
{
    private Context mContext;
    private LayoutInflater mInflater;
    /* 视频信息列表 */
    private List<MovieInfo> mVideoInfoList;
    /* 选中视频位置 */
    private int mSelectedPosition = -1;
    /* 上一次选中的视频位置 */
    private int mLastSelectedPosition = -1;

    /**
     *  Item点击事件
     */
    public interface OnItemClickListener
    {
        void onClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        mOnItemClickListener = onItemClickListener;
    }

    public MovieAlbumAdapter(Context context, List<MovieInfo> videoInfoList)
    {
        this.mContext = context;
        this.mVideoInfoList = videoInfoList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(mInflater.inflate(com.upyun.shortvideo.R.layout.album_select_video_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        if (mVideoInfoList == null) return;

        String path = mVideoInfoList.get(position).getPath();

        if (!TextUtils.isEmpty(path))
            Glide.with(mContext).load(path).asBitmap().into(holder.mImageView);

        int drawableId = position == mSelectedPosition ? com.upyun.shortvideo.R.drawable.lsq_video_album_picture_selected : com.upyun.shortvideo.R.drawable.lsq_video_album_picture_unselected;
        holder.mSelectorView.setBackground(mContext.getResources().getDrawable(drawableId));

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mOnItemClickListener == null) return;
                mOnItemClickListener.onClick(view, holder.getPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideoInfoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        private  ImageView mImageView;
        private  View mSelectorView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(com.upyun.shortvideo.R.id.lsq_video_thumb_view);
            mSelectorView = itemView.findViewById(com.upyun.shortvideo.R.id.lsq_movie_selected_icon);
        }
    }

    /**
     * 更新选中位置的选择图标
     */
    public void updateSelectedVideoPosition(int position)
    {
        if (mVideoInfoList != null  && position >= 0)
        {
            this.mLastSelectedPosition = this.mSelectedPosition;
            this.mSelectedPosition = position;
            notifyItemChanged(mLastSelectedPosition);
            notifyItemChanged(mSelectedPosition);
        }
    }

    /**
     * 获取选中的视频信息
     *
     * @return
     */
    public MovieInfo getSelectedVideoInfo()
    {
        if (mSelectedPosition < 0 || mVideoInfoList == null) return null;

        MovieInfo videoInfo = null;
        videoInfo = mVideoInfoList.get(mSelectedPosition);
        return videoInfo;
    }
}
