package org.lasque.tusdkvideodemo.views;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.tusdk.TuSDKMediaTransitionWrap;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.view.TuSdkImageView;
import com.upyun.shortvideo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景特效适配器
 *
 * @author xujie
 * @Date 2018/9/18
 */

public class TransitionsRecyclerAdapter extends RecyclerView.Adapter<TransitionsRecyclerAdapter.TransitionsViewHolder> {

    private List<TuSDKMediaTransitionWrap.TuSDKMediaTransitionType> mTransitionTypes;
    private int mCurrentPosition = -1;
    private boolean isCanDeleted = false;

    public interface ItemClickListener {
        void onItemClick(int position,TransitionsViewHolder ScreenViewHolder);
    }

    public interface OnItemTouchListener{
        void onItemTouch(MotionEvent event, int position, TransitionsViewHolder ScreenViewHolder);
    }

    public ItemClickListener listener;
    public OnItemTouchListener onItemTouchListener;

    public void setItemCilckListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener){
        this.onItemTouchListener = onItemTouchListener;
    }

    public TransitionsRecyclerAdapter() {
        super();
        mTransitionTypes = new ArrayList<>();
    }

    public void setSceneList(List<TuSDKMediaTransitionWrap.TuSDKMediaTransitionType> ScreenList) {
        this.mTransitionTypes = ScreenList;
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int position) {
        this.mCurrentPosition = position;
        notifyDataSetChanged();
    }

    public boolean isCanDeleted(){
        return isCanDeleted;
    }

    public void setCanDeleted(boolean canDeleted){
        this.isCanDeleted = canDeleted;
    }

    public List<TuSDKMediaTransitionWrap.TuSDKMediaTransitionType> getScreenList() {
        return this.mTransitionTypes;
    }

    @Override
    public int getItemCount() {
        return mTransitionTypes.size() +1;
    }

    @Override
    public TransitionsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.screen_recycler_item_view, null);
        TransitionsViewHolder viewHolder = new TransitionsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TransitionsViewHolder ScreenViewHolder, final int position) {
        ScreenViewHolder.mImageLayout.setVisibility(View.VISIBLE);
        if (position > 0){
            TuSDKMediaTransitionWrap.TuSDKMediaTransitionType screenCode = mTransitionTypes.get(position-1);
            String screenImageName = getThumbPrefix() + String.valueOf(screenCode).replace("TuSDKMediaTransitionType","").toLowerCase();
            ScreenViewHolder.mNoneLayout.setVisibility(View.GONE);
            ScreenViewHolder.mTitleView.setVisibility(View.VISIBLE);
            ScreenViewHolder.mSelectLayout.setVisibility(View.VISIBLE);
            ScreenViewHolder.mTitleView.setText(TuSdkContext.getString(getTextPrefix() + String.valueOf(screenCode).replace("TuSDKMediaTransitionType","")));
            TLog.e("screenImageName : %s", screenImageName);
            int screenId = TuSdkContext.getDrawableResId(screenImageName);
//            TransitionsViewHolder.mThumbImageView.setImageResource(screenId);
            //设置图片圆角角度
            RoundedCorners roundedCorners= new RoundedCorners(TuSdkContext.dip2px(8));
            RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override( ScreenViewHolder.mItemImage.getWidth(), ScreenViewHolder.mItemImage.getHeight());
            Glide.with(ScreenViewHolder.mItemImage.getContext()).asBitmap().load(screenId).apply(options).into(ScreenViewHolder.mItemImage);
        } else {
            ScreenViewHolder.mNoneLayout.setVisibility(View.VISIBLE);
            ScreenViewHolder.mTitleView.setVisibility(View.GONE);
            ScreenViewHolder.mSelectLayout.setVisibility(View.GONE);
            ScreenViewHolder.mImageLayout.setVisibility(View.GONE);
            ScreenViewHolder.mNoneLayout.setAlpha(isCanDeleted ? 1 : 0.3f);
        }
        // 反馈点击
        ScreenViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick(position,ScreenViewHolder);

            }
        });

        ScreenViewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(onItemTouchListener == null) return false;
                onItemTouchListener.onItemTouch(event,position,ScreenViewHolder);
                return false;
            }
        });

        ScreenViewHolder.itemView.setTag(position);
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

    public TuSDKMediaTransitionWrap.TuSDKMediaTransitionType getTransitionType(int position){
        if(mTransitionTypes == null && mTransitionTypes.size() + 1 < position || position == 0)return null;
        return mTransitionTypes.get(position -1);
    }

    /**
     *
     */
    public class TransitionsViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleView;
        public TuSdkImageView mItemImage;
        public RoundedImageView mSelectLayout;
        public FrameLayout mNoneLayout;
        public RelativeLayout mImageLayout;

        public TransitionsViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.lsq_item_title);
            mItemImage = itemView.findViewById(R.id.lsq_item_image);
            mSelectLayout = itemView.findViewById(R.id.lsq_select_layout);
            mNoneLayout = itemView.findViewById(R.id.lsq_none_layout);
            mImageLayout = itemView.findViewById(R.id.lsq_image_layout);
        }
    }
}
