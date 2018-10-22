/**
 * TuSDKCore
 * TextStickerData
 *
 * @author MirsFang
 * @Date: 2018/4/11 下午2:38
 * @Copyright: (c) 2018 tusdk.com. All rights reserved.
 */
package com.upyun.shortvideo.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.IntRange;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.upyun.shortvideo.component.MovieEditorActivity;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditorImpl;
import org.lasque.tusdk.core.seles.tusdk.textSticker.TextStickerData;
import org.lasque.tusdk.core.seles.tusdk.textSticker.TuSDKTextStickerImage;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.view.TuSdkLinearLayout;
import org.lasque.tusdk.core.view.TuSdkRelativeLayout;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.widget.TuMaskRegionView;
import org.lasque.tusdk.core.view.widget.TuSdkEditText;
import org.lasque.tusdk.core.view.widget.button.TuSdkImageButton;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;
import org.lasque.tusdk.impl.components.widget.sticker.StickerTextItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView.StickerViewDelegate;
import org.lasque.tusdk.modules.view.widget.sticker.StickerData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerFactory;
import org.lasque.tusdk.modules.view.widget.sticker.StickerItemViewInterface;
import org.lasque.tusdk.modules.view.widget.sticker.StickerText;
import org.lasque.tusdk.modules.view.widget.sticker.StickerTextData;
import org.lasque.tusdk.video.editor.TuSDKMediaTextEffectData;
import org.lasque.tusdk.video.editor.TuSDKTimeRange;
import com.upyun.shortvideo.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 文字特效自定义View
 */
public class TextEffectLayout extends TuSdkRelativeLayout implements StickerViewDelegate {

    //视频编辑器
    private TuSdkMovieEditorImpl mMovieEditor;

    /************************* view ******************************/
    //贴图视图
    private StickerView mStickerView;
    /** 裁剪选区视图 */
    private TuMaskRegionView mCutRegionView;
    /** 文字输入框 */
    private TuSdkEditText mEditTextView;
    /** 样式栏*/
    private LinearLayout mFeatureBar;
    /** 颜色调节栏*/
    private TuSdkColorSelectorBar mColorSelectorBar;
    /** 样式栏中颜色按钮*/
    private TuSdkTextButton mColorButton;
    /** 样式栏中字体样式按钮*/
    private TuSdkTextButton mStyleButton;
    /** 取消按钮 */
    private TuSdkImageButton mCancelButton;
    /** 完成按钮 */
    private TuSdkImageButton mCompleteButton;
    /** 添加贴纸按钮 */
    private TuSdkTextButton mAddStickerButton;
    /** 样式栏中从右到左设置按钮 */
    private TuSdkTextButton mToLeftStyleButton;
    /** 样式栏中从左到右设置按钮 */
    private TuSdkTextButton mToRightStyleButton;
    /** 样式栏中下划线设置按钮 */
    private TuSdkTextButton mUnderlineStyleButton;
    /** 样式栏中左对齐设置按钮 */
    private TuSdkTextButton mAlignLeftStyleButton;
    /** 样式栏中右对齐设置按钮 */
    private TuSdkTextButton mAlignRightStyleButton;
    /** 样式栏中居中对齐设置按钮 */
    private TuSdkTextButton mAlignCenterStyleButton;
    /** 底部栏视图*/
    private RelativeLayout mTopBar;
    /** 颜色和样式底部栏视图*/
    private RelativeLayout mFeatureBottomBar;
    /** 返回按钮*/
    private TuSdkImageButton mParamBackButton;

    /** 颜色参数视图 */
    private TuSdkLinearLayout mColorParamWrapLayout;
    /** 颜色调节父视图 */
    private TuSdkLinearLayout mColorWrapLayout;
    /** 样式调节父视图 */
    private HorizontalScrollView mStyleWrapLayout;

    /** 视频裁剪控件  */
    private MovieRangeSelectionBar mRangeSelectionBar;

    //Activity 的引用
    private WeakReference<Activity> mWRActivity;

    private OnTextStickerApplyListener mOnTextStickerApplyListener;



    /**
     * 设置应用贴纸的监听
     */
    public interface OnTextStickerApplyListener{
        void onApply(TuSDKMediaTextEffectData textMediaEffectData);
    }


    public TextEffectLayout(Context context) {
        super(context);
    }

    public TextEffectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setOnTextStickerApplyListener(OnTextStickerApplyListener mOnTextStickerApplyListener) {
        this.mOnTextStickerApplyListener = mOnTextStickerApplyListener;
    }

    public OnTextStickerApplyListener getOnTextStickerApplyListener() {
        return mOnTextStickerApplyListener;
    }

    /**
     * 设置编辑器
     * @param movieEditor
     */
    public void setMovieEditor(TuSdkMovieEditorImpl movieEditor)
    {
        this.mMovieEditor = movieEditor;
    }


    public void setActivity(Activity activity){
        if(mWRActivity != null)
        {
           mWRActivity.clear();
        }
        mWRActivity = new WeakReference<>(activity);
    }


    public Activity getActivity(){

        if(mWRActivity == null||mWRActivity.get()==null)
        {
            TLog.e(" WeakRefrens Acitvity is null");
            return null;
        }

        return mWRActivity.get();
    }


    /*******************************    View 视图   **********************************/

    /** 贴纸视图 */
    public StickerView getStickerView()
    {
        if (mStickerView == null)
        {
            TLog.e("StickerView  is  null   !!!");
        }

        mStickerView.setDelegate(this.getStickerViewDelegate());
        return mStickerView;
    }



    /** 文字输入框 */
    public TuSdkEditText getEditTextView()
    {
        if (mEditTextView == null)
        {
            mEditTextView = this.getViewById("lsq_editTextView");
            if (mEditTextView != null)
            {
                mEditTextView.addTextChangedListener(mTextWatcher);
                mEditTextView.setVisibility(View.INVISIBLE);
            }
        }
        return mEditTextView;
    }


    /** 样式栏 */
    public LinearLayout getFeatureBar()
    {
        if (mFeatureBar == null)
        {
            mFeatureBar = this.getViewById("lsq_featureBar");
        }
        return mFeatureBar;
    }

    /** 颜色调节栏 */
    public TuSdkColorSelectorBar getColorSelectorBar()
    {
        if (mColorSelectorBar == null)
        {
            mColorSelectorBar = this.getViewById("lsq_colorSelector");
            mColorSelectorBar.setColorChangeListener(mOnColorChangeListener);

            mColorSelectorBar.setColorBarHeight(getColorBarHeight());
            mColorSelectorBar.setColorIndicatorWidth(getColorIndicatorWidth());
            mColorSelectorBar.setColorIndicatorHeight(getColorIndicatorHeight());
            mColorSelectorBar.setColorBarPaddingTop(getColorBarPaddingTop());
        }
        return mColorSelectorBar;
    }


    /** 颜色参数视图*/
    public TuSdkLinearLayout getColorParamWrapLayout()
    {
        if (mColorParamWrapLayout == null)
        {
            mColorParamWrapLayout = this.getViewById("lsq_colorParamView");
        }
        return mColorParamWrapLayout;
    }


    /** 颜色调节父视图*/
    public TuSdkLinearLayout getColorWrapLayout()
    {
        if (mColorWrapLayout == null)
        {
            mColorWrapLayout = this.getViewById("lsq_colorWrap");
        }
        return mColorWrapLayout;
    }


    /** 样式调节父视图*/
    public HorizontalScrollView getStyleWrapLayout()
    {
        if (mStyleWrapLayout == null)
        {
            mStyleWrapLayout = this.getViewById("lsq_styleWrap");
        }
        return mStyleWrapLayout;
    }

    /** 颜色按钮 */
    public TuSdkTextButton getColorButton()
    {
        if (mColorButton == null)
        {
            mColorButton = this.getViewById("lsq_feature_color");
            mColorButton.setOnClickListener(mButtonClickListener);
        }
        return mColorButton;
    }

    /** 样式按钮 */
    public TuSdkTextButton getStyleButton()
    {
        if (mStyleButton == null)
        {
            mStyleButton = this.getViewById("lsq_feature_style");
            mStyleButton.setOnClickListener(mButtonClickListener);
        }
        return mStyleButton;
    }


    /** 取消按钮 */
    public TuSdkImageButton getCancelButton()
    {
        if (mCancelButton == null)
        {
            mCancelButton = this.getViewById("lsq_bar_cancelButton");
            if (mCancelButton != null)
            {
                mCancelButton.setOnClickListener(mButtonClickListener);
            }
        }
        return mCancelButton;
    }

    /** 完成按钮 */
    public TuSdkImageButton getCompleteButton()
    {
        if (mCompleteButton == null)
        {
            mCompleteButton = this.getViewById("lsq_bar_completeButton");
            if (mCompleteButton != null)
            {
                mCompleteButton.setOnClickListener(mButtonClickListener);
            }
        }
        return mCompleteButton;
    }


    /** 样式栏中从右到左设置按钮 */
    public TuSdkTextButton getToLeftStyleButton()
    {
        if (mToLeftStyleButton == null)
        {
            mToLeftStyleButton = this.getViewById("lsq_styleToLeft");
            if (mToLeftStyleButton != null)
            {
                mToLeftStyleButton.setSelectedColor(TuSdkContext.getColor("lsq_color_orange"));
                mToLeftStyleButton.setOnTouchListener(mOnTouchListener);
            }
        }
        return mToLeftStyleButton;
    }


    /** 样式栏中从左到右设置按钮 */
    public TuSdkTextButton getToRightStyleButton()
    {
        if (mToRightStyleButton == null)
        {
            mToRightStyleButton = this.getViewById("lsq_styleToRight");
            if (mToRightStyleButton != null)
            {
                mToRightStyleButton.setSelectedColor(TuSdkContext.getColor("lsq_color_orange"));
                mToRightStyleButton.setOnTouchListener(mOnTouchListener);
            }
        }
        return mToRightStyleButton;
    }


    /** 样式栏中下划线设置按钮 */
    public TuSdkTextButton getUnderlineStyleButton()
    {
        if (mUnderlineStyleButton== null)
        {
            mUnderlineStyleButton = this.getViewById("lsq_styleUnderLine");
            if (mUnderlineStyleButton != null)
            {
                mUnderlineStyleButton.setSelectedColor(TuSdkContext.getColor("lsq_color_orange"));
                mUnderlineStyleButton.setOnTouchListener(mOnTouchListener);
            }
        }
        return mUnderlineStyleButton;
    }


    /** 样式栏中左对齐设置按钮 */
    public TuSdkTextButton getAlignLeftStyleButton()
    {
        if (mAlignLeftStyleButton== null)
        {
            mAlignLeftStyleButton = this.getViewById("lsq_styleAlignLeft");
            if (mAlignLeftStyleButton != null)
            {
                mAlignLeftStyleButton.setSelectedColor(TuSdkContext.getColor("lsq_color_orange"));
                mAlignLeftStyleButton.setOnTouchListener(mOnTouchListener);
            }
        }
        return mAlignLeftStyleButton;
    }


    /** 样式栏中右对齐设置按钮 */
    public TuSdkTextButton getAlignRightStyleButton()
    {
        if (mAlignRightStyleButton== null)
        {
            mAlignRightStyleButton = this.getViewById("lsq_styleAlignRight");
            if (mAlignRightStyleButton != null)
            {
                mAlignRightStyleButton.setSelectedColor(TuSdkContext.getColor("lsq_color_orange"));
                mAlignRightStyleButton.setOnTouchListener(mOnTouchListener);
            }
        }
        return mAlignRightStyleButton;
    }


    /** 样式栏中居中对齐设置按钮 */
    public TuSdkTextButton getAlignCenterStyleButton()
    {
        if (mAlignCenterStyleButton== null)
        {
            mAlignCenterStyleButton = this.getViewById("lsq_styleAlignCenter");
            if (mAlignCenterStyleButton != null)
            {
                mAlignCenterStyleButton.setSelectedColor(TuSdkContext.getColor("lsq_color_orange"));
                mAlignCenterStyleButton.setOnTouchListener(mOnTouchListener);
            }
        }
        return mAlignCenterStyleButton;
    }


    /** 底部栏视图 */
    public RelativeLayout getTopBar()
    {
        if (mTopBar == null)
        {
            mTopBar = this.getViewById("lsq_bar_topBar");
        }
        return mTopBar;
    }

    /** 颜色和样式底部栏视图 */
    public RelativeLayout getColorBottomBar()
    {
        if (mFeatureBottomBar == null)
        {
            mFeatureBottomBar = this.getViewById("lsq_color_bar_bottomBar");
            mFeatureBottomBar.setOnClickListener(mButtonClickListener);
        }
        return mFeatureBottomBar;
    }


    /** 返回按钮 */
    public TuSdkImageButton getParamBackButton()
    {
        if (mParamBackButton == null)
        {
            mParamBackButton = this.getViewById("lsq_bar_backButton");
            mParamBackButton.setOnClickListener(mButtonClickListener);
        }
        return mParamBackButton;
    }


    /** 添加贴纸按钮 */
    public TuSdkTextButton getAddStickerButton()
    {
        if (mAddStickerButton == null)
        {
            mAddStickerButton = this.getViewById("lsq_feature_add");
            if (mAddStickerButton != null)
            {
                mAddStickerButton.setOnClickListener(mButtonClickListener);
            }
        }
        return mAddStickerButton;
    }

    /** 拖动颜色条改变颜色监听 */
    private TuSdkColorSelectorBar.OnColorChangeListener mOnColorChangeListener = new TuSdkColorSelectorBar.OnColorChangeListener()
    {
        @Override
        public void onSelectedColorChanged(String color)
        {
            // 根据当前所选中的 button 的 index 属性判断需要改变字体颜色还是背景色(0：字体颜色，1：背景颜色，2：线条颜色)
            int index = mCurrentSelectedButton == null ? 0 : mCurrentSelectedButton.index;
            getStickerView().onSelectedColorChanged(index, color);
        }
    };


    /** 对根视图设置全局布局监听,以间接实现监听键盘隐藏显示状态*/
    private void setListenerToRootView()
    {
        final View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                // 键盘时隐藏状态,则隐藏输入框
                if(!isKeyboardShown(rootView))
                {
                    getEditTextView().setVisibility(View.INVISIBLE);
                }
                // 键盘时显示状态,则显示输入框
                else if(isKeyboardShown(rootView))
                {
                    getEditTextView().setVisibility(View.VISIBLE);
                }
            }
        });
    }


    /** 更新文字贴纸视图中的内容*/
    public void updateText(String text, boolean needReverse)
    {
        if(text == null) return;

        this.getStickerView().updateText(text, needReverse);
    }

    /** 判断键盘显示状态*/
    private boolean isKeyboardShown(View rootView)
    {
        // 设置阈值
        final int softKeyboardHeight = 100;

        Rect r = new Rect();
        // 获取rootView 的可见区域
        rootView.getWindowVisibleDisplayFrame(r);
        // 获取可见区域的底部和 rootView 的底部的距离
        int heightDiff = rootView.getBottom() - r.bottom;

        // 如果可见区域的底部和 rootView 的底部距离较大且不是用户点击贴纸删除按钮导致
        return heightDiff > TuSdkContext.dip2px(softKeyboardHeight) && !isCancelAction;
    }


    /**
     * 获取时间选取
     */
    private MovieRangeSelectionBar getRangeLayout(){

        if (mRangeSelectionBar == null)
        {
            mRangeSelectionBar = this.getViewById("lsq_movie_seekbar");
            mRangeSelectionBar.setType(MovieRangeSelectionBar.Type.MV);
            mRangeSelectionBar.setShowPlayCursor(true);
            mRangeSelectionBar.setLeftSelection(0);
            mRangeSelectionBar.setPlaySelection(0);
            mRangeSelectionBar.setRightSelection(100);
            mRangeSelectionBar.setShadowColor("#55ffffff");
            mRangeSelectionBar.setOnCursorChangeListener(mOnCursorChangeListener);
        }

        return mRangeSelectionBar;
    }

    /** 记录裁剪控件的宽度  */
    private int seekBarWidth;
    private int mStart_time;
    private int mEnd_time;
    /***
     * 时间选取的监听
     */
    private MovieRangeSelectionBar.OnCursorChangeListener
            mOnCursorChangeListener = new MovieRangeSelectionBar.OnCursorChangeListener()
    {

        @Override
        public void onSeeekBarChanged(int width, int height)
        {
            setBarSpace();
        }

        @Override
        public void onLeftCursorChanged(int percent)
        {
            //记录开始区间
            mStart_time = (int) getTimeForDuration(percent);

            if(getStickerView().getCurrentItemViewSelected()!=null && getStickerView().getCurrentItemViewSelected() instanceof StickerTextItemView)
            {
                ((StickerTextData) ((StickerTextItemView) getStickerView().getCurrentItemViewSelected()).getSticker()).startime= mStart_time/1000;
            }
        }

        @Override
        public void onPlayCursorChanged(int percent)
        {

        }

        @Override
        public void onRightCursorChanged(int percent)
        {
            //记录结束区间
            mEnd_time = (int) getTimeForDuration(percent);

            if(getStickerView().getCurrentItemViewSelected()!=null && getStickerView().getCurrentItemViewSelected() instanceof StickerTextItemView)
            {
                ((StickerTextData) ((StickerTextItemView)getStickerView().getCurrentItemViewSelected()).getSticker()).stopTime = mEnd_time/1000;
            }
        }

        @Override
        public void onLeftCursorUp()
        {
        }

        @Override
        public void onRightCursorUp()
        {
        }
    };

    /**
     * 百分比整数 （0~100）
     * @param percent
     * @return
     */
    private float getTimeForDuration(@IntRange(from = 0, to = 100) int percent){
        return percent*mMovieEditor.getEditorTransCoder().getVideoInfo().durationTimeUs/(100*1000);
    }

    /**
     * 根据秒数获取百分比
     * @param time 秒数
     * @return
     */
    private float getPercentForTime(float time){
        return time*(100*1000*1000)/mMovieEditor.getEditorTransCoder().getVideoInfo().durationTimeUs;
    }

    /**同步视图**/
    public void drawVideoThumb(Bitmap bitmap){
        getRangeLayout().drawVideoThumb(bitmap);
    }

    /** 设置裁剪控件开始与结束的最小间隔距离 */
    private void setBarSpace()
    {
        if(mMovieEditor.getEditorTransCoder().getVideoInfo() ==null || mMovieEditor.getEditorTransCoder().getVideoInfo().durationTimeUs == 0 ) return;
        if(mRangeSelectionBar!=null)
        {
            /**
             * 需求需要，需设定最小间隔为1秒的
             * 间隔距离，单位秒要转化为毫秒；
             */
            double percent = (1/mMovieEditor.getEditorTransCoder().getVideoInfo().durationTimeUs);
            seekBarWidth = seekBarWidth==0?640:seekBarWidth;
            int space = (int) (percent*seekBarWidth);

            mRangeSelectionBar.setCursorSpace(space);
        }
    }


    /** 输入框文本变化监听 */
    private TextWatcher mTextWatcher = new TextWatcher()
    {
        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count)
        {
            if(getActivity().getCurrentFocus() != getEditTextView()) return;

            // 此时输入框位置信息会发生变化, 因此需要在这里更新位置信息
            mYCoordinate = TuSdkViewHelper.locationInWindowTop(getEditTextView());

            if(mNeedReverse) text = reverseString(text.toString());
            updateText(text.toString(), mNeedReverse);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void afterTextChanged(Editable s)
        {
        }
    };

    /** 按钮触摸事件 */
    OnTouchListener mOnTouchListener = new OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            // 仅允许单点触摸
            if (event.getPointerCount() > 1) return false;

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    v.setSelected(true);
                    break;

                case MotionEvent.ACTION_UP:
                    handleStyleIndexButton(v);
                    v.setSelected(false);
                    break;

                case MotionEvent.ACTION_CANCEL:
                    v.setSelected(false);
                    break;
                default:
                    break;
            }
            return true;
        }
    };


    /** 点击样式栏中按钮事件 */
    private void handleStyleIndexButton(View v)
    {
        if (this.equalViewIds(v, this.getToLeftStyleButton()))
        {
            this.handleToLeftStyleButton();
        }
        else if (this.equalViewIds(v, this.getToRightStyleButton()))
        {
            this.handleToRightStyleButton();
        }
        else if (this.equalViewIds(v, this.getUnderlineStyleButton()))
        {
            this.handleUnderlineStyleButton();
        }
        else if (this.equalViewIds(v, this.getAlignLeftStyleButton()))
        {
            this.handleAlignLeftStyleButton();
        }
        else if (this.equalViewIds(v, this.getAlignRightStyleButton()))
        {
            this.handleAlignRightStyleButton();
        }
        else if (this.equalViewIds(v, this.getAlignCenterStyleButton()))
        {
            this.handleAlignCenterStyleButton();
        }
    }

    /** 按钮点击事件 */
    protected OnClickListener mButtonClickListener = new TuSdkViewHelper.OnSafeClickListener()
    {
        @Override
        public void onSafeClick(View v)
        {
            // 分发视图点击事件
            dispatcherViewClick(v);
        }
    };

    /** 分发视图点击事件 */
    protected void dispatcherViewClick(View v)
    {
        if (this.equalViewIds(v, this.getCancelButton()))
        {
            this.handleBackButton();
        }
        else if (this.equalViewIds(v, this.getCompleteButton()))
        {
            this.handleCompleteButton();
        }
        else if (this.equalViewIds(v, this.getAddStickerButton()))
        {
            this.handleAddStickerButton();
        }
        else if (this.equalViewIds(v, this.getColorButton()))
        {
            this.handleColorButton();
        }
        else if (this.equalViewIds(v, this.getStyleButton()))
        {
            this.handleStyleButton();
        }
        else if (this.equalViewIds(v, this.getParamBackButton()))
        {
            this.handleParamBackButton();
        }
        else if (v instanceof TuSdkTextButton)
        {
            selectedIndex(((TuSdkTextButton) v));
        }
    }

    /** 后退按钮 */
    protected void handleBackButton()
    {
        setVisibility(GONE);

        for (StickerItemViewInterface stickerTextItemView:getStickerView().getStickerItems())
        {
            StickerTextItemView stickerItemView =  ((StickerTextItemView)stickerTextItemView);
            stickerItemView.setVisibility(GONE);
        }

        getStickerView().cancelAllStickerSelected();
        getStickerView().getStickerItems().clear();
        mRangeSelectionBar.setLeftSelection(0);
        mRangeSelectionBar.setRightSelection(100);
        mMovieEditor.getEditorPlayer().startPreview();

    }

    protected void handleCompleteButton()
    {
        if (this.getStickerView() == null)
        {
            this.handleBackButton();
            return;
        }

        if (this.getStickerView() == null)
        {
            this.handleBackButton();
            return;
        }

        if(getOnTextStickerApplyListener()==null) return;

        for (StickerItemViewInterface stickerTextItemView:getStickerView().getStickerItems())
        {

            StickerTextItemView stickerItemView =  ((StickerTextItemView)stickerTextItemView);

            //生成图片前重置一些视图
            stickerItemView.resetRotation();
            stickerItemView.setStroke(getResColor(R.color.lsq_color_transparent),0);

            //生成相应的图片
            Bitmap textBitmap = StickerFactory.getBitmapForText(stickerItemView.getTextView());

            //获取计算相应的位置
            int[] locaiont = new int[2];
            stickerItemView.getTextView().getLocationOnScreen(locaiont);
            int pointX = locaiont[0] - getStickerView().getLeft();
            int pointY = locaiont[1] - getStickerView().getTop();

            //设置初始化的时间
            float starTime =((StickerTextData)stickerItemView.getSticker()).startime;
            float stopTime =((StickerTextData)stickerItemView.getSticker()).stopTime;

            //获取StickerView的画布大小
            TuSdkSize stickerSize = TuSdkSize.create(getStickerView().getWidth(),getStickerView().getHeight());

            //创建特效对象并且应用
            TuSDKMediaTextEffectData textMediaEffectData = createTextMeidEffectData(textBitmap,pointX,pointY,stickerItemView.getResult(null).degree,starTime,stopTime,stickerSize);
            getOnTextStickerApplyListener().onApply(textMediaEffectData);
            
            stickerItemView.setVisibility(GONE);
        }

        //清空重置相关数据
        getStickerView().cancelAllStickerSelected();
        getStickerView().getStickerItems().clear();
        mRangeSelectionBar.setLeftSelection(0);
        mRangeSelectionBar.setRightSelection(100);
        mMovieEditor.getEditorPlayer().startPreview();
        setVisibility(GONE);

    }


    // 当前所选中的颜色模式 (字体颜色或是背景色)
    private TuSdkTextButton mCurrentSelectedButton;

    /** 选中颜色调节栏中的 index */
    protected void selectedIndex(TuSdkTextButton button)
    {
        if(button.isSelected()) return;

        for(int i=0; i < getColorParamWrapLayout().getChildCount(); i++)
        {
            TuSdkTextButton btn = (TuSdkTextButton) getColorParamWrapLayout().getChildAt(i);
            btn.setSelected(btn.index == button.index);
        }

        mCurrentSelectedButton = button;
    }

    /** 打开样式设置界面 */
    protected void handleStyleButton()
    {
        showViewIn(getFeatureBar(), false);
        showViewIn(getStyleWrapLayout(), true);

        showViewIn(getColorBottomBar(), true);
    }

    /** 打开颜色设置界面 */
    protected void handleColorButton()
    {
        for(int i=0; i < getColorParamWrapLayout().getChildCount(); i++) createParamView(i);

        showViewIn(getFeatureBar(), false);
        showViewIn(getColorWrapLayout(), true);

        // 默认选中第一项
        selectedIndex((TuSdkTextButton) getColorParamWrapLayout().getChildAt(0));

        showViewIn(getColorBottomBar(), true);
    }

    /** 返回按钮 */
    protected void handleParamBackButton()
    {
        showViewIn(getTopBar(), true);
        showViewIn(getFeatureBar(), true);
        showViewIn(getColorWrapLayout(), false);
        showViewIn(getStyleWrapLayout(), false);
        showViewIn(getColorBottomBar(), false);
    }

    /** 添加文字贴纸按钮 */
    protected void handleAddStickerButton()
    {
        addStickerItemView();
    }

    /** 设置下划线 */
    protected void handleUnderlineStyleButton()
    {
        getStickerView().toggleTextUnderlineStyle();
    }

    /** 设置字体从右到左 */
    protected void handleToLeftStyleButton()
    {
        if(mNeedReverse) return;

        mNeedReverse = true;
        toggleTextReverse();
    }

    /** 设置字体从左到右 */
    protected void handleToRightStyleButton()
    {
        if(!mNeedReverse) return;

        mNeedReverse = false;
        toggleTextReverse();
    }

    /** 切换文本字符反转*/
    protected void toggleTextReverse()
    {
        if (this.getStickerView() == null) return;
        this.getStickerView().toggleTextReverse();
    }

    /** 字体左对齐按钮 */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void handleAlignLeftStyleButton()
    {
        getStickerView().changeTextAlignment(Gravity.START);
    }

    /** 字体右对齐按钮 */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void handleAlignRightStyleButton()
    {
        getStickerView().changeTextAlignment(Gravity.END);
    }

    /** 字体居中对齐按钮 */
    protected void handleAlignCenterStyleButton()
    {
        getStickerView().changeTextAlignment(Gravity.CENTER);
    }

    /********************************** Config ***********************************/
    // 文字内容
    private String mText;
    // 文字和边框间距 (默认: 10dp)
    private int mTextPaddings;
    // 文字大小 (默认: 20 SP)
    private int mTextSize;
    // 文字颜色 (默认:#FFFFFF)
    private String mTextColor;
    // 文字阴影颜色 (默认:#000000)
    private String mTextShadowColor;
    /** 贴纸视图委托 */
    private StickerViewDelegate mStickerViewDelegate;
    /** 输入法管理器 */
    private InputMethodManager mInputMethodManager;

    /** 颜色列表的高度 */
    private float mColorBarHeight;
    /** 颜色指示器宽度 */
    private float mColorIndicatorWidth;
    /** 颜色指示器高度 */
    private float mColorIndicatorHeight;
    /** 颜色列表顶部 paddding */
    private float mColorBarPaddingTop;
    /** 输入框y坐标 */
    int mYCoordinate;

    /** 贴纸视图委托 */
    public StickerViewDelegate getStickerViewDelegate()
    {
        if(mStickerViewDelegate == null) return this;

        return mStickerViewDelegate;
    }

    /** 贴纸视图委托 */
    public void setStickerViewDelegate(StickerViewDelegate mStickerViewDelegate)
    {
        this.mStickerViewDelegate = mStickerViewDelegate;
    }

    /**
     * 获取文字内容
     */
    public String getText()
    {
        return mText;
    }

    /** 设置文字内容 */
    public void setText(String mText)
    {
        this.mText = mText;
    }

    /** 文字和边框间距 (默认: 10dp)*/
    public int getTextPaddings()
    {
        if(mTextPaddings < 0)
        {
            mTextPaddings = 0;
        }

        return mTextPaddings;
    }

    /** 文字和边框间距 (默认: 10dp)*/
    public void setTextPaddings(int mTextPaddings)
    {
        this.mTextPaddings = mTextPaddings;
    }

    /** 文字大小 */
    public int getTextSize()
    {
        if(mTextSize < 0)
        {
            mTextSize = 0;
        }
        return mTextSize;
    }

    /** 文字大小 */
    public void setTextSize(int mTextSize)
    {
        this.mTextSize = mTextSize;
    }

    /** 文字颜色 (默认:#FFFFFF)*/
    public String getTextColor()
    {
        return mTextColor;
    }

    /**
     * 文字颜色 (默认:#FFFFFF)
     *
     * @param mTextColor
     *            文字颜色
     */
    public void setTextColor(String mTextColor)
    {
        if(mTextColor == null)
        {
            mTextColor = TuSdkContext.getString("lsq_text_sticker_text_color");
        }
        this.mTextColor = mTextColor;
    }

    /** 文字阴影颜色 (默认:#000000)*/
    public String getTextShadowColor()
    {
        if(mTextShadowColor == null)
        {
            mTextShadowColor = TuSdkContext.getString("lsq_text_sticker_text_shadow_color");
        }
        return mTextShadowColor;
    }

    /** 文字阴影颜色 (默认:#000000)*/
    public void setTextShadowColor(String mTextShadowColor)
    {
        this.mTextShadowColor = mTextShadowColor;
    }

    /** 颜色列表的高度 */
    public float getColorBarHeight()
    {
        if(this.mColorBarHeight < 0)
        {
            mColorBarHeight = TuSdkContext.dip2px(20);
        }
        return this.mColorBarHeight;
    }

    /** 颜色列表的高度 */
    public void setColorBarHeight(float height)
    {
        this.mColorBarHeight = height;
    }

    /** 颜色指示器宽度 */
    public float getColorIndicatorWidth()
    {
        if(this.mColorIndicatorHeight < 0)
        {
            mColorIndicatorHeight = TuSdkContext.dip2px(20);
        }

        return this.mColorIndicatorWidth;
    }

    /** 颜色指示器宽度 */
    public void setColorIndicatorWidth(float width)
    {

        this.mColorIndicatorWidth = width;
    }

    /** 颜色指示器高度 */
    public float getColorIndicatorHeight()
    {
        if(this.mColorIndicatorHeight < 0)
        {
            mColorIndicatorHeight = TuSdkContext.dip2px(20);
        }

        return this.mColorIndicatorHeight;
    }

    /** 颜色指示器高度 */
    public void setColorIndicatorHeight(float height)
    {
        this.mColorIndicatorHeight = height;
    }

    /** 颜色列表顶部 paddding */
    public float getColorBarPaddingTop()
    {
        return this.mColorBarPaddingTop;
    }

    /** 颜色列表顶部 paddding */
    public void setColorBarPaddingTop(float padding)
    {
        this.mColorBarPaddingTop = padding;
    }


    /********************************** loadView ***********************************/

    @Override
    public void loadView() {
        super.loadView();

        getStickerView();
        getCancelButton();
        getCompleteButton();
        getAddStickerButton();
        getEditTextView();
        getFeatureBar();
        getRangeLayout();

        getColorButton();
        getStyleButton();
        getTopBar();
        getColorBottomBar();

        getColorSelectorBar();
        showViewIn(getColorWrapLayout(), false);

        showViewIn(getStyleWrapLayout(), false);
        getToLeftStyleButton();
        getToRightStyleButton();
        getUnderlineStyleButton();
        getAlignLeftStyleButton();
        getAlignRightStyleButton();
        getAlignCenterStyleButton();

        // 初始化软键盘管理器
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // 对根视图设置全局监听
        setListenerToRootView();
    }

    /** 创建参数选项视图 */
    private TuSdkTextButton createParamView(int index)
    {
        TuSdkTextButton textButton = (TuSdkTextButton) getColorParamWrapLayout().getChildAt(index);
        int[] colors = new int[] {
                TuSdkContext.getColor("lsq_filter_config_highlight"), TuSdkContext.getColor("lsq_scence_effect_color_title") };
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_selected }, new int[] {} };

        ColorStateList colorStateList = new ColorStateList(states, colors);

        textButton.setTextColor(colorStateList);
        textButton.setGravity(Gravity.CENTER);
        textButton.setEllipsize(TextUtils.TruncateAt.END);
        textButton.setText(textButton.getText());
        textButton.setLines(1);
        textButton.index = index;
        textButton.setOnClickListener(mButtonClickListener);
        return textButton;
    }



    /** 是否需要反转字符串 */
    private boolean mNeedReverse;
    /** 是否点击了贴纸删除按钮或直接触摸屏幕其他区域*/
    private boolean isCancelAction;
    /** 贴纸数据*/
    private StickerTextData mStickerData;
    /** 贴纸文字*/
    private ArrayList<StickerText> mStickerTexts;

    /** 添加文本贴纸 */
    public void addStickerItemView()
    {
        mStickerData = new StickerTextData();
        /** 贴纸元素类型:1:图片贴纸 ,2:文字水印贴纸,3:动态贴纸 */
        mStickerData.stickerType = 2;
        mStickerTexts = new ArrayList<StickerText>();

        // 构建 StickerText 对象
        StickerText text = new StickerText();
        text.content = getText();
        text.textSize = getTextSize();
        text.color = getTextColor();
        text.paddings = getTextPaddings();
        text.shadowColor = getTextShadowColor();

        // 设置文字区域位置相对上边距百分比信息
        text.rectTop = 0;
        text.rectLeft = 0;
        text.rectWidth = 1.0f;
        text.rectHeight = 1.0f;

        mStickerTexts.add(text);

        //时间间隔为2s
        mStickerData.startime = 0;
        mStickerData.stopTime = 5;

        mStickerData.texts = mStickerTexts;

        mMovieEditor.getEditorPlayer().pausePreview();
        this.appendStickerItem(mStickerData);
    }

    /** 隐藏软键盘 */
    private void hideSoftInput()
    {
        View view = getActivity().getCurrentFocus();
        if(view == null) view = new View(getActivity());

        if(mInputMethodManager != null) mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /** 显示软键盘 */
    private void showSoftInput()
    {
        View view = getActivity().getCurrentFocus();
        if(view == null) view = new View(getActivity());

        if(mInputMethodManager != null) mInputMethodManager.showSoftInput(view, 0);
        isCancelAction = false;
    }



    public final void appendStickerItem(StickerData data)
    {
        if (data == null || this.getStickerView() == null) return;
        this.getStickerView().appendSticker(data);
    }

    public void setStickerView(StickerView stickerView){
        this.mStickerView = stickerView;
    }


    @Override
    public boolean canAppendSticker(StickerView view, StickerData sticker) {
        return true;
    }

    @Override
    public void onStickerItemViewSelected(String text, boolean needReverse) {
        changeEditTextOnStickerSelected(text, needReverse);
    }

    @Override
    public void onStickerItemViewReleased() {
        // 显示软键盘
        showSoftInput();
        showViewIn(getEditTextView(), true);
    }

    @Override
    public void onCancelAllStickerSelected() {
        isCancelAction = true;

        // 隐藏输入键盘和输入框
        hideSoftInput();
        getEditTextView().setVisibility(View.INVISIBLE);
    }

    /** 将字符串反转并返回 */
    private String reverseString(String text)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(text);
        stringBuilder.reverse();

        return stringBuilder.toString();
    }


    /** 选中文字贴纸时改变输入框内容 */
    private void changeEditTextOnStickerSelected(String text, boolean needReverse)
    {
        this.mNeedReverse = needReverse;

        if(needReverse) text = reverseString(text);

        // 如果贴纸文本没有被修改, 则输入框中置为空
        if(text.equals(getText())) text = "";
        getEditTextView().removeTextChangedListener(mTextWatcher);
        getEditTextView().setText(text);
        getEditTextView().setSelection(text.length());
        getEditTextView().addTextChangedListener(mTextWatcher);
        if(getStickerView().getCurrentItemViewSelected() instanceof StickerTextItemView){
            float startTime = ((StickerTextData) ((StickerTextItemView) getStickerView().getCurrentItemViewSelected()).getSticker()).startime;
            float stopTime = ((StickerTextData) ((StickerTextItemView)getStickerView().getCurrentItemViewSelected()).getSticker()).stopTime;

            getRangeLayout().setLeftSelection((int) getPercentForTime(startTime));
            getRangeLayout().setRightSelection((int) getPercentForTime(stopTime));
        }
        // 此时输入框位置信息会发生变化, 因此需要在这里更新位置信息
        mYCoordinate = TuSdkViewHelper.locationInWindowTop(getEditTextView());

    }

    /**
     * 播放游标显示状态
     * @return
     */
    public boolean isShowPlayCursor() {
        if(getRangeLayout()!= null){
            return getRangeLayout().isShowPlayCursor();
        }
        return false;
    }

    /**
     * 设置是否显示播放游标
     * @param b
     */
    public void setShowPlayCursor(boolean b) {
        if(getRangeLayout()!= null){
            getRangeLayout().setShowPlayCursor(b);
        }
    }

    /**
     * 设置显示的进度
     * @param percent
     */
    public void setPlaySelection(int percent) {
        if(getRangeLayout()!= null){
            getRangeLayout().setPlaySelection(percent);
        }
    }

    @Override
    protected void dispatchVisibilityChanged(View changedView, int visibility) {
        super.dispatchVisibilityChanged(changedView, visibility);

        if(changedView == this)
        {
            if(visibility == VISIBLE)
            {
                getTopBar().setVisibility(VISIBLE);

                if(getActivity() != null)
                {
                    ((MovieEditorActivity) getActivity()).getTopBar().setVisibility(INVISIBLE);
                }
            }
            else
            {
                getTopBar().setVisibility(GONE);

                if(getActivity() != null)
                {
                    ((MovieEditorActivity) getActivity()).getTopBar().setVisibility(VISIBLE);
                    ((MovieEditorActivity) getActivity()).backToPreviousEffect();
                }
            }
        }

    }

    /**
     * 将数据转成公用的 TuSDKMediaEffectData
     *
     * @param bitmap 文字生成的图片
     * @param offsetX 相对视频左上角X轴的位置
     * @param offsetY 相对视频左上角Y轴的位置
     * @param rotation  旋转的角度
     * @param startTime 文字特效开始的时间
     * @param stopTime 文字特效结束的时间
     * @param stickerSize  当前StickerView的宽高（计算比例用）
     * @return
     */
    protected TuSDKMediaTextEffectData createTextMeidEffectData(Bitmap bitmap, float offsetX, float offsetY, float rotation, float startTime, float stopTime, TuSdkSize stickerSize){
        TuSDKTextStickerImage stickerImage = new TuSDKTextStickerImage();
        TextStickerData stickerData = new TextStickerData(bitmap,bitmap.getWidth(),bitmap.getHeight(),0,offsetX,offsetY,rotation);
        stickerImage.setCurrentSticker(stickerData);
        //设置设计画布的宽高
        stickerImage.setDesignScreenSize(stickerSize);
        TuSDKMediaTextEffectData mediaTextEffectData = new TuSDKMediaTextEffectData(stickerImage);
        mediaTextEffectData.setAtTimeRange(TuSDKTimeRange.makeRange(startTime,stopTime));
        return mediaTextEffectData;
    }

    /**
     * 清除图片缓存
     */
    public void clearVideoThumbList() {
        if(mRangeSelectionBar!=null)
            mRangeSelectionBar.clearVideoThumbList();
    }
}