package com.upyun.shortvideo.views;

import android.content.Context;
import android.util.AttributeSet;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.TuSdkRelativeLayout;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;

import com.upyun.shortvideo.utils.Constants;

import java.util.Arrays;

/**
 * 魔法效果
 */

public class MagicEffectLayout extends TuSdkRelativeLayout
{
    private MagicEffectsTimelineView mTimelineView;

    private MagicEffectListView mMagicEffectListView;


    public MagicEffectLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * 加载视图
     */
    @Override
    public void loadView()
    {
        super.loadView();

        // 动画时间轴视图
        mTimelineView = getViewById(com.upyun.shortvideo.R.id.lsq_magic_effect_timelineView);
        mTimelineView.setDelegate(mEffectsTimelineViewDelegate);
        initMagicEffectListView();
    }

    private EffectsTimelineView.EffectsTimelineViewDelegate mEffectsTimelineViewDelegate = new EffectsTimelineView.EffectsTimelineViewDelegate()
    {
        @Override
        public void onProgressCursorWillChaned()
        {
        }

        @Override
        public void onProgressChaned(float progress)
        {
        }

        @Override
        public void onEffectNumChanged(int effectNum)
        {
            getMagicEffectListView().updateUndoButtonState(effectNum == 0 ? false :true);
        }
    };

    /**
     * 魔法特效时间轴视图
     *
     * @return
     */
    public MagicEffectsTimelineView getTimelineView()
    {
        return mTimelineView;
    }


    /**
     * 设置魔法特效列表委托对象
     *
     * @param itemClickDelegate
     */
    public void setDelegate(TuSdkTableView.TuSdkTableViewItemClickDelegate itemClickDelegate)
    {
        getMagicEffectListView().setItemClickDelegate(itemClickDelegate);
    }

    /**
     * 设置魔法特效时间表委托对象
     *
     * @param delegate
     */
    public void setDelegate(EffectsTimelineView.EffectsTimelineViewDelegate delegate)
    {
        getTimelineView().setDelegate(delegate);
    }

    /**
     * 魔法效果列表视图
     *
     * @return
     */
    public MagicEffectListView getMagicEffectListView()
    {
        if (mMagicEffectListView == null)
        {
            mMagicEffectListView = (MagicEffectListView) findViewById(com.upyun.shortvideo.R.id.lsq_magic_effect_list_view);

            if (mMagicEffectListView == null) return null;

            mMagicEffectListView.loadView();
            mMagicEffectListView.setCellLayoutId(com.upyun.shortvideo.R.layout.magic_list_cell_view);
            mMagicEffectListView.setCellWidth(TuSdkContext.dip2px(62));
            mMagicEffectListView.reloadData();
        }
        return mMagicEffectListView;
    }

    /**
     * 初始化魔法效果视图
     */
    protected void initMagicEffectListView()
    {
        getMagicEffectListView();

        if (mMagicEffectListView == null) return;

        this.mMagicEffectListView.setModeList(Arrays.asList(Constants.PARTICLE_CODES));
    }

    /**
     * 获取当前粒子效果的Code
     *
     * @return
     */
    public String getCurrentParticleCode()
    {
        if (mMagicEffectListView.getSelectedPosition() < 0) return "";
        return Constants.PARTICLE_CODES[mMagicEffectListView.getSelectedPosition()];
    }


}
