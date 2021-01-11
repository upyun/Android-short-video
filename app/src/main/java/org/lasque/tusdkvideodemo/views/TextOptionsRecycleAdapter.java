package org.lasque.tusdkvideodemo.views;

import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.upyun.shortvideo.R;

/**
 * 文字组件选项适配器
 *
 * @author MirsFang
 * @date 2019/03/07 10:47:00
 */
public class TextOptionsRecycleAdapter extends RecyclerView.Adapter<TextOptionsRecycleAdapter.OptionsViewHolder> {

    private int[] iconArray = {
            R.drawable.edit_ic_add, R.drawable.edit_ic_font, R.drawable.edit_ic_colour,
            R.drawable.t_ic_transparency, R.drawable.t_ic_stroke, R.drawable.t_ic_bg,
            R.drawable.t_ic_space, R.drawable.t_ic_align, R.drawable.t_ic_array,
            R.drawable.edit_ic_style
    };

    private int[] titleArray = {
            R.string.lsq_editor_text_add, R.string.lsq_editor_text_font, R.string.lsq_editor_text_color,
            R.string.lsq_editor_text_transparency, R.string.lsq_editor_text_stroke, R.string.lsq_editor_text_background,
            R.string.lsq_editor_text_space, R.string.lsq_editor_text_align, R.string.lsq_editor_text_array,
            R.string.lsq_editor_text_style
    };

    /** 是否启用 **/
    private boolean mEnable = false;
    /** 是否启用添加按钮 **/
    private boolean mAddEnable = true;

    /** 点击事件回调 **/
    private OnItemClickListener mItemClick;

    public interface OnItemClickListener {
        /** Item的点击事件 **/
        void onItemClick(int position,@StringRes int titleId);
    }

    @Override
    public OptionsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lsq_recycle_text_opentions_item, null);
        OptionsViewHolder viewHolder = new OptionsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OptionsViewHolder optionsViewHolder, final int i) {
        optionsViewHolder.mOptionsText.setText(titleArray[i]);
        optionsViewHolder.mOptionsIcon.setImageResource(iconArray[i]);

        if (i == 0) {
            optionsViewHolder.setEnable(mAddEnable);
            if (mAddEnable) {
                optionsViewHolder.mContent.setClickable(true);
                optionsViewHolder.mContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mItemClick != null) mItemClick.onItemClick(i,titleArray[i]);
                    }
                });
            } else {
                optionsViewHolder.mContent.setClickable(false);
                optionsViewHolder.mContent.setOnClickListener(null);
            }
        } else {
            optionsViewHolder.setEnable(mEnable);
            if (mEnable) {
                optionsViewHolder.mContent.setClickable(true);
                optionsViewHolder.mContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mItemClick != null) mItemClick.onItemClick(i,titleArray[i]);
                    }
                });
            } else {
                optionsViewHolder.mContent.setClickable(false);
                optionsViewHolder.mContent.setOnClickListener(null);
            }
        }


    }

    @Override
    public int getItemCount() {
        return iconArray.length;
    }

    /** 设置item的点击事件 **/
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClick = onItemClickListener;
    }

    /** 是否启用 **/
    public void setEnable(boolean enable) {
        mEnable = enable;
        notifyDataSetChanged();
    }

    /** 是否启用添加按钮 **/
    public void setAddEnable(boolean addEnable) {
        mAddEnable = addEnable;
        notifyDataSetChanged();
        ;
    }


    /** 选项的ViewHolder **/
    class OptionsViewHolder extends RecyclerView.ViewHolder {
        private View mContent;
        public ImageView mOptionsIcon;
        public TextView mOptionsText;

        public OptionsViewHolder(View itemView) {
            super(itemView);
            mContent = itemView.findViewById(R.id.lsq_text_options_content);
            mOptionsIcon = itemView.findViewById(R.id.lsq_text_options_icon);
            mOptionsText = itemView.findViewById(R.id.lsq_text_options_text);
        }

        public void setEnable(boolean enable) {
            mOptionsText.setTextColor(mOptionsText.getContext().getResources().getColor(enable ? R.color.lsq_color_white : R.color.lsq_alpha_white_66));
            mOptionsIcon.setAlpha(enable ? 1f : 0.6f);
        }
    }
}
