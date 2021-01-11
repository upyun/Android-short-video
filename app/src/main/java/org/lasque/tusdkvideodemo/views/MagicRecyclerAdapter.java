package org.lasque.tusdkvideodemo.views;

import android.graphics.Bitmap;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
 *
 * @author xujie
 * @Date 2018/9/18
 */

public class MagicRecyclerAdapter extends RecyclerView.Adapter<MagicRecyclerAdapter.MagicViewHolder> {

    private List<String> mMagicString;
    private int mCurrentPosition = -1;
    private boolean isCanDeleted = false;

    public interface ItemClickListener {
        void onItemClick(int position,MagicViewHolder MagicViewHolder);
    }

    public interface OnItemTouchListener{
        void onItemTouch(MotionEvent event, int position, MagicViewHolder MagicViewHolder);
    }

    public ItemClickListener listener;
    public OnItemTouchListener onItemTouchListener;

    public void setItemCilckListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener){
        this.onItemTouchListener = onItemTouchListener;
    }

    public MagicRecyclerAdapter() {
        super();
        mMagicString = new ArrayList<>();
    }

    public void setMagicList(List<String> MagicList) {
        this.mMagicString = MagicList;
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int position) {
        this.mCurrentPosition = position;
        notifyDataSetChanged();
    }

    public void setCanDeleted(boolean isCanDeleted){
        this.isCanDeleted = isCanDeleted;
    }

    public boolean isCanDeleted(){
        return isCanDeleted;
    }

    public List<String> getMagicList() {
        return this.mMagicString;
    }

    @Override
    public int getItemCount() {
        return mMagicString.size();
    }

    @Override
    public MagicViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lsq_magic_recycler_item_view, null);
        MagicViewHolder viewHolder = new MagicViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MagicViewHolder magicViewHolder, final int position) {
        String magicCode = mMagicString.get(position);
        magicCode = magicCode.toLowerCase();
        String magicImageName = getThumbPrefix() + magicCode;
        magicViewHolder.mImageLayout.setVisibility(View.VISIBLE);
        if (position == 0) {
            magicViewHolder.mNoneLayout.setVisibility(View.VISIBLE);
            magicViewHolder.mTitleView.setVisibility(View.GONE);
            magicViewHolder.mSelectLayout.setVisibility(View.GONE);
            magicViewHolder.mImageLayout.setVisibility(View.GONE);
            magicViewHolder.mNoneLayout.setAlpha(isCanDeleted()? 1:0.3f);
        } else if (position == mCurrentPosition) {
            magicViewHolder.mNoneLayout.setVisibility(View.GONE);
            magicViewHolder.mTitleView.setVisibility(View.GONE);
            magicViewHolder.mSelectLayout.setVisibility(View.VISIBLE);
        } else {
            magicViewHolder.mNoneLayout.setVisibility(View.GONE);
            magicViewHolder.mTitleView.setVisibility(View.VISIBLE);
            magicViewHolder.mSelectLayout.setVisibility(View.GONE);

            magicViewHolder.mTitleView.setText(TuSdkContext.getString(getTextPrefix() + magicCode));
        }

        Bitmap filterImage = TuSdkContext.getRawBitmap(magicImageName);
        if (filterImage != null)
        {
            magicViewHolder.mItemImage.setImageBitmap(filterImage);
        }


        // 反馈点击
        magicViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onItemClick(position,magicViewHolder);
                notifyItemChanged(mCurrentPosition);
                notifyItemChanged(position);
                if(position == 0)return;
                mCurrentPosition = position;
            }
        });

        magicViewHolder.itemView.setTag(position);
    }

    /**
     * 缩略图前缀
     *
     * @return
     */
    protected String getThumbPrefix() {
        return "lsq_filter_thumb_";
    }

    /**
     * Item名称前缀
     *
     * @return
     */
    protected String getTextPrefix() {
        return "lsq_filter_";
    }

    public String getMagicCode(int position){
        if(mMagicString == null && mMagicString.size() < position)return "None";
        return mMagicString.get(position);
    }
    public class MagicViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleView;
        public TuSdkImageView mItemImage;
        public FrameLayout mSelectLayout;
        public FrameLayout mNoneLayout;
        public RelativeLayout mImageLayout;

        public MagicViewHolder(View itemView) {
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
