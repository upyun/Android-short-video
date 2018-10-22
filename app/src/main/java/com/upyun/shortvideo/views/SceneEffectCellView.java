package com.upyun.shortvideo.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.listview.TuSdkCellRelativeLayout;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;
import com.upyun.shortvideo.R;

/**
 * Created by sprint on 26/12/2017.
 */

public class SceneEffectCellView extends TuSdkCellRelativeLayout<String>
{

    /** 缩略图 */
    private ImageView mThumbView;
    /** 场景特效名称 */
    private TextView mTitleView;
    /** 撤销按钮 */
    private TuSdkTextButton mUndoButton;

    public SceneEffectCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


//    public SceneEffectData(String effectCode)
//    {
//        this.mColor =  TuSdkContext.getColor(TuSdkContext.getColorResId("lsq_scence_effect_color_"+effectCode));
//        this.mSceneEffectCode = effectCode;
//    }

    @Override
    protected void bindModel()
    {

        String code = getModel();

        getImageView().setImageResource(TuSdkContext.getDrawableResId("lsq_scence_effect_"+code));
        getTitleView().setText(TuSdkContext.getString("lsq_filter_"+code));

        // 当前是否为撤销 Cell
        boolean isUndoCell = (Integer) getTag() == 0;

        findViewById(R.id.lsq_scence_effect_cell_layout).setVisibility( isUndoCell ? View.GONE : View.VISIBLE);
        getUndoButton().setVisibility(isUndoCell ? View.VISIBLE : View.GONE);
    }

    public TuSdkTextButton getUndoButton()
    {
        if ( mUndoButton == null)
        {
            mUndoButton = (TuSdkTextButton) findViewById(R.id.lsq_scence_effect_cell_undo_btn);
        }
        return mUndoButton;
    }


    public ImageView getImageView()
    {
        if ( mThumbView == null )
        {
            mThumbView = (ImageView)findViewById(R.id.lsq_item_image);
        }
        return mThumbView;
    }

    public TextView getTitleView()
    {
        if ( mTitleView == null )
        {
            mTitleView = (TextView)findViewById(R.id.lsq_item_title);
        }
        return mTitleView;
    }

    /**
     * 更新撤销按钮的状态
     *
     * @param isEnableClicked
     */
    public void updateUndoButton(boolean isEnableClicked)
    {
        Drawable cancelUnClickedDrawable = getResources().getDrawable(R.drawable.edit_ic_back);
        cancelUnClickedDrawable.setAlpha(isEnableClicked ? 255 : 66);
        // 这一步必须要做,否则不会显示
        cancelUnClickedDrawable.setBounds(0, 0, cancelUnClickedDrawable.getMinimumWidth(), cancelUnClickedDrawable.getMinimumHeight());
        getUndoButton().setCompoundDrawables(null, cancelUnClickedDrawable, null, null);

        getUndoButton().setEnabled(isEnableClicked);
        getUndoButton().setTextColor(getResColor(isEnableClicked ? R.color.lsq_filter_title_color : R.color.lsq_filter_title_color_alpha_20));
    }
}
