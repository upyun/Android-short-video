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
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import com.upyun.shortvideo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xujie
 * @Date 2018/9/27
 */

public class MvRecyclerAdapter extends RecyclerView.Adapter<MvRecyclerAdapter.MvViewHolder> {

    private List<StickerGroup> mMvStickerGroups;
    private int mCurrentPosition = -1;
    private boolean isMV = true;

    public interface ItemClickListener{
        void onItemClick(int position);
    }
    public MvRecyclerAdapter.ItemClickListener listener;

    public void setItemClickListener(MvRecyclerAdapter.ItemClickListener listener){
        this.listener = listener;
    }

    public MvRecyclerAdapter(){
        this(true);
    }

    public MvRecyclerAdapter(boolean isMV) {
        super();
        this.isMV = isMV;
        mMvStickerGroups = new ArrayList<>();
    }

    public void setMvModeList(List<StickerGroup> modeList){
        this.mMvStickerGroups = modeList;
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int position){
        this.mCurrentPosition = position;
        notifyDataSetChanged();
    }

    public List<StickerGroup> getMvModeList(){
        return this.mMvStickerGroups;
    }

    @Override
    public int getItemCount() {
        return mMvStickerGroups.size();
    }

    @Override
    public MvViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lsq_filter_recycler_item_view,null);
        MvViewHolder viewHolder = new MvViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MvViewHolder mvViewHolder, final int position) {
        StickerGroup stickerGroup = mMvStickerGroups.get(position);
        mvViewHolder.mImageLayout.setVisibility(View.VISIBLE);
        if(position == 0 && isMV){
            mvViewHolder.mNoneLayout.setVisibility(View.VISIBLE);
            mvViewHolder.mTitleView.setVisibility(View.GONE);
            mvViewHolder.mSelectLayout.setVisibility(View.GONE);
            mvViewHolder.mImageLayout.setVisibility(View.GONE);
        }else if(position == mCurrentPosition){
            mvViewHolder.mNoneLayout.setVisibility(View.GONE);
            mvViewHolder.mTitleView.setVisibility(View.GONE);
            mvViewHolder.mSelectLayout.setVisibility(View.VISIBLE);
        }else{
            mvViewHolder.mNoneLayout.setVisibility(View.GONE);
            mvViewHolder.mTitleView.setVisibility(View.VISIBLE);
            mvViewHolder.mSelectLayout.setVisibility(View.GONE);

            mvViewHolder.mTitleView.setText(TuSdkContext.getString("lsq_mv_"+ stickerGroup.name));
        }
        mvViewHolder.mItemImage.setImageBitmap(null);
        StickerLocalPackage.shared().loadGroupThumb(stickerGroup, mvViewHolder.mItemImage);
        // 反馈点击
        mvViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onItemClick(position);

                notifyItemChanged(mCurrentPosition);
                mCurrentPosition = position;
                notifyItemChanged(position);
            }
        });
        mvViewHolder.itemView.setTag(position);
    }

    class MvViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitleView;
        public TuSdkImageView mItemImage;
        public FrameLayout mSelectLayout;
        public FrameLayout mNoneLayout;
        public RelativeLayout mImageLayout;
        public MvViewHolder(View itemView) {
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
