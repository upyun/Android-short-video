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
import org.lasque.tusdk.core.utils.sqllite.ImageSqlInfo;
import com.upyun.shortvideo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频选择列表适配器
 */

public class ImageAlbumAdapter extends RecyclerView.Adapter<ImageAlbumAdapter.ViewHolder>
{
    private Context mContext;
    private LayoutInflater mInflater;
    /* 相册信息列表 */
    private List<ImageSqlInfo> mImageSqlInfoList;
    /* 选中视频位置 */
    private int mSelectedPosition = -1;
    /* 上一次选中的视频位置 */
    private int mLastSelectedPosition = -1;
    /* 已选视频 */
    private List<ImageSqlInfo> selectImageSqlInfo;
    /* 最多选择数量 */
    private int mSelectMax = 1;

    /**
     *  Item点击事件
     */
    public interface OnItemClickListener
    {
        void onSelectClick(View view, ImageSqlInfo item, int position);
        void onClick(View view, ImageSqlInfo item, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        mOnItemClickListener = onItemClickListener;
    }

    public ImageAlbumAdapter(Context context, List<ImageSqlInfo> videoInfoList, int selectMax)
    {
        this.mContext = context;
        this.mImageSqlInfoList = videoInfoList;
        this.mSelectMax = selectMax;
        selectImageSqlInfo = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(mInflater.inflate(R.layout.lsq_album_select_video_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        if (mImageSqlInfoList == null) return;

        String path = mImageSqlInfoList.get(position).path;

        if (!TextUtils.isEmpty(path))
            Glide.with(mContext).load(path).into(holder.mImageView);

        int drawableId = selectImageSqlInfo.contains(mImageSqlInfoList.get(position)) ? R.drawable.edit_heckbox_sel : R.drawable.edit_heckbox_unsel;
        holder.mSelectorView.setBackground(mContext.getResources().getDrawable(drawableId));
        holder.mSelectorView.setText(selectImageSqlInfo.indexOf(mImageSqlInfoList.get(position)) >= 0 ? String.valueOf(selectImageSqlInfo.indexOf(mImageSqlInfoList.get(position)) + 1) : "");
        holder.mTimeView.setVisibility(View.GONE);

        holder.mSelectorViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onSelectClick(view, mImageSqlInfoList.get(position),holder.getPosition());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onClick(view,mImageSqlInfoList.get(position), holder.getPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageSqlInfoList.size();
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
    public void updateSelectedImagePosition(int position)
    {
        if (mImageSqlInfoList != null  && position >= 0)
        {
            ImageSqlInfo imageSqlInfo = mImageSqlInfoList.get(position);
            if(mSelectMax == 1)
            {
                // 不可重复选择
                if(mSelectedPosition == position)
                    return;
                this.mLastSelectedPosition = this.mSelectedPosition;
                this.mSelectedPosition = position;

                if(selectImageSqlInfo.contains(imageSqlInfo))
                {
                    selectImageSqlInfo.remove(imageSqlInfo);
                }
                else
                {
                    selectImageSqlInfo.clear();
                    selectImageSqlInfo.add(imageSqlInfo);
                }

                notifyItemChanged(mLastSelectedPosition);
                notifyItemChanged(mSelectedPosition);
            }
            else
            {
                if(selectImageSqlInfo.contains(mImageSqlInfoList.get(position))){
                    selectImageSqlInfo.remove(mImageSqlInfoList.get(position));
                }
                else
                {
                    if(selectImageSqlInfo.size() >= mSelectMax)
                    {
                        TuSdk.messageHub().showToast(mContext, R.string.lsq_select_video_max);
                        return;
                    }
                    selectImageSqlInfo.add(mImageSqlInfoList.get(position));
                }

                notifyItemChanged(position);

                for (ImageSqlInfo info: selectImageSqlInfo) {
                    notifyItemChanged(mImageSqlInfoList.indexOf(info));
                }
            }
        }
    }

    /**
     * 获取选中的视频信息
     *
     * @return
     */
    public List<ImageSqlInfo> getSelectedVideoInfo()
    {
        return selectImageSqlInfo;
    }

    public List<ImageSqlInfo> getVideoInfoList(){
        return mImageSqlInfoList;
    }

    public void setVideoInfoList(List<ImageSqlInfo> videoInfoList){
        this.mImageSqlInfoList = videoInfoList;
        notifyDataSetChanged();
    }

}
