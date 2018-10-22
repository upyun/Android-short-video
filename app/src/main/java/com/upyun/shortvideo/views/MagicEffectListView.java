package com.upyun.shortvideo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;


/**
 * 魔法效果列表
 */
public class MagicEffectListView extends TuSdkTableView<String, MagicEffectCellView>
{
    /** 行视图宽度 */
    private int mCellWidth;

    // 撤销按钮是否可被点击
    private boolean mIsEnableUndoClicked = false;

    public MagicEffectListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public MagicEffectListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MagicEffectListView(Context context)
    {
        super(context);
    }

    /** 行视图宽度 */
    public int getCellWidth()
    {
        return mCellWidth;
    }

    /** 行视图宽度 */
    public void setCellWidth(int mCellWidth)
    {
        this.mCellWidth = mCellWidth;

    }

    @Override
    public void loadView()
    {
        super.loadView();

        this.setHasFixedSize(true);
    }

    /**
     * 视图创建
     *
     * @param view
     *            创建的视图
     * @param parent
     *            父对象
     * @param viewType
     *            视图类型
     */
    @Override
    protected void onViewCreated(MagicEffectCellView view, ViewGroup parent, int viewType)
    {
        if (this.getCellWidth() > 0)
        {
            view.setWidth(this.getCellWidth());
        }
    }

    @Override
    protected void onViewBinded(MagicEffectCellView view, int position)
    {
        view.setTag(position);

        if (position == 0) view.updateUndoButton(mIsEnableUndoClicked);
    }

    /**
     * 更新撤销按钮的状态
     *
     * @param isEnableClicked
     */
    public void updateUndoButtonState(boolean isEnableClicked)
    {
        getSdkAdapter().notifyItemChanged(0);

        this.mIsEnableUndoClicked = isEnableClicked;
    }
}
