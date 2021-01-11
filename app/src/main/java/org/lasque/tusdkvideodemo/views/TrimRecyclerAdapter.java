package org.lasque.tusdkvideodemo.views;

import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;
import com.upyun.shortvideo.R;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2019/3/5 10:05
 * @Copright (c) 2019 tusdk.com. All rights reserved.
 * <p>
 * 裁剪适配器
 */
public class TrimRecyclerAdapter extends RecyclerView.Adapter<TrimRecyclerAdapter.TrimViewHolder> {

    private int[] trimStrs = {R.string.lsq_trim_no, R.string.lsq_trim_16_9, R.string.lsq_trim_3_2, R.string.lsq_trim_4_3, R.string.lsq_trim_1_1, R.string.lsq_trim_3_4, R.string.lsq_trim_2_3, R.string.lsq_trim_9_16};
    private float[] trimRatio = {0, 16f / 9f, 3f / 2f, 4f / 3f, 1f, 3f / 4f, 2f / 3f, 9f / 16f};
    private int[] trimNorIcon = {R.drawable.crop_no_nor, R.drawable.crop_16_9_nor, R.drawable.crop_3_2_nor, R.drawable.crop_4_3_nor, R.drawable.crop_1_1_nor, R.drawable.crop_3_4_nor, R.drawable.crop_2_3_nor, R.drawable.crop_9_16_nor};
    private int[] trimSelIcon = {R.drawable.crop_no_sel, R.drawable.crop_16_9_sel, R.drawable.crop_3_2_sel, R.drawable.crop_4_3_sel, R.drawable.crop_1_1_sel, R.drawable.crop_3_4_sel, R.drawable.crop_2_3_sel, R.drawable.crop_9_16_sel};


    private int mCurrentPosition = 0;


    public interface ItemClickListener {
        void onItemClick(float ratio, int position);
    }

    public ItemClickListener listener;

    public void setItemCilckListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public TrimRecyclerAdapter() {

    }

    @Override
    public TrimViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lsq_trim_recycler_item, null);
        TrimViewHolder viewHolder = new TrimViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrimViewHolder trimViewHolder, final int i) {
        trimViewHolder.trimTextBtn.setText(trimStrs[i]);
        if (mCurrentPosition == i) {
            trimViewHolder.trimTextBtn.setTextColor(trimViewHolder.trimTextBtn.getContext().getResources().getColor(R.color.lsq_color_api_button));
            Drawable topDrawable = TuSdkContext.getDrawable(trimSelIcon[i]);
            trimViewHolder.trimTextBtn.setCompoundDrawables(null, topDrawable, null, null);
        } else {
            trimViewHolder.trimTextBtn.setTextColor(trimViewHolder.trimTextBtn.getContext().getResources().getColor(R.color.lsq_color_white));
            Drawable topDrawable = TuSdkContext.getDrawable(trimNorIcon[i]);
            trimViewHolder.trimTextBtn.setCompoundDrawables(null, topDrawable, null, null);
        }

        trimViewHolder.trimTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick(trimRatio[i],i);
                notifyItemChanged(mCurrentPosition);
                mCurrentPosition = i;
                notifyItemChanged(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trimStrs.length;
    }

    /**
     *
     * @param position
     */
    public void setSelectItem(int position){
        mCurrentPosition = position;
        if(listener != null) listener.onItemClick(trimRatio[position],position);
        notifyDataSetChanged();
    }

    class TrimViewHolder extends RecyclerView.ViewHolder {
        public TuSdkTextButton trimTextBtn;

        public TrimViewHolder(View itemView) {
            super(itemView);
            trimTextBtn = itemView.findViewById(R.id.trim_btn);
        }
    }

}
