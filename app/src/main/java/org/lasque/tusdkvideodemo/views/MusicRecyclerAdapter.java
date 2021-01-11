package org.lasque.tusdkvideodemo.views;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.TuSdkImageView;
import com.upyun.shortvideo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 滤镜适配器
 * @author xujie
 * @Date 2018/9/18
 */

public class MusicRecyclerAdapter extends RecyclerView.Adapter<MusicRecyclerAdapter.MusicViewHolder>{

    private List<String> mMusicString;
    private int mCurrentPosition = -1;

    public interface ItemClickListener{
        void onItemClick(String musicCode, int position);
    }
    public ItemClickListener listener;

    public void setItemCilckListener(ItemClickListener listener){
        this.listener = listener;
    }

    public MusicRecyclerAdapter() {
        super();
        mMusicString = new ArrayList<>();
    }

    public void setMusicList(List<String> musicList){
        this.mMusicString = musicList;
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int position){
        this.mCurrentPosition = position;
        notifyDataSetChanged();
    }

    public List<String> getMusicList(){
        return this.mMusicString;
    }

    @Override
    public int getItemCount() {
        return mMusicString.size();
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_recycler_item_view,null);
        MusicViewHolder viewHolder = new MusicViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MusicViewHolder musicViewHolder, final int position) {
        String musicCode = mMusicString.get(position);
        musicCode = musicCode.toLowerCase();
        String musicImageName = getThumbPrefix() + musicCode;
        musicViewHolder.mImageLayout.setVisibility(View.VISIBLE);
        if(position == 0){
            musicViewHolder.mNoneLayout.setVisibility(View.VISIBLE);
            musicViewHolder.mTitleView.setVisibility(View.GONE);
            musicViewHolder.mSelectLayout.setVisibility(View.GONE);
            musicViewHolder.mImageLayout.setVisibility(View.GONE);
        }else if(position == mCurrentPosition){
            musicViewHolder.mNoneLayout.setVisibility(View.GONE);
            musicViewHolder.mTitleView.setVisibility(View.GONE);
            musicViewHolder.mSelectLayout.setVisibility(View.VISIBLE);
        }else{
            musicViewHolder.mNoneLayout.setVisibility(View.GONE);
            musicViewHolder.mTitleView.setVisibility(View.VISIBLE);
            musicViewHolder.mSelectLayout.setVisibility(View.GONE);

            musicViewHolder.mTitleView.setText(TuSdkContext.getString(getTextPrefix()+ musicCode));
        }

        if(position == 1){
            int padding = TuSdkContext.dip2px(18);
            musicViewHolder.mItemImage.setPadding(padding,padding,padding,padding);
        }else {
            musicViewHolder.mItemImage.setPadding(0,0,0,0);
        }

        musicViewHolder.mItemImage.setImageResource(TuSdkContext.getDrawableResId(musicImageName));

        // 反馈点击
        final String finalMusicCode = musicCode;
        musicViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onItemClick(getAudioPrefix()+finalMusicCode,position);
                notifyItemChanged(mCurrentPosition);
                mCurrentPosition = position;
                notifyItemChanged(position);
            }
        });
        musicViewHolder.itemView.setTag(position);
    }

    /**
     * 缩略图前缀
     *
     * @return
     */
    protected String getThumbPrefix()
    {
        return "lsq_mixing_thumb_";
    }

    /**
     * Item名称前缀
     *
     * @return
     */
    protected String getTextPrefix()
    {
        return "lsq_mixing_";
    }

    private String getAudioPrefix(){return "lsq_audio_";};

    class MusicViewHolder extends RecyclerView.ViewHolder{

        public TextView mTitleView;
        public TuSdkImageView mItemImage;
        public FrameLayout mSelectLayout;
        public FrameLayout mNoneLayout;
        public RelativeLayout mImageLayout;

        public MusicViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.lsq_item_title);
            mItemImage = itemView.findViewById(R.id.lsq_item_image);
            mSelectLayout = itemView.findViewById(R.id.lsq_select_layout);
            mNoneLayout = itemView.findViewById(R.id.lsq_none_layout);
            mImageLayout = itemView.findViewById(R.id.lsq_image_layout);
            mItemImage.setCornerRadiusDP(5);
        }
    }
}
