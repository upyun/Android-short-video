/**
 * TuSDKVideoDemo
 * MovieAlbumAdapter.java
 *
 * @author  loukang
 * @Date  Oct 9, 2017 10:43:14 AM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 *
 */
package org.lasque.tusdkvideodemo.album;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.media.codec.suit.mutablePlayer.AVAssetFile;
import com.upyun.shortvideo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频选择列表适配器
 */

public class MovieAlbumAdapter extends RecyclerView.Adapter<MovieAlbumAdapter.ViewHolder>
{
    /* 最小视频时长(单位：ms) */
    private static int MIN_VIDEO_DURATION = 3000;
    private Context mContext;
    private LayoutInflater mInflater;
    /* 视频信息列表 */
    private List<MovieInfo> mVideoInfoList;
    /* 选中视频位置 */
    private int mSelectedPosition = -1;
    /* 上一次选中的视频位置 */
    private int mLastSelectedPosition = -1;
    /* 已选视频 */
    private List<MovieInfo> selectMovieInfos;
    /* 最多选择数量 */
    private int mSelectMax = 1;

    /**
     *  Item点击事件
     */
    public interface OnItemClickListener
    {
        void onSelectClick(View view, int position);
        void onClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        mOnItemClickListener = onItemClickListener;
    }

    public MovieAlbumAdapter(Context context, List<MovieInfo> videoInfoList, int selectMax)
    {
        this.mContext = context;
        this.mVideoInfoList = videoInfoList;
        this.mSelectMax = selectMax;
        selectMovieInfos = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(mInflater.inflate(R.layout.lsq_album_select_video_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        if (mVideoInfoList == null) return;

        String path = mVideoInfoList.get(position).getPath();

        if (!TextUtils.isEmpty(path))
            Glide.with(mContext).asBitmap().load(path).into(holder.mImageView);

        int drawableId = selectMovieInfos.contains(mVideoInfoList.get(position)) ? R.drawable.edit_heckbox_sel : R.drawable.edit_heckbox_unsel;
        holder.mSelectorView.setBackground(mContext.getResources().getDrawable(drawableId));
        holder.mSelectorView.setText(selectMovieInfos.indexOf(mVideoInfoList.get(position)) >= 0 ? String.valueOf(selectMovieInfos.indexOf(mVideoInfoList.get(position)) + 1) : "");
        holder.mTimeView.setText(String.format("%02d:%02d",mVideoInfoList.get(position).getDuration() / 1000 / 60,mVideoInfoList.get(position).getDuration() / 1000 % 60));

        holder.mSelectorViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onSelectClick(view, holder.getPosition());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mOnItemClickListener != null)
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
        private FrameLayout mSelectorViewLayout;
        private TextView mSelectorView;
        private TextView mTimeView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.lsq_video_thumb_view);
            mSelectorViewLayout = (FrameLayout)itemView.findViewById(R.id.lsq_movie_selected_icon_layout);
            mSelectorView = (TextView)itemView.findViewById(R.id.lsq_movie_selected_icon);
            mTimeView = (TextView)itemView.findViewById(R.id.lsq_movie_time);
        }
    }

    /**
     * 更新选中位置的选择图标
     */
    public void updateSelectedVideoPosition(int position)
    {
        if (mVideoInfoList != null  && position >= 0)
        {
            MovieInfo movieInfo = mVideoInfoList.get(position);
            if(mSelectMax == 1)
            {
                // 时间限制
                if(movieInfo.getDuration() < MIN_VIDEO_DURATION){
                    TuSdk.messageHub().showToast(mContext, R.string.lsq_album_select_min_time);
                    return;
                }
                // 不可重复选择
                if(mSelectedPosition == position)
                    return;
                this.mLastSelectedPosition = this.mSelectedPosition;
                this.mSelectedPosition = position;

                if(selectMovieInfos.contains(movieInfo))
                {
                    selectMovieInfos.remove(movieInfo);
                }
                else
                {
                    selectMovieInfos.clear();
                    selectMovieInfos.add(movieInfo);
                }

                notifyItemChanged(mLastSelectedPosition);
                notifyItemChanged(mSelectedPosition);
            }
            else
            {
                if(selectMovieInfos.contains(mVideoInfoList.get(position))){
                    selectMovieInfos.remove(mVideoInfoList.get(position));
                }
                else
                {
                    if(selectMovieInfos.size() >= mSelectMax)
                    {
                        TuSdk.messageHub().showToast(mContext, R.string.lsq_select_video_max);
                        return;
                    }
                    AVAssetFile assetFile = new AVAssetFile(new File(mVideoInfoList.get(position).getPath()));
                    if(assetFile.createExtractor().getTrackCount() <= 1){
                        TuSdk.messageHub().showToast(mContext, R.string.lsq_select_include_audio);
                        return;
                    }
                    selectMovieInfos.add(mVideoInfoList.get(position));

                }

                notifyItemChanged(position);

                for (MovieInfo info:selectMovieInfos) {
                    notifyItemChanged(mVideoInfoList.indexOf(info));
                }
            }
        }
    }

    /**
     * 获取选中的视频信息
     *
     * @return
     */
    public List<MovieInfo> getSelectedVideoInfo()
    {
        return selectMovieInfos;
    }

    public List<MovieInfo> getVideoInfoList(){
        return mVideoInfoList;
    }

    public void setVideoInfoList(List<MovieInfo> videoInfoList){
        this.mVideoInfoList = videoInfoList;
        selectMovieInfos.clear();
        notifyDataSetChanged();
    }

}
