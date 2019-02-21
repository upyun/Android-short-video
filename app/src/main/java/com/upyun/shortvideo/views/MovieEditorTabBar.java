package com.upyun.shortvideo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.lasque.tusdk.core.view.TuSdkRelativeLayout;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;
import com.upyun.shortvideo.R;

/**
 * @author sprint
 * @Date: 26/02/2018
 * @Copyright: (c) 2018 tusdk.com. All rights reserved.
 * @Description 视频编辑页面底部TabBar
 */
public class MovieEditorTabBar extends TuSdkRelativeLayout implements View.OnClickListener {
    // 滤镜Tab
    protected TuSdkTextButton mFilterTabBtn;
    // MV Tab
    protected TuSdkTextButton mMvTabBtn;
    // 配音
    private TuSdkTextButton mMusicTabBtn;
    // 文字贴纸按钮
    private TuSdkTextButton mTextTabBtn;
    // 特效Tab
    protected TuSdkTextButton mEffectTabBtn;

    //是否启用
    private boolean mEnable = true;

    private TabType mSelectedTabType;

    public enum TabType {
        //滤镜
        FilterTab,
        //MV
        MVTab,
        //配音
        MusicTab,
        //文字
        TextTab,
        //特效
        EffectTab,
        //封面
        CoverTab
    }

    public interface MovieEditorTabBarDelegate {
        public void onSelectedTabType(TabType tabType);
    }

    private MovieEditorTabBarDelegate mDelegate;

    public MovieEditorTabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDelegate(MovieEditorTabBarDelegate delegate) {
        this.mDelegate = delegate;
    }

    public MovieEditorTabBarDelegate getDelegate() {
        return this.mDelegate;
    }

    public TabType getSelectedTabType() {
        return mSelectedTabType;
    }

    @Override
    public void loadView() {
        super.loadView();

        mFilterTabBtn = findViewById(R.id.lsq_tab_filter_btn);
        mFilterTabBtn.setOnClickListener(this);

        mMvTabBtn = findViewById(R.id.lsq_tab_mv_btn);
        mMvTabBtn.setOnClickListener(this);

        mMusicTabBtn = findViewById(R.id.lsq_tab_music_btn);
        mMusicTabBtn.setOnClickListener(this);

        mTextTabBtn = findViewById(R.id.lsq_tab_text_effect_btn);
        mTextTabBtn.setOnClickListener(this);

        mEffectTabBtn = findViewById(R.id.lsq_tab_effect_btn);
        mEffectTabBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (!getEnable()) return;

        if (mDelegate == null) return;

        switch (view.getId()) {
            case R.id.lsq_tab_filter_btn:
                mSelectedTabType = TabType.FilterTab;
                break;
            case R.id.lsq_tab_mv_btn:
                mSelectedTabType = TabType.MVTab;
                break;
            case R.id.lsq_tab_music_btn:
                mSelectedTabType = TabType.MusicTab;
                break;
            case R.id.lsq_tab_text_effect_btn:
                mSelectedTabType = TabType.TextTab;
                break;
            case R.id.lsq_tab_effect_btn:
                mSelectedTabType = TabType.EffectTab;
                break;
        }

        mDelegate.onSelectedTabType(mSelectedTabType);
    }


    /**
     * 是否可用
     *
     * @param mEnable true 可用
     */
    public void setEnable(boolean mEnable) {
        this.mEnable = mEnable;
    }

    public boolean getEnable() {
        return mEnable;
    }
}
