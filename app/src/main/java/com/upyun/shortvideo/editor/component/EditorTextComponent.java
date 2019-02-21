package com.upyun.shortvideo.editor.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.tusdk.textSticker.TextStickerData;
import org.lasque.tusdk.core.seles.tusdk.textSticker.TuSdkTextStickerImage;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.widget.TuSdkEditText;
import org.lasque.tusdk.impl.components.widget.sticker.StickerTextItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerFactory;
import org.lasque.tusdk.modules.view.widget.sticker.StickerItemViewInterface;
import org.lasque.tusdk.modules.view.widget.sticker.StickerText;
import org.lasque.tusdk.modules.view.widget.sticker.StickerTextData;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaTextEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;

import com.upyun.shortvideo.editor.MovieEditorController;
import com.upyun.shortvideo.views.editor.playview.TuSdkMovieScrollView;
import com.upyun.shortvideo.views.editor.playview.TuSdkRangeSelectionBar;
import com.upyun.shortvideo.views.editor.playview.rangeselect.TuSdkMovieColorGroupView;
import com.upyun.shortvideo.views.editor.playview.rangeselect.TuSdkMovieColorRectView;
import com.upyun.shortvideo.R;
import com.upyun.shortvideo.views.editor.LineView;
import com.upyun.shortvideo.views.editor.TuSdkMovieScrollPlayLineView;
import com.upyun.shortvideo.views.editor.color.ColorView;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/25 15:53
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 文字组件
 */
public class EditorTextComponent extends EditorComponent {
    private static final String TAG = "EditorTextComponent";

    /** 文字的底部View */
    private EditorTextBottomView mBottomView;
    /** 贴纸视图 */
    private StickerView mStickerView;
    /** 文字参数设置 */
    private EditorTextConfig mTextConfig;
    /** 文字输入框 */
    private TuSdkEditText mEditTextView;
    /** 贴纸数据 */
    private StickerTextData mStickerData;
    /** 贴纸文字 */
    private ArrayList<StickerText> mStickerTexts;
    /** 是否点击了贴纸删除按钮或直接触摸屏幕其他区域*/
    private boolean isCancelAction;
    /** 输入法管理器 */
    private InputMethodManager mInputMethodManager;
    /** 默认持续时间 **/
    private long defaultDurationUs =  1 * 1000000;
    /** 最小持续时间 **/
    private int minSelectTimeUs =  1 * 1000000;
    /** 文字特效备忘管理 **/
    private EditorTextBackups mTextBackups;
    /** 当前的颜色块 **/
    private TuSdkMovieColorRectView mCurrentColorRectView;
    /**
     * 显示区域改变回调
     *
     * @since V3.0.0
     */
    private TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener mOnDisplayChangeListener = new TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener() {
        @Override
        public void onPreviewSizeChanged(final TuSdkSize previewSize) {
            if (mStickerView == null) return;
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    mStickerView.resize(previewSize, getEditorController().getVideoContentView());
                }
            });

        }
    };

    /** 文字贴纸视图回调 */
    private StickerView.StickerViewDelegate mStickerDelegate = new StickerView.StickerViewDelegate() {
        @Override
        public boolean canAppendSticker(StickerView view, StickerData sticker) {
            return true;
        }

        @Override
        public void onStickerItemViewSelected(StickerData stickerData, String text, boolean needReverse) {
            if (stickerData != null && stickerData instanceof StickerTextData) {
                mBottomView.mLineView.setShowSelectBar(true);
                mStickerData = (StickerTextData) stickerData;
                mBottomView.mLineView.setLeftBarPosition(((StickerTextData) stickerData).starTimeUs/(float)getEditorPlayer().getTotalTimeUs());
                mBottomView.mLineView.setRightBarPosition(((StickerTextData) stickerData).stopTimeUs/(float)getEditorPlayer().getTotalTimeUs());
                mCurrentColorRectView = mTextBackups.findColorRect(stickerData);
                mBottomView.mBottomOptions.setOptionsEnable(true);
            }
        }

        @Override
        public void onStickerItemViewReleased() {
            showSoftInput();
            getEditTextView().setVisibility(View.VISIBLE);

        }

        @Override
        public void onCancelAllStickerSelected() {
            hideSoftInput();
            getEditTextView().setVisibility(View.GONE);
            if(mBottomView != null) mBottomView.mLineView.setShowSelectBar(false);
            if(mBottomView != null) mBottomView.mBottomOptions.setOptionsEnable(false);
        }

        @Override
        public void onStickerCountChanged(StickerData stickerData, final StickerItemViewInterface stickerItemViewInterface, int operation, int count) {
            if (mBottomView == null || mBottomView.mBottomOptions == null) return;
            mBottomView.mBottomOptions.setOptionsEnable(count > 0);
            if(operation == 0) {
                mTextBackups.removeBackupEntityWithSticker((StickerTextItemView) stickerItemViewInterface);
                mBottomView.getLineView().setShowSelectBar(false);
            } else{
                mBottomView.getLineView().setShowSelectBar(true);
                float startPercent = mBottomView.mLineView.getCurrentPercent();
                float endPercent = ((StickerTextData)stickerData).stopTimeUs/(float)getEditorPlayer().getTotalTimeUs();
                TuSdkMovieColorRectView rectView = mBottomView.mLineView.recoverColorRect(R.color.lsq_scence_effect_color_EdgeMagic01,startPercent,endPercent);
                mCurrentColorRectView = rectView;
                mTextBackups.addBackupEntity(EditorTextBackups.createBackUpEntity(stickerData,(StickerTextItemView) stickerItemViewInterface,rectView));
            }
        }
    };

    /** 播放状态和进度回调 */
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
            if (mBottomView == null) return;
            mBottomView.setPlayState(state);
        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if (mBottomView == null || isAnimationStaring ) return;
            mBottomView.mLineView.seekTo(percentage);
        }
    };

    /** 最大值最小值判断 **/
    private TuSdkRangeSelectionBar.OnExceedCriticalValueListener mOnExceedValueListener = new TuSdkRangeSelectionBar.OnExceedCriticalValueListener() {
        @Override
        public void onMaxValueExceed() {
            TuSdk.messageHub().showToast(getBottomView().getContext(),R.string.lsq_max_time_effect);
        }

        @Override
        public void onMinValueExceed() {
            TuSdk.messageHub().showToast(getBottomView().getContext(),R.string.lsq_min_time_effect);
        }
    };


    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorTextComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.Text;
        mStickerView = editorController.getActivity().getStickerView();
        mStickerView.setDelegate(mStickerDelegate);

        getBottomView();
        mTextBackups = new EditorTextBackups(mStickerView,mBottomView,getEditorEffector());
        mTextBackups.setLineView(mBottomView.mLineView);

        getEditorPlayer().addPreviewSizeChangeListener(mOnDisplayChangeListener);
        getEditorPlayer().addProgressListener(mPlayProgressListener);

        // 初始化软键盘管理器
        mInputMethodManager = (InputMethodManager)editorController.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // 对根视图设置全局监听
        setListenerToRootView();
    }

    @Override
    public void attach() {
        getEditorController().getBottomView().addView(getBottomView());
        // 暂停
        TLog.e("attach()");
        getEditorPlayer().pausePreview();

        getEditorController().getVideoContentView().setClickable(false);
        getEditorController().getPlayBtn().setVisibility(View.GONE);

        mBottomView.mBottomOptions.setOptionsEnable(false);
        mBottomView.mLineView.setShowSelectBar(false);
        mBottomView.setPlayState(1);

    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        TLog.e("onAnimationStart()");
        if(getEditorPlayer().isReversing()) {
            mBottomView.mLineView.seekTo(1f);
        }else {
            mBottomView.mLineView.seekTo(0f);
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        getEditorPlayer().seekOutputTimeUs(0);
        if(getEditorPlayer().isReversing()) {
            mBottomView.mLineView.seekTo(1f);
        }else {
            mBottomView.mLineView.seekTo(0f);
        }
    }

    public void backUpDatas(){
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeText);
        ThreadHelper.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mTextBackups == null)return;
                mTextBackups.onComponentAttach();
            }
        },50);

    }

    @Override
    public void detach() {
        mBottomView.setPlayState(1);
        getEditorPlayer().seekOutputTimeUs(0);
        getEditorController().getPlayBtn().setVisibility(View.VISIBLE);
        getEditorController().getVideoContentView().setClickable(true);
        mBottomView.mBottomOptions.backToOptionView();

        mTextBackups.onComponentDetach();
    }


    @Override
    public View getHeaderView() {
        return null;
    }

    @Override
    public View getBottomView() {
        if(mBottomView == null)
            mBottomView = new EditorTextBottomView();
        return mBottomView.mBottomView;
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {
        getBottomView();
        mBottomView.getLineView().addBitmap(bitmap);

    }
    /**
     * 设置文字参数
     *
     * @param textConfig
     */
    public void setTextConfig(EditorTextConfig textConfig) {
        this.mTextConfig = textConfig;
    }


    /** 文字输入框 **/
    public TuSdkEditText getEditTextView(){
        if (mEditTextView == null)
        {
            mEditTextView = getEditorController().getActivity().findViewById(R.id.lsq_editTextView);
            if (mEditTextView != null)
            {
                mEditTextView.addTextChangedListener(mTextWatcher);
                mEditTextView.setVisibility(View.GONE);
            }
        }
        return mEditTextView;
    }


    /** 隐藏软键盘 */
    private void hideSoftInput()
    {
        View view = getEditorController().getActivity().getCurrentFocus();
        if(view == null) view = new View(getEditorController().getActivity());

        if(mInputMethodManager != null) mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /** 显示软键盘 */
    private void showSoftInput()
    {
        View view = getEditorController().getActivity().getCurrentFocus();
        if(view == null) view = new View(getEditorController().getActivity());

        if(mInputMethodManager != null) mInputMethodManager.showSoftInput(view, 0);
        isCancelAction = false;
    }

    /** 对根视图设置全局布局监听,以间接实现监听键盘隐藏显示状态*/
    private void setListenerToRootView()
    {
        final View rootView = getEditorController().getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

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

    /** 输入框文本变化监听 */
    private TextWatcher mTextWatcher = new TextWatcher()
    {
        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count)
        {
            if(getEditorController().getActivity().getCurrentFocus() != getEditTextView()) return;

            if(mBottomView.mBottomOptions.mStyleOptions == null)return;
            boolean isReverse = mBottomView.mBottomOptions.mStyleOptions.mNeedReverse;
            if(isReverse) text = reverseString(text.toString());
            updateText(text.toString(), isReverse);
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



    /** 将字符串反转并返回 */
    private String reverseString(String text)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(text);
        stringBuilder.reverse();

        return stringBuilder.toString();
    }

    /** 更新文字贴纸视图中的内容*/
    public void updateText(String text, boolean needReverse)
    {
        if(text == null) return;

        this.mStickerView.updateText(text, needReverse);
    }


    /** 文字设置 **/
    public static class EditorTextConfig {
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

        public static EditorTextConfig creat() {
            return new EditorTextConfig();
        }

        /**
         * 设置文字
         *
         * @param text 显示的文字
         * @since V3.0.0
         */
        public EditorTextConfig setText(String text) {
            this.mText = text;
            return this;
        }

        /**
         * 设置文字和边框的间距
         *
         * @param padding
         * @since V3.0.0
         */
        public EditorTextConfig setTextPadding(int padding) {
            this.mTextPaddings = padding;
            return this;
        }

        /**
         * 设置文字大小
         *
         * @param textSize
         * @since V3.0.0
         */
        public EditorTextConfig setTextSize(int textSize) {
            this.mTextSize = textSize;
            return this;
        }

        /**
         * 设置文字颜色
         *
         * @param color 文字颜色
         * @since V3.0.0
         */
        public EditorTextConfig setTextColor(String color) {
            this.mTextColor = color;
            return this;
        }

        public EditorTextConfig setTextShadowColor(String color) {
            this.mTextShadowColor = color;
            return this;
        }
    }

    //底部View
    class EditorTextBottomView {
        private View mBottomView;

        private FrameLayout mControlContent;
        //返回按钮
        private ImageButton mBackBtn;
        //确定按钮
        private ImageButton mNextBtn;
        //播放|暂停 按钮
        private ImageView mPlayBtn;
        //进度以及区间选择View
        private TuSdkMovieScrollPlayLineView mLineView;

        //选项View
        private EditorBottomOptions mBottomOptions;

        public EditorTextBottomView() {
            mBottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_bottom, null);
            initView();
        }

        private void initView() {
            mControlContent = mBottomView.findViewById(R.id.lsq_editor_text_cotroll_content);

            mBottomOptions = new EditorBottomOptions();
            mControlContent.addView(mBottomOptions.mOptionView);

            mBackBtn = mBottomView.findViewById(R.id.lsq_text_close);
            mNextBtn = mBottomView.findViewById(R.id.lsq_text_sure);
            mPlayBtn = mBottomView.findViewById(R.id.lsq_editor_text_play);
            mLineView = mBottomView.findViewById(R.id.lsq_editor_text_play_range);

            mBackBtn.setOnClickListener(mOnClickListener);
            mNextBtn.setOnClickListener(mOnClickListener);
            mPlayBtn.setOnClickListener(mOnClickListener);
            mLineView.setType(1);
            mLineView.setOnProgressChangedListener(mOnScrollingPlayPositionListener);
            mLineView.setSelectRangeChangedListener(mOnSelectTimeChangeListener);
            mLineView.setOnTouchSelectBarListener(mOnTouchSelectBarlistener);
            mLineView.setOnSelectColorRectListener(mSelectColorListener);
            mLineView.setExceedCriticalValueListener(mOnExceedValueListener);
            float minPercent = minSelectTimeUs/(float)getEditorPlayer().getTotalTimeUs();
            mLineView.setMinWidth(minPercent);
        }

        //获取底部View
        private View getBottomView() {
            return mBottomView;
        }

        //View的点击事件
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_text_close:
                        mTextBackups.onBackEffect();
                        mCurrentColorRectView = null;
                        mBottomOptions.mStyleOptions.mNeedReverse = false;
                        mEditTextView.setText("");
                        getEditorController().onBackEvent();
                        break;
                    case R.id.lsq_text_sure:
                        mTextBackups.onApplyEffect();
                        handleCompleted();
                        mCurrentColorRectView = null;
                        mBottomOptions.mStyleOptions.mNeedReverse = false;
                        mEditTextView.setText("");
                        mStickerData = null;
                        mBottomOptions.setOptionsEnable(false);
                        getEditorController().onBackEvent();
                        break;
                    case R.id.lsq_editor_text_play:
                        if (getEditorPlayer().isPause())
                            getEditorPlayer().startPreview();
                        else
                            getEditorPlayer().pausePreview();
                        break;
                }
            }
        };

        protected void handleCompleted() {
            for (StickerItemViewInterface stickerTextItemView : mStickerView.getStickerItems()) {

                StickerTextItemView stickerItemView = ((StickerTextItemView) stickerTextItemView);

                //生成图片前重置一些视图
                stickerItemView.resetRotation();
                stickerItemView.setStroke(TuSdkContext.getColor(R.color.lsq_color_transparent), 0);

                //生成相应的图片
                Bitmap textBitmap = StickerFactory.getBitmapForText(stickerItemView.getTextView()).copy(Bitmap.Config.ARGB_8888, false);

                stickerItemView.setStroke(TuSdkContext.getColor(R.color.lsq_color_white), 2);
                //获取计算相应的位置
                int[] locaiont = new int[2];
                stickerItemView.getTextView().getLocationOnScreen(locaiont);
                int pointX = locaiont[0] - mStickerView.getLeft();
                int pointY = locaiont[1] - mStickerView.getTop();

                //设置初始化的时间

                long starTimeUs = ((StickerTextData) stickerItemView.getSticker()).starTimeUs;
                long stopTimeUs = ((StickerTextData) stickerItemView.getSticker()).stopTimeUs;

                //获取StickerView的画布大小
                TuSdkSize stickerSize = TuSdkSize.create(mStickerView.getWidth(), mStickerView.getHeight());

                //创建特效对象并且应用
                TuSdkMediaTextEffectData textMediaEffectData = createTextMeidEffectData(stickerItemView.getSticker(),
                        textBitmap, pointX, pointY, stickerItemView.getResult(null).degree,
                        starTimeUs, stopTimeUs, stickerSize);
                getEditorEffector().addMediaEffectData(textMediaEffectData);

                EditorTextBackups.TextBackupEntity  backupEntity = mTextBackups.findTextBackupEntity(stickerItemView);
                if(backupEntity != null) backupEntity.textMediaEffectData = textMediaEffectData;

                stickerItemView.setVisibility(GONE);
            }

            //清空重置相关数据
            mStickerView.cancelAllStickerSelected();
            mStickerView.removeAllSticker();
        }

        /**
         * 将数据转成公用的 TuSdkMediaEffectData
         *
         * @param sticker     贴纸数据
         * @param bitmap      文字生成的图片
         * @param offsetX     相对视频左上角X轴的位置
         * @param offsetY     相对视频左上角Y轴的位置
         * @param rotation    旋转的角度
         * @param startTimeUs   文字特效开始的时间
         * @param stopTimeUs    文字特效结束的时间
         * @param stickerSize 当前StickerView的宽高（计算比例用）
         * @return
         */
        protected TuSdkMediaTextEffectData createTextMeidEffectData(StickerData sticker, Bitmap bitmap, float offsetX, float offsetY, float rotation, long startTimeUs, long stopTimeUs, TuSdkSize stickerSize) {
            TuSdkTextStickerImage stickerImage = new TuSdkTextStickerImage();
            TextStickerData stickerData = new TextStickerData(bitmap, bitmap.getWidth(), bitmap.getHeight(), 0, offsetX, offsetY, rotation);
            stickerImage.setCurrentSticker(stickerData);
            //设置设计画布的宽高
            stickerImage.setDesignScreenSize(stickerSize);
            TuSdkMediaTextEffectData mediaTextEffectData = new TuSdkMediaTextEffectData(stickerImage);
            mediaTextEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(startTimeUs, stopTimeUs));
            return mediaTextEffectData;
        }


        //播放指针 位置改变监听
        private LineView.OnPlayPointerChangeListener mOnPlayPointerChangeListener = new LineView.OnPlayPointerChangeListener() {
            @Override
            public void onPlayPointerPosition(long playPointerPositionTimeUs, float playPointerPositionTimePercent) {
                TLog.d("%s OnPlayPointerChangeListener ", TAG);

            }
        };

        //时间区间选择
        private LineView.OnSelectCoverTimeListener mOnSelectCoverTimeListener = new LineView.OnSelectCoverTimeListener() {
            @Override
            public void onCoverSelectTime(long startTimeUs, long endTimeUs, float startTimePercent, float endTimePercent) {
                TLog.d("%s OnSelectCoverTimeListener", TAG);
            }
        };

        public TuSdkMovieScrollPlayLineView getLineView() {
            return mLineView;
        }


        private TuSdkMovieScrollView.OnProgressChangedListener mOnScrollingPlayPositionListener = new TuSdkMovieScrollView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float progress, boolean isTouching) {
                long playPositionTime = (long) (progress * getEditorPlayer().getTotalTimeUs());
                for (StickerItemViewInterface itemViewInterface : mStickerView.getStickerItems()) {
                    StickerTextItemView itemView = (StickerTextItemView) itemViewInterface;
                    if (!(itemView.getSticker() instanceof StickerTextData)) continue;
                    StickerTextData textData = (StickerTextData) itemView.getSticker();
                    if (textData.isContains(playPositionTime)) {
                        itemView.setVisibility(View.VISIBLE);
                    } else {
                        itemView.setVisibility(View.GONE);
                    }
                }

                if(!isTouching)return;
                if(isTouching){
                    getEditorPlayer().pausePreview();
                }

                if (getEditorPlayer().isPause())
                    getEditorPlayer().seekOutputTimeUs(playPositionTime);

            }

            @Override
            public void onCancelSeek() {

            }
        };

        private TuSdkRangeSelectionBar.OnSelectRangeChangedListener mOnSelectTimeChangeListener = new TuSdkRangeSelectionBar.OnSelectRangeChangedListener() {
            @Override
            public void onSelectRangeChanged(float leftPercent, float rightPerchent, int type) {
                if (mStickerData == null) return;
                if(type == 0){
                    mStickerData.starTimeUs = (long) (leftPercent * getEditorPlayer().getTotalTimeUs());
                }else if(type == 1) {
                    mStickerData.stopTimeUs = (long) (rightPerchent * getEditorPlayer().getTotalTimeUs());
                }
                mLineView.changeColorRect(mCurrentColorRectView,leftPercent,rightPerchent);
            }
        };


        private TuSdkRangeSelectionBar.OnTouchSelectBarListener mOnTouchSelectBarlistener = new TuSdkRangeSelectionBar.OnTouchSelectBarListener() {
            @Override
            public void onTouchBar(float leftPercent, float rightPerchent, int type) {
                if(type == 0){
                    mLineView.seekTo(leftPercent);
                }else if(type == 1) {
                    mLineView.seekTo(rightPerchent);
                }
            }
        };


        private TuSdkMovieColorGroupView.OnSelectColorRectListener mSelectColorListener = new TuSdkMovieColorGroupView.OnSelectColorRectListener() {
            @Override
            public void onSelectColorRect(final TuSdkMovieColorRectView rectView) {

                if(rectView == null){
                    mLineView.setShowSelectBar(false);
                    mStickerView.cancelAllStickerSelected();
                }
                if(mStickerView.getStickerItems().size() == 0)return;
                final StickerTextData stickerData = (StickerTextData) mTextBackups.findStickerData(rectView);
//                if(rectView == mCurrentColorRectView)return;

                if (stickerData != null && stickerData instanceof StickerTextData) {
                    mLineView.setShowSelectBar(true);
                    mStickerData = stickerData;
                    final float leftPercent =  stickerData.starTimeUs/(float)getEditorPlayer().getTotalTimeUs();
                    float rightPercent =stickerData.stopTimeUs/(float)getEditorPlayer().getTotalTimeUs();
                    mLineView.setLeftBarPosition(leftPercent);
                    mLineView.setRightBarPosition(rightPercent);

                    if(mCurrentColorRectView == rectView)return;

                    mCurrentColorRectView = rectView;
                    ThreadHelper.post(new Runnable() {
                        @Override
                        public void run() {
                            mLineView.seekTo(rectView.getStartPercent()+0.002f);
                        }
                    });
                }

                if(mTextBackups.findStickerItem(rectView) != null){
                    mStickerView.onStickerItemViewSelected(mTextBackups.findStickerItem(rectView));
                    mTextBackups.findStickerItem(rectView).setSelected(true);
                }
            }
        };

        /**
         * 设置播放状态
         *
         * @param state 0 播放  1 暂停
         * @since V3.0.0
         */
        public void setPlayState(int state) {
            if(state == 1) getEditorPlayer().pausePreview();
            else getEditorPlayer().startPreview();

            mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(state == 0 ? R.drawable.edit_ic_pause : R.drawable.edit_ic_play));
            mBottomOptions.setOptionsEnable(state == 1 && mStickerView.stickersCount() > 0 && mStickerData != null);
            mBottomOptions.setAddBtnEnable(state == 1);
            mBottomOptions.backToOptionView();
        }
    }

    //底部文字选项View
    class EditorBottomOptions {
        private View mOptionView;
        //文字添加按钮
        private ImageView mTextAddBtn;
        //文字字体按钮
        private ImageView mTextFontBtn;
        //文字颜色按钮
        private ImageView mTextColorBtn;
        //文字样式按钮
        private ImageView mTextStyleBtn;

        //字体选项
        private EditorBottomFontOption mFontOptions;
        //颜色选项
        private EditorBottomColorOption mColorOptions;
        //风格选项
        private EditorBottomStyleOption mStyleOptions;
        //是否可以回退为选项视图
        private boolean isCanBackToOptionView = false;

        public EditorBottomOptions() {
            mOptionView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_options, null);
            initView();
        }

        private void initView() {
            mFontOptions = new EditorBottomFontOption();
            mColorOptions = new EditorBottomColorOption();
            mStyleOptions = new EditorBottomStyleOption();

            mTextAddBtn = findViewById(R.id.lsq_editor_options_add);
            mTextFontBtn = findViewById(R.id.lsq_editor_options_font);
            mTextColorBtn = findViewById(R.id.lsq_editor_options_color);
            mTextStyleBtn = findViewById(R.id.lsq_editor_options_style);

            mTextAddBtn.setOnClickListener(mOnClickListener);
            mTextFontBtn.setOnClickListener(mOnClickListener);
            mTextColorBtn.setOnClickListener(mOnClickListener);
            mTextStyleBtn.setOnClickListener(mOnClickListener);

            setOptionsEnable(false);
        }

        private <T extends View> T findViewById(@IdRes int id) {
            return mOptionView.findViewById(id);
        }

        /**
         * 设置是否启用
         *
         * @param isEnable
         */
        public void setOptionsEnable(boolean isEnable) {
            mTextFontBtn.setClickable(isEnable);
            mTextColorBtn.setClickable(isEnable);
            mTextStyleBtn.setClickable(isEnable);

            mTextFontBtn.setImageResource(isEnable ? R.drawable.edit_ic_font : R.drawable.edit_ic_font_disabled);
            mTextColorBtn.setImageResource(isEnable ? R.drawable.edit_ic_colour : R.drawable.edit_ic_colour_disabled);
            mTextStyleBtn.setImageResource(isEnable ? R.drawable.edit_ic_style : R.drawable.edit_ic_style_disable);
        }

        /**
         * 设置添加按钮是否启用
         *
         * @param isEnable
         */
        public void setAddBtnEnable(boolean isEnable) {
            mTextAddBtn.setClickable(isEnable);
            mTextAddBtn.setAlpha(isEnable ? 1 : 0.3f);
        }

        //点击事件
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_editor_options_add:
                        if(!mBottomView.mLineView.isShowSelectBar())
                        mBottomView.mLineView.setShowSelectBar(true);
                        addStickerItemView();
                        break;
                    case R.id.lsq_editor_options_font:
                        isCanBackToOptionView = true;
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mControlContent.addView(mFontOptions.mFontView);
                        break;
                    case R.id.lsq_editor_options_color:
                        isCanBackToOptionView = true;
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mControlContent.addView(mColorOptions.mColorView);
                        break;
                    case R.id.lsq_editor_options_style:
                        isCanBackToOptionView = true;
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mControlContent.addView(mStyleOptions.mStyleView);
                        break;
                }
            }
        };

        /** 回退到选项View **/
        public void backToOptionView() {
            if (!isCanBackToOptionView) return;
            mBottomView.mControlContent.removeAllViews();
            isCanBackToOptionView = true;
            mBottomView.mControlContent.addView(mOptionView);
        }

        /** 添加文本贴纸 */
        public void addStickerItemView() {
            mCurrentColorRectView = null;
            mStickerData = new StickerTextData();
            /** 贴纸元素类型:1:图片贴纸 ,2:文字水印贴纸,3:动态贴纸 */
            mStickerData.stickerType = 2;
            mStickerTexts = new ArrayList<StickerText>();

            // 构建 StickerText 对象
            StickerText text = new StickerText();
            text.content = mTextConfig.mText;
            text.textSize = mTextConfig.mTextSize;
            text.color = mTextConfig.mTextColor;
            text.paddings = mTextConfig.mTextPaddings;
            text.shadowColor = mTextConfig.mTextShadowColor;

            // 设置文字区域位置相对上边距百分比信息
            text.rectTop = 0f;
            text.rectLeft = 0f;
            text.rectWidth = 1.0f;
            text.rectHeight = 1.0f;

            mStickerTexts.add(text);

            //时间间隔为2s
            mStickerData.starTimeUs = (long) (mBottomView.mLineView.getCurrentPercent() * getEditorPlayer().getInputTotalTimeUs());
            if(mStickerData.starTimeUs + defaultDurationUs > getEditorPlayer().getInputTotalTimeUs() ){
                mStickerData.stopTimeUs = getEditorPlayer().getOutputTotalTimeUS();
            }else {
                mStickerData.stopTimeUs = mStickerData.starTimeUs + defaultDurationUs;
            }

            if (mBottomView != null && mBottomView.mLineView != null) {
                mBottomView.mLineView.setLeftBarPosition(mStickerData.starTimeUs/(float)getEditorPlayer().getTotalTimeUs());
                mBottomView.mLineView.setRightBarPosition(mStickerData.stopTimeUs/(float)getEditorPlayer().getTotalTimeUs());
            }

            mStickerData.texts = mStickerTexts;

            getEditorPlayer().pausePreview();
            mStickerView.appendSticker(mStickerData);
        }

    }

    /** 底部文字字体选项 */
    class EditorBottomFontOption {
        private View mFontView;
        //返回按钮
        private ImageView mReturn;
        //默认字体
        private LinearLayout mNormalFontBtn;
        //serif字体
        private LinearLayout mSerifFontBtn;


        public EditorBottomFontOption() {
            mFontView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_font, null);
            initView();
        }

        private void initView() {
            mReturn = findViewById(R.id.lsq_editor_component_text_font_back);
            mNormalFontBtn = findViewById(R.id.lsq_editor_component_text_font_normal);
            mSerifFontBtn = findViewById(R.id.lsq_editor_component_text_font_serif);

            mReturn.setOnClickListener(mOnClickListener);
            mNormalFontBtn.setOnClickListener(mOnClickListener);
            mSerifFontBtn.setOnClickListener(mOnClickListener);
        }

        private <T extends View> T findViewById(@IdRes int id) {
            return mFontView.findViewById(id);
        }

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_editor_component_text_font_back:
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mBottomOptions.isCanBackToOptionView = true;
                        mBottomView.mControlContent.addView(mBottomView.mBottomOptions.mOptionView);
                        break;
                    case R.id.lsq_editor_component_text_font_normal:
                        if (mStickerTexts == null || mStickerTexts.size() == 0 || mStickerView.getCurrentItemViewSelected() == null)
                            return;
                        ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).setTextFont(Typeface.DEFAULT);
                        break;
                    case R.id.lsq_editor_component_text_font_serif:
                        if (mStickerTexts == null || mStickerTexts.size() == 0 || mStickerView.getCurrentItemViewSelected() == null)
                            return;
                        ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).setTextFont(Typeface.SERIF);
                        break;
                }
            }
        };
    }

    /** 颜色选项 **/
    class EditorBottomColorOption {
        private View mColorView;

        private ImageView mBackButton;
        //字体
        private ColorView mFontSeek;
        //背景
        private ColorView mBackGroundSeek;
        //线条
        private ColorView mLineSeek;

        public EditorBottomColorOption() {
            mColorView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_color, null);
            initView();
        }

        private void initView() {
            mBackButton = findViewById(R.id.lsq_editor_component_text_font_back);
            mFontSeek = findViewById(R.id.lsq_editor_text_color_seek_font);
            mBackGroundSeek = findViewById(R.id.lsq_editor_text_color_seek_background);
            mLineSeek = findViewById(R.id.lsq_editor_text_color_seek_line);

            mBackButton.setOnClickListener(mOnClickListener);
            mFontSeek.setOnColorChangeListener(mOnFontColorChangeListener);
            mBackGroundSeek.setOnColorChangeListener(mOnBackGroundColorChangeListener);
            mLineSeek.setOnColorChangeListener(mOnLineColorChangeListener);
        }

        private <T extends View> T findViewById(@IdRes int id) {
            return mColorView.findViewById(id);
        }

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomView.mControlContent.removeAllViews();
                mBottomView.mBottomOptions.isCanBackToOptionView = true;
                mBottomView.mControlContent.addView(mBottomView.mBottomOptions.mOptionView);
            }
        };

        /**
         * 字体颜色改变回调
         */
        private ColorView.OnColorChangeListener mOnFontColorChangeListener = new ColorView.OnColorChangeListener() {
            @Override
            public void changeColor(int colorId) {
                if (mStickerView.getCurrentItemViewSelected() == null) return;
                StickerTextItemView itemView = (StickerTextItemView) mStickerView.getCurrentItemViewSelected();
                itemView.onSelectedColorChanged(0, colorId);
            }

            @Override
            public void changePosition(float percent) {

            }
        };

        /**
         * 背景颜色回调
         */
        private ColorView.OnColorChangeListener mOnBackGroundColorChangeListener = new ColorView.OnColorChangeListener() {
            @Override
            public void changeColor(int colorId) {
                if (mStickerView.getCurrentItemViewSelected() == null) return;
                StickerTextItemView itemView = (StickerTextItemView) mStickerView.getCurrentItemViewSelected();
                itemView.onSelectedColorChanged(1, colorId);
            }

            @Override
            public void changePosition(float percent) {

            }
        };

        /**
         * 线颜色回调
         */
        private ColorView.OnColorChangeListener mOnLineColorChangeListener = new ColorView.OnColorChangeListener() {
            @Override
            public void changeColor(int colorId) {
                if (mStickerView.getCurrentItemViewSelected() == null) return;
                StickerTextItemView itemView = (StickerTextItemView) mStickerView.getCurrentItemViewSelected();
                itemView.onSelectedColorChanged(2, colorId);
            }

            @Override
            public void changePosition(float percent) {

            }
        };
    }

    /** 风格选项 */
    class EditorBottomStyleOption {
        private View mStyleView;
        private ImageView mBackButton;
        private LinearLayout mLeftAlignButton;
        private LinearLayout mCenterAlignButton;
        private LinearLayout mRightAlignButton;
        private LinearLayout mUnderlineButton;
        private LinearLayout mLeftToRightButton;
        private LinearLayout mRightToLeftButton;

        private boolean mNeedReverse;

        public EditorBottomStyleOption() {
            mStyleView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_style, null);
            initView();
        }

        private void initView() {
            mBackButton = findViewById(R.id.lsq_editor_component_text_font_back);
            mLeftAlignButton = findViewById(R.id.lsq_editor_text_leftalignment);
            mCenterAlignButton = findViewById(R.id.lsq_editor_text_centeralignment);
            mRightAlignButton = findViewById(R.id.lsq_editor_text_rightalignment);
            mUnderlineButton = findViewById(R.id.lsq_editor_text_underline);
            mLeftToRightButton = findViewById(R.id.lsq_editor_text_lefttoright);
            mRightToLeftButton = findViewById(R.id.lsq_editor_text_righttoleft);

            mBackButton.setOnClickListener(mOnClickListener);
            mLeftAlignButton.setOnClickListener(mOnClickListener);
            mCenterAlignButton.setOnClickListener(mOnClickListener);
            mRightAlignButton.setOnClickListener(mOnClickListener);
            mUnderlineButton.setOnClickListener(mOnClickListener);
            mLeftToRightButton.setOnClickListener(mOnClickListener);
            mRightToLeftButton.setOnClickListener(mOnClickListener);

        }

        private <T extends View> T findViewById(@IdRes int id) {
            return mStyleView.findViewById(id);
        }

        /** **/
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_editor_component_text_font_back:
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mBottomOptions.isCanBackToOptionView = true;
                        mBottomView.mControlContent.addView(mBottomView.mBottomOptions.mOptionView);
                        break;
                    case R.id.lsq_editor_text_leftalignment:
                        mStickerView.changeTextAlignment(Gravity.LEFT);
                        break;
                    case R.id.lsq_editor_text_centeralignment:
                        mStickerView.changeTextAlignment(Gravity.CENTER);
                        break;
                    case R.id.lsq_editor_text_rightalignment:
                        mStickerView.changeTextAlignment(Gravity.RIGHT);
                        break;
                    case R.id.lsq_editor_text_underline:
                        mStickerView.toggleTextUnderlineStyle();
                        break;
                    case R.id.lsq_editor_text_lefttoright:
                        if (mNeedReverse) return;

                        mNeedReverse = true;
                        mStickerView.toggleTextReverse();
                        break;
                    case R.id.lsq_editor_text_righttoleft:
                        if (!mNeedReverse) return;

                        mNeedReverse = false;
                        mStickerView.toggleTextReverse();
                        break;
                }
            }
        };
    }
}
