package com.upyun.shortvideo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;

/**
 * 魔法效果列表Item视图管理
 */

public class MagicEffectCellView extends FilterCellView
{
    /** 撤销按钮 */
    private TuSdkTextButton mUndoButton;

    public MagicEffectCellView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public MagicEffectCellView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MagicEffectCellView(Context context)
    {
        super(context);
    }

    @Override
    protected void bindModelTypeOne()
    {
        String code = getModel();

        getImageView().setImageBitmap(null);

        Bitmap filterImage = TuSdkContext.getRawBitmap("lsq_filter_thumb_"+code.toLowerCase());

        if (this.getImageView() != null && filterImage != null)
        {
            getImageView().setImageBitmap(filterImage);
        }

        getTitleView().setText(TuSdkContext.getString("lsq_filter_"+code));
    }

    @Override
    protected void bindModelTypeTwo()
    {
        // 当前是否为撤销 Cell
        boolean isUndoCell = (Integer) getTag() == 0;

        findViewById(com.upyun.shortvideo.R.id.lsq_border_layout).setVisibility( isUndoCell ? GONE : VISIBLE);

        getUndoButton().setVisibility(isUndoCell ? VISIBLE : GONE);
    }

    public TuSdkTextButton getUndoButton()
    {
        if ( mUndoButton == null)
        {
            mUndoButton = (TuSdkTextButton) findViewById(com.upyun.shortvideo.R.id.lsq_magic_effect_cell_undo_btn);
        }
        return mUndoButton;
    }

    /**
     * 更新撤销按钮的状态
     *
     * @param isEnableClicked
     */
    public void updateUndoButton(boolean isEnableClicked)
    {
        Drawable cancelUnClickedDrawable = getResources().getDrawable(com.upyun.shortvideo.R.drawable.edit_ic_back);
        cancelUnClickedDrawable.setAlpha(isEnableClicked ? 255 : 66);
        // 这一步必须要做,否则不会显示
        cancelUnClickedDrawable.setBounds(0, 0, cancelUnClickedDrawable.getMinimumWidth(), cancelUnClickedDrawable.getMinimumHeight());
        getUndoButton().setCompoundDrawables(null, cancelUnClickedDrawable, null, null);

        getUndoButton().setEnabled(isEnableClicked);
        getUndoButton().setTextColor(getResColor(isEnableClicked ? com.upyun.shortvideo.R.color.lsq_filter_title_color : com.upyun.shortvideo.R.color.lsq_filter_title_color_alpha_20));
    }
}
