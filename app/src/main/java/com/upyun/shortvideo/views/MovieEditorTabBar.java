package com.upyun.shortvideo.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.TuSdkRelativeLayout;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;

/**
 * @author sprint
 * @Date: 26/02/2018
 * @Copyright: (c) 2018 tusdk.com. All rights reserved.
 * @Description 视频编辑页面底部TabBar
 */
public class MovieEditorTabBar extends TuSdkRelativeLayout implements View.OnClickListener
{
    /** 滤镜Tab */
    protected TuSdkTextButton mFilterTabBtn;
    /** MV Tab */
    protected TuSdkTextButton mMvTabBtn;
    /** 配音 Tab */
    protected CompoundDrawableTextView mDubbingTabBtn;
    /** 场景特效 Tab */
    protected TuSdkTextButton mScenceEffectTabBtn;
    // 魔法效果按钮
    private TuSdkTextButton mMagicTabBtn;

    private TabType mSelectedTabType = TabType.FilterTabType;

    public enum TabType
    {
        FilterTabType,
        MVTabType,
        DubbingTabType,
        SenceEffectTabType,
        ParticleEffectTabType
    }

    public interface MovieEditorTabBarDelegate
    {
        public void onSelectedTabType(TabType tabType);
    }

    private MovieEditorTabBarDelegate mDelegate;

    public MovieEditorTabBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setDelegate(MovieEditorTabBarDelegate delegate) {
        this.mDelegate = delegate;
    }

    public MovieEditorTabBarDelegate getDelegate()
    {
        return this.mDelegate;
    }

    public TabType getSelectedTabType()
    {
        return mSelectedTabType;
    }

    @Override
    public void loadView()
    {
        super.loadView();


        mFilterTabBtn = findViewById(com.upyun.shortvideo.R.id.lsq_tab_filter_btn);
        mFilterTabBtn.setOnClickListener(this);


        mMvTabBtn = findViewById(com.upyun.shortvideo.R.id.lsq_tab_mv_btn);
        mMvTabBtn.setOnClickListener(this);

        mDubbingTabBtn = findViewById(com.upyun.shortvideo.R.id.lsq_tab_dubbing_btn);
        mDubbingTabBtn.setOnClickListener(this);

        mScenceEffectTabBtn = findViewById(com.upyun.shortvideo.R.id.lsq_tab_scene_effect_btn);
        mScenceEffectTabBtn.setOnClickListener(this);

        mMagicTabBtn =  findViewById(com.upyun.shortvideo.R.id.lsq_tab_magic_btn);
        mMagicTabBtn.setOnClickListener(this);
    }

    public TuSdkTextButton getMagicTab()
    {
        return mMagicTabBtn;
    }

    public TuSdkTextButton getFilterTab()
    {
        return mFilterTabBtn;
    }

    /**
     * 更新按钮显示状态
     *
     * @param button
     * @param clickable
     */
    public void updateButtonStatus(TuSdkTextButton button, boolean clickable)
    {
        int imgId = 0, colorId = 0;

        switch (button.getId())
        {
            case com.upyun.shortvideo.R.id.lsq_tab_filter_btn:
                imgId = clickable? com.upyun.shortvideo.R.drawable.lsq_style_default_btn_filter_selected
                        : com.upyun.shortvideo.R.drawable.lsq_style_default_btn_filter_unselected;
                colorId = clickable? com.upyun.shortvideo.R.color.lsq_filter_title_color : com.upyun.shortvideo.R.color.lsq_filter_title_default_color;
                break;

            case com.upyun.shortvideo.R.id.lsq_tab_mv_btn:
                imgId = clickable? com.upyun.shortvideo.R.drawable.lsq_style_default_btn_mv_selected
                        : com.upyun.shortvideo.R.drawable.lsq_style_default_btn_mv_unselected;
                colorId = clickable? com.upyun.shortvideo.R.color.lsq_filter_title_color : com.upyun.shortvideo.R.color.lsq_filter_title_default_color;
                break;
            case com.upyun.shortvideo.R.id.lsq_tab_dubbing_btn:
                imgId = clickable? com.upyun.shortvideo.R.drawable.lsq_style_default_btn_beauty_selected
                        : com.upyun.shortvideo.R.drawable.lsq_style_default_btn_beauty_unselected;
                colorId = clickable? com.upyun.shortvideo.R.color.lsq_filter_title_color : com.upyun.shortvideo.R.color.lsq_filter_title_default_color;
                break;
            case com.upyun.shortvideo.R.id.lsq_tab_scene_effect_btn:
                imgId = clickable? com.upyun.shortvideo.R.drawable.lsq_tab_ic_special_selected
                        : com.upyun.shortvideo.R.drawable.lsq_tab_ic_special_normal;
                colorId = clickable? com.upyun.shortvideo.R.color.lsq_filter_title_color : com.upyun.shortvideo.R.color.lsq_filter_title_default_color;
                break;
            case com.upyun.shortvideo.R.id.lsq_tab_magic_btn:
                imgId = clickable? com.upyun.shortvideo.R.drawable.lsq_magic_selected
                        : com.upyun.shortvideo.R.drawable.lsq_magic_unselected;
                colorId = clickable? com.upyun.shortvideo.R.color.lsq_filter_title_color : com.upyun.shortvideo.R.color.lsq_filter_title_default_color;
                break;
        }

        button.setCompoundDrawables(null, TuSdkContext.getDrawable(imgId), null, null);
        button.setTextColor(TuSdkContext.getColor(colorId));
    }

    @Override
    public void onClick(View view)
    {
        updateButtonStatus(mFilterTabBtn, mFilterTabBtn == view);
        updateButtonStatus(mMvTabBtn, mMvTabBtn == view);
        updateButtonStatus(mDubbingTabBtn, mDubbingTabBtn == view);
        updateButtonStatus(mScenceEffectTabBtn,mScenceEffectTabBtn == view);
        updateButtonStatus(mMagicTabBtn,mMagicTabBtn == view);

        if (mDelegate == null) return;

        switch (view.getId())
        {
            case com.upyun.shortvideo.R.id.lsq_tab_filter_btn:
                mSelectedTabType = TabType.FilterTabType;
                break;
            case com.upyun.shortvideo.R.id.lsq_tab_mv_btn:
                mSelectedTabType = TabType.MVTabType;
                break;
            case com.upyun.shortvideo.R.id.lsq_tab_dubbing_btn:
                mSelectedTabType = TabType.DubbingTabType;
                break;
            case com.upyun.shortvideo.R.id.lsq_tab_scene_effect_btn:
                mSelectedTabType = TabType.SenceEffectTabType;
                break;
            case com.upyun.shortvideo.R.id.lsq_tab_magic_btn:
                mSelectedTabType = TabType.ParticleEffectTabType;
                break;
        }

        mDelegate.onSelectedTabType(mSelectedTabType);
    }

    /**
     * 更新按钮显示状态
     *
     * @param button
     * @param clickable
     */
    protected void updateButtonStatus(CompoundDrawableTextView button, boolean clickable)
    {
        int imgId = 0, colorId = 0;
        switch (button.getId())
        {
            case com.upyun.shortvideo.R.id.lsq_tab_dubbing_btn:
                imgId = clickable? com.upyun.shortvideo.R.drawable.lsq_dubbing_selected
                        : com.upyun.shortvideo.R.drawable.lsq_dubbing_default;
                colorId = clickable? com.upyun.shortvideo.R.color.lsq_filter_title_color : com.upyun.shortvideo.R.color.lsq_filter_title_default_color;
                break;
        }

        Drawable dubbingDrawable = TuSdkContext.getDrawable(imgId);
        dubbingDrawable.setBounds(0, 0, TuSdkContext.dip2px(28), TuSdkContext.dip2px(28));
        button.setCompoundDrawables(null, dubbingDrawable, null, null);
        button.setTextColor(TuSdkContext.getColor(colorId));
    }
}
