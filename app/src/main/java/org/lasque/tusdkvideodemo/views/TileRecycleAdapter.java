package org.lasque.tusdkvideodemo.views;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lasque.tusdk.core.view.TuSdkImageView;
import com.upyun.shortvideo.R;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2019/3/5 19:01
 * @Copright (c) 2019 tusdk.com. All rights reserved.
 * <p>
 * 贴纸适配器
 */
public class TileRecycleAdapter extends RecyclerView.Adapter<TileRecycleAdapter.TileViewHolder> {
    /** 贴纸图片 **/
    private int[] images = { R.drawable.sticker_10342,R.drawable.sticker_10344, R.drawable.sticker_10345, R.drawable.sticker_10346,R.drawable.sticker_10348,R.drawable.sticker_10347,R.drawable.sticker_10343,R.drawable.sticker_10341};
    /** 点击监听 **/
    private OnItemClickListener mItemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int resId);
    }

    @Override
    public TileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lsq_tile_recycle_item,null);
        TileViewHolder viewHolder = new TileViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TileViewHolder tileViewHolder, final int i) {
        tileViewHolder.mImage.setImageResource(images[i]);
        tileViewHolder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener != null){
                    mItemClickListener.onItemClick(images[i]);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    class TileViewHolder extends RecyclerView.ViewHolder {
        public TuSdkImageView mImage;

        public TileViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.lsq_tile_image);
        }
    }
}
