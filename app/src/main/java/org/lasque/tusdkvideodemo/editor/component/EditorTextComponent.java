package org.lasque.tusdkvideodemo.editor.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.ColorUtils;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.widget.TuSdkEditText;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;
import org.lasque.tusdk.impl.components.widget.sticker.StickerDynamicItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerImageItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerTextItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdk.impl.view.widget.TuSeekBar;
import org.lasque.tusdk.modules.view.widget.sticker.StickerData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerDynamicData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerFactory;
import org.lasque.tusdk.modules.view.widget.sticker.StickerImageData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerItemViewInterface;
import org.lasque.tusdk.modules.view.widget.sticker.StickerText;
import org.lasque.tusdk.modules.view.widget.sticker.StickerTextData;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaTextEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import com.upyun.shortvideo.R;
import org.lasque.tusdkvideodemo.editor.MovieEditorController;
import org.lasque.tusdkvideodemo.views.TextOptionsRecycleAdapter;
import org.lasque.tusdkvideodemo.views.editor.LineView;
import org.lasque.tusdkvideodemo.views.editor.TuSdkMovieScrollPlayLineView;
import org.lasque.tusdkvideodemo.views.editor.color.ColorView;
import org.lasque.tusdkvideodemo.views.editor.playview.TuSdkMovieScrollView;
import org.lasque.tusdkvideodemo.views.editor.playview.TuSdkRangeSelectionBar;
import org.lasque.tusdkvideodemo.views.editor.playview.rangeselect.TuSdkMovieColorGroupView;
import org.lasque.tusdkvideodemo.views.editor.playview.rangeselect.TuSdkMovieColorRectView;

import java.util.ArrayList;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
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
    /** 是否点击了贴纸删除按钮或直接触摸屏幕其他区域 */
    private boolean isCancelAction;
    /** 输入法管理器 */
    private InputMethodManager mInputMethodManager;
    /** 默认持续时间 **/
    private long defaultDurationUs = 1 * 1000000;
    /** 最小持续时间 **/
    private int minSelectTimeUs = 1 * 1000000;
    /** 文字特效备忘管理 **/
    private EditorTextBackups mTextBackups;
    /** 当前的颜色块 **/
    private TuSdkMovieColorRectView mCurrentColorRectView;
    /** 当前的备份数据 **/
    private EditorTextBackups.TextBackupEntity mBackupEntity;
    /** View **/
    private View mDisplayView;

    private boolean isFirstCallSoftInput = true;

    private TuSdkSize mCurrentPreviewSize = null;

    /**
     * 显示区域改变回调
     *
     * @since V3.0.0
     */
    private TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener mOnDisplayChangeListener = new TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener() {
        @Override
        public void onPreviewSizeChanged(final TuSdkSize previewSize) {
            if (mStickerView == null) return;
            mCurrentPreviewSize = TuSdkSize.create(previewSize.width,previewSize.height);
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
        public boolean canAppendSticker(StickerView view, StickerDynamicData sticker) {
            return false;
        }

        @Override
        public void onStickerItemViewSelected(StickerData stickerData, String text, boolean needReverse) {
            if (stickerData != null && stickerData instanceof StickerTextData) {
                mBottomView.mLineView.setShowSelectBar(true);
                mStickerData = (StickerTextData) stickerData;
                mBottomView.mLineView.setLeftBarPosition(((StickerTextData) stickerData).starTimeUs / (float) getEditorPlayer().getTotalTimeUs());
                mBottomView.mLineView.setRightBarPosition(((StickerTextData) stickerData).stopTimeUs / (float) getEditorPlayer().getTotalTimeUs());
                mCurrentColorRectView = mTextBackups.findColorRect(stickerData);
                mBottomView.mBottomOptions.setOptionsEnable(true);
                mBackupEntity = mTextBackups.findTextBackupEntity(stickerData);
                if(mBackupEntity != null)
                    applyBackupEntity(mBackupEntity);
            }
        }

        @Override
        public void onStickerItemViewSelected(StickerDynamicData stickerData, String text, boolean needReverse) {

        }

        @Override
        public void onStickerItemViewReleased() {
            if (isFirstCallSoftInput){
                TuSdkViewHelper.showViewIn(getEditTextView(),true);
                isFirstCallSoftInput = false;
            } else {
                showSoftInput();
                TuSdkViewHelper.showViewIn(getEditTextView(),true);
                getEditTextView().requestFocus();
            }
        }

        @Override
        public void onCancelAllStickerSelected() {
            hideSoftInput();
            getEditTextView().setVisibility(View.GONE);
            if (mBottomView != null) mBottomView.mLineView.setShowSelectBar(false);
            if (mBottomView != null) mBottomView.mBottomOptions.setOptionsEnable(false);
            if (mBottomView != null) mBottomView.mBottomOptions.resetOptions();
            mCurrentColorRectView = null;
            mSelectEffectData = null;
        }

        @Override
        public void onStickerCountChanged(StickerData stickerData, final StickerItemViewInterface stickerItemViewInterface, int operation, int count) {
            if (mBottomView == null || mBottomView.mBottomOptions == null || stickerData.stickerType != 2)
                return;
            mBottomView.mBottomOptions.setOptionsEnable(count > 0);
            if (operation == 0) {
                mTextBackups.removeBackupEntityWithSticker((StickerTextItemView) stickerItemViewInterface);
                mBottomView.getLineView().setShowSelectBar(false);
            } else {
                mBottomView.getLineView().setShowSelectBar(true);
                float startPercent = getEditorPlayer().isReversing() ? ((StickerTextData) stickerData).starTimeUs / (float) getEditorPlayer().getTotalTimeUs() : mBottomView.mLineView.getCurrentPercent();
                float endPercent = ((StickerTextData) stickerData).stopTimeUs / (float) getEditorPlayer().getTotalTimeUs();
                TuSdkMovieColorRectView rectView = mBottomView.mLineView.recoverColorRect(R.color.lsq_scence_effect_color_EdgeMagic01, startPercent, endPercent);
                mCurrentColorRectView = rectView;
                mBackupEntity = EditorTextBackups.createBackUpEntity(stickerData, (StickerTextItemView) stickerItemViewInterface, rectView);
                mTextBackups.addBackupEntity(mBackupEntity);
            }
        }

        @Override
        public void onStickerCountChanged(StickerDynamicData stickerData, StickerItemViewInterface stickerItemViewInterface, int operation, int count) {

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
            if (mBottomView == null || isAnimationStaring) return;
            mBottomView.mLineView.seekTo(percentage);
        }
    };

    /** 最大值最小值判断 **/
    private TuSdkRangeSelectionBar.OnExceedCriticalValueListener mOnExceedValueListener = new TuSdkRangeSelectionBar.OnExceedCriticalValueListener() {
        @Override
        public void onMaxValueExceed() {
            TuSdk.messageHub().showToast(getBottomView().getContext(), R.string.lsq_max_time_effect);
        }

        @Override
        public void onMinValueExceed() {
            TuSdk.messageHub().showToast(getBottomView().getContext(), R.string.lsq_min_time_effect);
        }
    };


    public EditorTextBackups getTextBackups() {
        return mTextBackups;
    }

    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorTextComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.Text;
        mStickerView = editorController.getActivity().getTextStickerView();

        getBottomView();
        mTextBackups = new EditorTextBackups(mStickerView, mBottomView, getEditorEffector(),editorController.getImageTextRankHelper());
        mTextBackups.setLineView(mBottomView.mLineView);

        getEditorPlayer().addPreviewSizeChangeListener(mOnDisplayChangeListener);
        getEditorPlayer().addProgressListener(mPlayProgressListener);

        // 初始化软键盘管理器
        mInputMethodManager = (InputMethodManager) editorController.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void attach() {
        getEditorController().getActivity().getTextStickerView().setVisibility(View.VISIBLE);
        getEditorController().getBottomView().removeAllViews();
        getEditorController().getBottomView().addView(getBottomView());

        // 暂停
        getEditorPlayer().pausePreview();

        getEditorController().getVideoContentView().setClickable(false);
        getEditorController().getPlayBtn().setVisibility(View.GONE);

        mStickerView.setDelegate(mStickerDelegate);

        mBottomView.mBottomOptions.setOptionsEnable(false);
        mBottomView.mLineView.setShowSelectBar(false);
        mBottomView.setPlayState(1);

        mStickerView.changeOrUpdateStickerType(StickerView.StickerType.Text);
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (getEditorPlayer().isReversing()) {
            mBottomView.mLineView.seekTo(1f);
        } else {
            mBottomView.mLineView.seekTo(0f);
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        getEditorPlayer().seekOutputTimeUs(0);
        if (getEditorPlayer().isReversing()) {
            mBottomView.mLineView.seekTo(1f);
        } else {
            mBottomView.mLineView.seekTo(0f);
        }
    }

    public void backUpDatas() {
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeText);
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediEffectDataTypeStickerImage);

        ThreadHelper.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mTextBackups == null) return;
                mTextBackups.onComponentAttach();
            }
        }, 50);

    }

    @Override
    public void detach() {
        getEditorController().getActivity().getTextStickerView().setVisibility(View.GONE);
        mBottomView.setPlayState(1);
        getEditorPlayer().seekTimeUs(0);
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
        if (mBottomView == null)
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
    public TuSdkEditText getEditTextView() {
        if (mEditTextView == null) {
            mEditTextView = getEditorController().getActivity().findViewById(R.id.lsq_editTextView);
            if (mEditTextView != null) {
                mEditTextView.addTextChangedListener(mTextWatcher);
                mEditTextView.setVisibility(View.GONE);
            }
            // 对根视图设置全局监听
            setListenerToRootView();
        }
        return mEditTextView;
    }


    /** 隐藏软键盘 */
    private void hideSoftInput() {
        View view = getEditorController().getActivity().getCurrentFocus();
        if (view == null) view = new View(getEditorController().getActivity());

        if (mInputMethodManager != null)
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /** 显示软键盘 */
    private void showSoftInput() {
        View view = getEditorController().getActivity().getCurrentFocus();
        if (view == null) view = new View(getEditorController().getActivity());

        if (mInputMethodManager != null) mInputMethodManager.showSoftInput(view, 0);
        isCancelAction = false;

    }

    /** 对根视图设置全局布局监听,以间接实现监听键盘隐藏显示状态 */
    private void setListenerToRootView() {
        final View rootView = getEditorController().getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        mStickerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(TuSdkViewHelper.isFastDoubleClick(1000))return;
                // 键盘时隐藏状态,则隐藏输入框
                if (!isKeyboardShown(rootView)) {
                    getEditTextView().setVisibility(View.INVISIBLE);
                }
                // 键盘时显示状态,则显示输入框
                else if (isKeyboardShown(rootView)) {
                    getEditTextView().setVisibility(View.VISIBLE);
                }
            }
        });
    }


    /** 判断键盘显示状态 */
    private boolean isKeyboardShown(View rootView) {
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
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            if (getEditorController().getActivity().getCurrentFocus() != getEditTextView()) return;

            if (mBottomView.mBottomOptions.mStyleOptions == null) return;
            boolean isReverse = mBottomView.mBottomOptions.mArrayOptions.mNeedReverse;
            updateText(text.toString(), isReverse);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    /** 将字符串反转并返回 */
    private String reverseString(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(text);
        stringBuilder.reverse();

        return stringBuilder.toString();
    }

    /** 更新文字贴纸视图中的内容 */
    public void updateText(String text, boolean needReverse) {
        if (text == null) return;

        this.mStickerView.updateText(text, needReverse);
    }

    /** 设置备份数据的备忘 **/
    private void applyBackupEntity(EditorTextBackups.TextBackupEntity mBackupEntity) {
        if(mBottomView == null)return;
        mBottomView.applyBackupEntity(mBackupEntity);
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
        //标题按钮
        private TextView mTitleView;
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
            mDisplayView = mBottomOptions.mOptionView;

            mBackBtn = mBottomView.findViewById(R.id.lsq_text_close);
            mNextBtn = mBottomView.findViewById(R.id.lsq_text_sure);
            mPlayBtn = mBottomView.findViewById(R.id.lsq_editor_text_play);
            mLineView = mBottomView.findViewById(R.id.lsq_editor_text_play_range);
            mTitleView = mBottomView.findViewById(R.id.lsq_text_title);

            mBackBtn.setOnClickListener(mOnClickListener);
            mNextBtn.setOnClickListener(mOnClickListener);
            mPlayBtn.setOnClickListener(mOnClickListener);
            mLineView.setType(1);
            mLineView.setOnProgressChangedListener(mOnScrollingPlayPositionListener);
            mLineView.setSelectRangeChangedListener(mOnSelectTimeChangeListener);
            mLineView.setOnTouchSelectBarListener(mOnTouchSelectBarlistener);
            mLineView.setOnSelectColorRectListener(mSelectColorListener);
            mLineView.setExceedCriticalValueListener(mOnExceedValueListener);
            float minPercent = minSelectTimeUs / (float) getEditorPlayer().getTotalTimeUs();
            mLineView.setMinWidth(minPercent);
        }

        //获取底部View
        private View getBottomView() {
            return mBottomView;
        }

        public void setTitleText(@StringRes int title){
            mTitleView.setText(title);
        }

        //View的点击事件
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_text_close:
                        mTextBackups.onBackEffect();
                        mCurrentColorRectView = null;
                        mBottomOptions.mArrayOptions.mNeedReverse = false;
                        if(mEditTextView != null) mEditTextView.setText("");
                        getEditorController().onBackEvent();
                        break;
                    case R.id.lsq_text_sure:
                        handleCompleted();
                        mTextBackups.onApplyEffect();
                        mCurrentColorRectView = null;
                        mBottomOptions.mArrayOptions.mNeedReverse = false;
                        if(mEditTextView != null) mEditTextView.setText("");
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

        /**
         *
         */
        protected void handleCompleted() {
            for (StickerItemViewInterface stickerTextItemView : mStickerView.getStickerItems()) {

                if(stickerTextItemView instanceof StickerTextItemView) {
                    float renderWidth = mCurrentPreviewSize.width;
                    float renderHeight = mCurrentPreviewSize.height;

                    StickerTextItemView stickerItemView = ((StickerTextItemView) stickerTextItemView);

                    //生成图片前重置一些视图
                    stickerItemView.resetRotation();
                    stickerItemView.setStroke(TuSdkContext.getColor(R.color.lsq_color_transparent), 0);

                    //生成相应的图片
                    Bitmap textBitmap = StickerFactory.createBitmapFromView(stickerItemView.getTextView(),5);

                    stickerItemView.setStroke(TuSdkContext.getColor(R.color.lsq_color_white), 2);

                    StickerView stickerView = getEditorController().getActivity().getImageStickerView();
                    int[] parentLocaiont = new int[2];
                    stickerView.getLocationInWindow(parentLocaiont);
                    //获取计算相应的位置
                    int[] locaiont = new int[2];
                    /** 当SDKVersion >= 27 需要使用 getLocationInWindow() 方法 不然会产生极大的误差 小于27时 getLocationInWindow() 与 getLocationOnScreen()方法返回值相同*/
                    stickerItemView.getTextView().getLocationInWindow(locaiont);
                    int pointX = locaiont[0] - parentLocaiont[0];
                    int pointY = (int) (locaiont[1] - parentLocaiont[1]);
                    TuSdkSize canvasSize = TuSdkSize.create(textBitmap);
                    /** 归一化计算 */
                    float offsetX = pointX / renderWidth;
                    float offsetY = pointY / renderHeight;
                    float stickerWidth = (float) canvasSize.width / renderWidth;
                    float stickerHeight = (float) canvasSize.height / renderHeight;
                    float degree = stickerItemView.getResult(null).degree;
                    float ratio = (float) canvasSize.width / (float) canvasSize.height;


                    long starTimeUs = ((StickerTextData) stickerItemView.getSticker()).starTimeUs;
                    long stopTimeUs = ((StickerTextData) stickerItemView.getSticker()).stopTimeUs;
                    //创建特效对象并且应用
                    TuSdkMediaTextEffectData textMediaEffectData = createTextMeidEffectData(
                            textBitmap, pointX, pointY, stickerItemView.getResult(null).degree,
                            starTimeUs, stopTimeUs, canvasSize);

                    TuSdkMediaTextEffectData textEffectData = createTextMediaEffectData(textBitmap,stickerWidth,stickerHeight,offsetX,offsetY,degree,starTimeUs,stopTimeUs,ratio);
                    getEditorEffector().addMediaEffectData(textEffectData);

                    EditorTextBackups.TextBackupEntity backupEntity = mTextBackups.findTextBackupEntity(stickerItemView);
                    if (backupEntity != null)
                        backupEntity.textMediaEffectData = textEffectData;

                    stickerItemView.setVisibility(GONE);
                }else if(stickerTextItemView instanceof StickerImageItemView){
                    StickerImageItemView imageItemView = (StickerImageItemView) stickerTextItemView;
                    EditorStickerImageBackups.StickerImageBackupEntity stickerImageBackupEntity = getEditorController().getStickerComponent().getStickerImageBackups().findTextBackupEntityByMemo(imageItemView);
                    getEditorEffector().addMediaEffectData(stickerImageBackupEntity.stickerImageMediaEffectData);
                    imageItemView.setVisibility(GONE);
                }
            }

            //清空重置相关数据
            mStickerView.cancelAllStickerSelected();
            mStickerView.removeAllSticker();
        }

        /**
         * 将数据转成公用的 TuSdkMediaEffectData
         *
         * @param bitmap      文字生成的图片
         * @param offsetX     相对视频左上角X轴的位置
         * @param offsetY     相对视频左上角Y轴的位置
         * @param rotation    旋转的角度
         * @param startTimeUs 文字特效开始的时间
         * @param stopTimeUs  文字特效结束的时间
         * @param canvasSize 当前StickerView的宽高（计算比例用）
         * @return
         */
        @Deprecated
        protected TuSdkMediaTextEffectData createTextMeidEffectData(Bitmap bitmap, float offsetX, float offsetY, float rotation, long startTimeUs, long stopTimeUs, TuSdkSize canvasSize) {
            TuSdkMediaTextEffectData mediaTextEffectData = new TuSdkMediaTextEffectData(bitmap,offsetX,offsetY,rotation,TuSdkSize.create(bitmap.getWidth(), bitmap.getHeight()),canvasSize);
            mediaTextEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(startTimeUs, stopTimeUs));
            return mediaTextEffectData;
        }

        /**
         * @param bitmap 图片
         * @param stickerWidth 归一化后 图片的显示宽度
         * @param stickerHeight 归一化后 图片的显示高度
         * @param offsetX 归一化后 x轴相对左上角偏移量
         * @param offsetY 归一化后 y轴相对左上角偏移量
         * @param rotation 旋转的角度
         * @param startTimeUs 特效开始的时间
         * @param stopTimeUs 特效结束的时间
         * @return
         */
        protected TuSdkMediaTextEffectData createTextMediaEffectData(Bitmap bitmap,float stickerWidth,float stickerHeight,float offsetX,float offsetY,float rotation,long startTimeUs,long stopTimeUs,float ratio){
            TuSdkMediaTextEffectData mediaTextEffectData = new TuSdkMediaTextEffectData(bitmap, stickerWidth, stickerHeight, offsetX, offsetY, rotation,ratio);
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
                    if (itemViewInterface instanceof StickerTextItemView) {
                        StickerTextItemView itemView = (StickerTextItemView) itemViewInterface;
                        StickerTextData textData = (StickerTextData) itemView.getSticker();
                        if (textData.isContains(playPositionTime)) {
                            itemView.setVisibility(View.VISIBLE);
                        } else {
                            itemView.setVisibility(View.GONE);
                        }
                    }else if(itemViewInterface instanceof StickerImageItemView){
                        StickerImageItemView itemView = (StickerImageItemView) itemViewInterface;
                        StickerImageData imageData = (StickerImageData) itemView.getSticker();
                        if(imageData.isContains(playPositionTime)){
                            itemView.setVisibility(View.VISIBLE);
                        }else {
                            itemView.setVisibility(GONE);
                        }
                    } else if (itemViewInterface instanceof StickerDynamicItemView){
                        StickerDynamicItemView itemView = ((StickerDynamicItemView) itemViewInterface);
                        StickerDynamicData dynamicData = itemView.getCurrentStickerGroup();
                        itemView.updateStickers(System.currentTimeMillis());
                        if (dynamicData.isContains(playPositionTime)){
                            itemView.setVisibility(View.VISIBLE);
                        } else {
                            itemView.setVisibility(GONE);
                        }
                    }
                }

                if (!isTouching) return;
                if (isTouching) {
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
                if (type == 0) {
                    mStickerData.starTimeUs = (long) (leftPercent * getEditorPlayer().getTotalTimeUs());
                } else if (type == 1) {
                    mStickerData.stopTimeUs = (long) (rightPerchent * getEditorPlayer().getTotalTimeUs());
                }
                mLineView.changeColorRect(mCurrentColorRectView, leftPercent, rightPerchent);
            }
        };


        private TuSdkRangeSelectionBar.OnTouchSelectBarListener mOnTouchSelectBarlistener = new TuSdkRangeSelectionBar.OnTouchSelectBarListener() {
            @Override
            public void onTouchBar(float leftPercent, float rightPerchent, int type) {
                if (type == 0) {
                    mLineView.seekTo(leftPercent);
                } else if (type == 1) {
                    mLineView.seekTo(rightPerchent);
                }
            }
        };


        private TuSdkMovieColorGroupView.OnSelectColorRectListener mSelectColorListener = new TuSdkMovieColorGroupView.OnSelectColorRectListener() {
            @Override
            public void onSelectColorRect(final TuSdkMovieColorRectView rectView) {

                if (rectView == null) {
                    mLineView.setShowSelectBar(false);
                    mStickerView.cancelAllStickerSelected();
                }
                if (mStickerView.getStickerTextItems().size() == 0) return;
                final StickerTextData stickerData = (StickerTextData) mTextBackups.findStickerData(rectView);
//                if(rectView == mCurrentColorRectView)return;

                if (stickerData != null && stickerData instanceof StickerTextData) {
                    mLineView.setShowSelectBar(true);
                    mStickerData = stickerData;
                    final float leftPercent = stickerData.starTimeUs / (float) getEditorPlayer().getTotalTimeUs();
                    float rightPercent = stickerData.stopTimeUs / (float) getEditorPlayer().getTotalTimeUs();
                    mLineView.setLeftBarPosition(leftPercent);
                    mLineView.setRightBarPosition(rightPercent);

                    if (mCurrentColorRectView == rectView && mStickerView.getCurrentItemViewSelected() != null){
                        return;
                    }

                    mCurrentColorRectView = rectView;
                    ThreadHelper.post(new Runnable() {
                        @Override
                        public void run() {
                            mLineView.seekTo(rectView.getStartPercent() + 0.002f);
                        }
                    });
                }

                if (mTextBackups.findStickerItem(rectView) != null) {
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
            if (state == 1) {
                getEditorPlayer().pausePreview();
            }
            else {
                mStickerView.cancelAllStickerSelected();
                getEditorPlayer().startPreview();
            }

            mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(state == 0 ? R.drawable.edit_ic_pause : R.drawable.edit_ic_play));
            mBottomOptions.setOptionsEnable(state == 1 && mStickerView.stickersCount() > 0 && mStickerData != null && mStickerView.getCurrentItemViewSelected() != null);
            mBottomOptions.setAddBtnEnable(state == 1);
            mBottomOptions.backToOptionView();
        }

        /**
         * 设置备忘
         * @param backupEntity
         */
        public void applyBackupEntity(EditorTextBackups.TextBackupEntity backupEntity){
            if(mBottomOptions == null)return;
            mBottomOptions.applyBackupEntity(backupEntity);
        }
    }

    //底部文字选项View
    class EditorBottomOptions {
        private View mOptionView;
        private RecyclerView mOptionsRecycle;
        private TextOptionsRecycleAdapter mOptionsAdapter;

        //字体选项
        private EditorBottomFontOption mFontOptions;
        //颜色选项
        private EditorBottomColorOption mColorOptions;
        //不透明度
        private EditorBottomAlphaOption mAlphaOptions;
        //描边
        private EditorBottomStrokeOption mStrokeOpiotns;
        //背景
        private EditorBottomBackGroundOption mBackGroundOptions;
        //间距
        private EditorBottomSpacingOption mSpacingOptions;
        //对齐
        private EditorBottomAlignOption mAlignOptions;
        //排列
        private EditorBottomArrayOption mArrayOptions;
        //风格选项
        private EditorBottomStyleOption mStyleOptions;
        //是否可以回退为选项视图
        private boolean isCanBackToOptionView = false;

        public EditorBottomOptions() {
            mOptionView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_options, null);
            initView();
        }

        private void initView() {
            mOptionsRecycle = findViewById(R.id.lsq_editor_text_options_recycle);
            mOptionsRecycle.setLayoutManager(new LinearLayoutManager(mOptionsRecycle.getContext(), HORIZONTAL, false));
            mOptionsAdapter = new TextOptionsRecycleAdapter();
            mOptionsRecycle.setAdapter(mOptionsAdapter);
            mOptionsAdapter.setOnItemClickListener(mItemClickListener);

            mFontOptions        = new EditorBottomFontOption();
            mColorOptions       = new EditorBottomColorOption();
            mAlphaOptions       = new EditorBottomAlphaOption();
            mStrokeOpiotns      = new EditorBottomStrokeOption();
            mBackGroundOptions  = new EditorBottomBackGroundOption();
            mSpacingOptions     = new EditorBottomSpacingOption();
            mAlignOptions       = new EditorBottomAlignOption();
            mArrayOptions       = new EditorBottomArrayOption();
            mStyleOptions       = new EditorBottomStyleOption();

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
            mOptionsAdapter.setEnable(isEnable);
        }

        /**
         * 设置添加按钮是否启用
         *
         * @param isEnable
         */
        public void setAddBtnEnable(boolean isEnable) {
            mOptionsAdapter.setAddEnable(isEnable);
        }

        /** item点击事件 **/
        TextOptionsRecycleAdapter.OnItemClickListener mItemClickListener = new TextOptionsRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position,int titleId) {
                if(mBottomView != null) mBottomView.setTitleText(titleId);
                switch (position) {
                    case 0:
                        if (!mBottomView.mLineView.isShowSelectBar())
                            mBottomView.mLineView.setShowSelectBar(true);
                        addStickerItemView();
                        break;
                    case 1:
                        //字体
                        isCanBackToOptionView = true;
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mControlContent.addView(mFontOptions.mFontView);
                        mDisplayView = mFontOptions.mFontView;
                        break;
                    case 2:
                        //颜色
                        isCanBackToOptionView = true;
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mControlContent.addView(mColorOptions.mColorView);
                        mDisplayView = mColorOptions.mColorView;
                        break;
                    case 3:
                        //不透明度
                        isCanBackToOptionView = true;
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mControlContent.addView(mAlphaOptions.mAlphaView);
                        if(mBackupEntity.firstSetAlpha){
                            mAlphaOptions.mAlphaSeekBar.setProgress(1f);
                            if (mStickerView.getCurrentItemViewSelected() == null) return;
                            mStickerView.changeTextAlpha(1f);
                            mBackupEntity.alpha = 1f;
                            mBackupEntity.firstSetAlpha = false;
                        }
                        mDisplayView = mAlphaOptions.mAlphaView;
                        break;
                    case 4:
                        //描边
                        isCanBackToOptionView = true;
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mControlContent.addView(mStrokeOpiotns.mStrokeView);
                        mDisplayView = mStrokeOpiotns.mStrokeView;
                        break;
                    case 5:
                        //背景
                        isCanBackToOptionView = true;
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mControlContent.addView(mBackGroundOptions.mBackGroundView);

                        if(mBackupEntity.firstSetBackGroudAlpha){
                            mBackGroundOptions.mStrokeSizeSeekBar.setProgress(0.5f);
                            if (mStickerView.getCurrentItemViewSelected() == null) return;
                            int retColor = ColorUtils.alphaEvaluator(0.5f, 0);
                            ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).onSelectedColorChanged(1, retColor);
                            mBackupEntity.bgAlpha = mBackGroundOptions.mAlpha  =0.5f;
                            mBackupEntity.bgColor = mBackGroundOptions.mColor =  0;
                            mBackupEntity.firstSetBackGroudAlpha = false;
                        }

                        mDisplayView = mBackGroundOptions.mBackGroundView;
                        break;
                    case 6:
                        //间距
                        isCanBackToOptionView = true;
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mControlContent.addView(mSpacingOptions.mSpacingView);
                        mDisplayView = mSpacingOptions.mSpacingView;
                        break;
                    case 7:
                        //对齐
                        isCanBackToOptionView = true;
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mControlContent.addView(mAlignOptions.mAlignView);
                        mDisplayView = mAlignOptions.mAlignView;
                        break;
                    case 8:
                        //排列
                        isCanBackToOptionView = true;
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mControlContent.addView(mArrayOptions.mArrayView);
                        mDisplayView = mArrayOptions.mArrayView;
                        break;
                    case 9:
                        //样式
                        isCanBackToOptionView = true;
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mControlContent.addView(mStyleOptions.mStyleView);
                        mDisplayView = mStyleOptions.mStyleView;
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
//            mDisplayView = mOptionView;
        }

        /** 添加文本贴纸 */
        public void addStickerItemView() {
            mCurrentColorRectView = null;
            mArrayOptions.mNeedReverse = false;

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
            if (mStickerData.starTimeUs + defaultDurationUs > getEditorPlayer().getInputTotalTimeUs() && !getEditorPlayer().isReversing()) {
                mStickerData.stopTimeUs = getEditorPlayer().getOutputTotalTimeUS();
            } else {
                if (!getEditorPlayer().isReversing())
                    mStickerData.stopTimeUs = mStickerData.starTimeUs + defaultDurationUs;
                else{
                    mStickerData.stopTimeUs = mStickerData.starTimeUs;
                    mStickerData.starTimeUs = mStickerData.stopTimeUs - defaultDurationUs;
                }
            }

            if (mBottomView != null && mBottomView.mLineView != null) {
                mBottomView.mLineView.setLeftBarPosition(mStickerData.starTimeUs / (float) getEditorPlayer().getTotalTimeUs());
                mBottomView.mLineView.setRightBarPosition(mStickerData.stopTimeUs / (float) getEditorPlayer().getTotalTimeUs());
            }

            mStickerData.texts = mStickerTexts;

            getEditorPlayer().pausePreview();
            mStickerView.appendSticker(mStickerData);
        }

        public void applyBackupEntity(EditorTextBackups.TextBackupEntity backupEntity){
            mColorOptions.setBackupEntity(backupEntity);
            mAlphaOptions.setBackupEntity(backupEntity);
            mStrokeOpiotns.setBackupEntity(backupEntity);
            mBackGroundOptions.setBackupEntity(backupEntity);
            mSpacingOptions.setBackupEntity(backupEntity);
            mAlignOptions.setBackupEntity(backupEntity);
            mArrayOptions.setBackupEntity(backupEntity);
            mStyleOptions.setBackupEntity(backupEntity);
        }

        /** 重置参数 **/
        public void resetOptions(){
            mFontOptions.reset();
            mColorOptions.reset();
            mAlphaOptions.reset();
            mStrokeOpiotns.reset();
            mBackGroundOptions.reset();
            mSpacingOptions.reset();
            mAlignOptions.reset();
            mStyleOptions.reset();
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
                        mBottomView.setTitleText(R.string.lsq_edit_text_title);
                        break;
                    case R.id.lsq_editor_component_text_font_normal:
                        if (mStickerTexts == null || mStickerTexts.size() == 0 || mStickerView.getCurrentItemViewSelected() == null)
                            return;
                        ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).setTextFont(Typeface.DEFAULT);
                        mBackupEntity.mTextFont = 1;
                        break;
                    case R.id.lsq_editor_component_text_font_serif:
                        if (mStickerTexts == null || mStickerTexts.size() == 0 || mStickerView.getCurrentItemViewSelected() == null)
                            return;
                        ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).setTextFont(Typeface.SERIF);
                        mBackupEntity.mTextFont = 2;
                        break;
                }
            }
        };

        public void setBackupEntity(EditorTextBackups.TextBackupEntity entity) {
            if (mStickerTexts == null || mStickerTexts.size() == 0 || mStickerView.getCurrentItemViewSelected() == null)
                return;
            Typeface font = Typeface.DEFAULT;

            if(entity.mTextFont == 1){
                font = Typeface.DEFAULT;
            }else if(entity.mTextFont == 2){
                font = Typeface.SERIF;
            }

            ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).setTextFont(font);
        }

        /** 重置状态 **/
        public void reset(){
//            if (mStickerTexts == null || mStickerTexts.size() == 0 || mStickerView.getCurrentItemViewSelected() == null)
//                return;
//            ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).setTextFont(Typeface.DEFAULT);
        }
    }

    /** 颜色选项 **/
    class EditorBottomColorOption {
        private View mColorView;

        private ImageView mBackButton;
        //字体
        private ColorView mFontSeek;

        public EditorBottomColorOption() {
            mColorView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_color, null);
            initView();
        }

        private void initView() {
            mBackButton = findViewById(R.id.lsq_editor_component_text_font_back);
            mFontSeek = findViewById(R.id.lsq_editor_text_color_seek_font);

            mBackButton.setOnClickListener(mOnClickListener);
            mFontSeek.setOnColorChangeListener(mOnFontColorChangeListener);

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
                mDisplayView = mBottomView.mBottomOptions.mOptionView;
                mBottomView.setTitleText(R.string.lsq_edit_text_title);
                mDisplayView = mBottomView.mBottomOptions.mOptionView;
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
                mBackupEntity.color = colorId;
            }

            @Override
            public void changePosition(float percent) {

            }
        };


        public void setBackupEntity(EditorTextBackups.TextBackupEntity entity) {
            if (mStickerView.getCurrentItemViewSelected() == null || entity == null || entity.textItemView == null) return;
            entity.textItemView.onSelectedColorChanged(0,entity.color);
            mFontSeek.findColorInt(entity.color);
        }

        /** 重置状态 **/
        public void reset(){
            mFontSeek.reset();
        }
    }



    /** 不透明度 **/
    class EditorBottomAlphaOption {
        private View mAlphaView;
        private ImageView mBackButton;
        private TuSeekBar mAlphaSeekBar;

        public EditorBottomAlphaOption() {
            mAlphaView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_alpha, null);
            initView();
        }

        private void initView() {
            mBackButton = findViewById(R.id.lsq_editor_component_text_alpha_back);
            mAlphaSeekBar = findViewById(R.id.lsq_editor_text_alpha_seek_font);

            mBackButton.setOnClickListener(mOnClickListener);
            mAlphaSeekBar.setDelegate(mSeekBarDelegate);
        }

        private <T extends View> T findViewById(@IdRes int id) {
            return mAlphaView.findViewById(id);
        }

        /** 透明度改变 **/
        private TuSeekBar.TuSeekBarDelegate mSeekBarDelegate = new TuSeekBar.TuSeekBarDelegate() {
            @Override
            public void onTuSeekBarChanged(TuSeekBar seekBar, float progress) {
                if(mStickerView.getCurrentItemViewSelected() == null)return;
                mStickerView.changeTextAlpha(progress);
                if(mBackupEntity != null)mBackupEntity.alpha = progress;
            }
        };

        /** **/
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_editor_component_text_alpha_back:
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mBottomOptions.isCanBackToOptionView = true;
                        mBottomView.mControlContent.addView(mBottomView.mBottomOptions.mOptionView);
                        mBottomView.setTitleText(R.string.lsq_edit_text_title);
                        mDisplayView = mBottomView.mBottomOptions.mOptionView;
                        break;
                }
            }
        };

        /** 设置备忘数据 **/
        public void setBackupEntity(EditorTextBackups.TextBackupEntity entity){
            if(entity == null)return;
            mAlphaSeekBar.setProgress(entity == null ? 0f : entity.alpha);
            if (mStickerView.getCurrentItemViewSelected() == null) return;
            mStickerView.changeTextAlpha(entity.alpha);
        }

        /** 重置状态 **/
        public void reset(){

        }
    }


    /** 边框 **/
    class EditorBottomStrokeOption {
        private View mStrokeView;
        private ImageView mBackButton;
        private TuSeekBar mStrokeSizeSeekBar;
        private ColorView mColorSeekBar;

        private int mStrokeWidth = TuSdkContext.dip2px(2);
        private int mMaxStrokeWidth = TuSdkContext.dip2px(10);
        private int mColor = TuSdkContext.getColor(R.color.lsq_color_white);


        public EditorBottomStrokeOption() {
            mStrokeView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_stroke, null);
            initView();
        }

        private void initView() {
            mBackButton = findViewById(R.id.lsq_editor_component_text_stroke_back);
            mStrokeSizeSeekBar = findViewById(R.id.lsq_editor_text_stroke_alpha_seek);
            mColorSeekBar = findViewById(R.id.lsq_editor_text_stroke_color);
            mColorSeekBar.setCircleRadius(8);

            mBackButton.setOnClickListener(mOnClickListener);
            mStrokeSizeSeekBar.setDelegate(mSeekBarDelegate);
            mColorSeekBar.setOnColorChangeListener(mColorChangeListener);

        }

        private <T extends View> T findViewById(@IdRes int id) {
            return mStrokeView.findViewById(id);
        }

        /** 透明度改变 **/
        private TuSeekBar.TuSeekBarDelegate mSeekBarDelegate = new TuSeekBar.TuSeekBarDelegate() {
            @Override
            public void onTuSeekBarChanged(TuSeekBar seekBar, float progress) {
                if(mStickerView.getCurrentItemViewSelected() == null)return;
                mStrokeWidth = (int) (mMaxStrokeWidth * progress);
                if(mBackupEntity != null)mBackupEntity.strokeWidth = mStrokeWidth;
                mStickerView.changeTextStrokeWidth(mStrokeWidth);
            }
        };

        /** 边框颜色 **/
        private ColorView.OnColorChangeListener mColorChangeListener = new ColorView.OnColorChangeListener() {
            @Override
            public void changeColor(int colorId) {
                if(mStickerView.getCurrentItemViewSelected() == null)return;
                mColor = colorId;
                mStickerView.changeTextStrokeColor(mColor);
                if(mBackupEntity != null) mBackupEntity.strokeColor = mColor;
            }

            @Override
            public void changePosition(float percent) {

            }
        };

        /** **/
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_editor_component_text_stroke_back:
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mBottomOptions.isCanBackToOptionView = true;
                        mBottomView.mControlContent.addView(mBottomView.mBottomOptions.mOptionView);
                        mBottomView.setTitleText(R.string.lsq_edit_text_title);
                        mDisplayView = mBottomView.mBottomOptions.mOptionView;
                        break;
                }
            }
        };

        /** 设置备忘数据 **/
        public void setBackupEntity(EditorTextBackups.TextBackupEntity entity){
            mStrokeSizeSeekBar.setProgress(entity== null?0f:entity.strokeWidth);
        }

        /** 重置状态 **/
        public void reset(){
            mStrokeSizeSeekBar.setProgress(0f);
            mColorSeekBar.reset();
        }
    }

    /** 背景 **/
    class EditorBottomBackGroundOption {
        private View mBackGroundView;
        private ImageView mBackButton;
        private ColorView mColorView;
        private TuSeekBar mStrokeSizeSeekBar;
        public int mColor;
        public float mAlpha;

        public EditorBottomBackGroundOption() {
            mBackGroundView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_background, null);
            initView();
        }

        private void initView() {
            mBackButton = findViewById(R.id.lsq_editor_component_text_bg_back);
            mColorView = findViewById(R.id.lsq_editor_text_bg_color);
            mStrokeSizeSeekBar = findViewById(R.id.lsq_editor_text_bg_seek);

            mBackButton.setOnClickListener(mOnClickListener);
            mColorView.setCircleRadius(8);
            mColorView.setOnColorChangeListener(mColorChangeListener);
            mStrokeSizeSeekBar.setDelegate(mSeekBarDelegate);
            mStrokeSizeSeekBar.setProgress(0.5f);
        }

        private <T extends View> T findViewById(@IdRes int id) {
            return mBackGroundView.findViewById(id);
        }

        /** 透明度改变 **/
        private TuSeekBar.TuSeekBarDelegate mSeekBarDelegate = new TuSeekBar.TuSeekBarDelegate() {
            @Override
            public void onTuSeekBarChanged(TuSeekBar seekBar, float progress) {
                if(mStickerView.getCurrentItemViewSelected() == null)return;
                mAlpha = progress;
                mBackupEntity.bgAlpha = mAlpha;
                int retColor = ColorUtils.alphaEvaluator(progress, mColor);
                ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).onSelectedColorChanged(1, retColor);
                if(mBackupEntity != null)mBackupEntity.bgAlpha = progress;
            }
        };

        /** 边框颜色 **/
        private ColorView.OnColorChangeListener mColorChangeListener = new ColorView.OnColorChangeListener() {
            @Override
            public void changeColor(int colorId) {
                if(mStickerView.getCurrentItemViewSelected() == null)return;
                mColor = colorId;
                mBackupEntity.bgColor = mColor;
                int retColor = ColorUtils.alphaEvaluator(mAlpha, colorId);
                ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).onSelectedColorChanged(1, retColor);
            }

            @Override
            public void changePosition(float percent) {

            }
        };

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_editor_component_text_bg_back:
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mBottomOptions.isCanBackToOptionView = true;
                        mBottomView.mControlContent.addView(mBottomView.mBottomOptions.mOptionView);
                        mBottomView.setTitleText(R.string.lsq_edit_text_title);
                        mDisplayView = mBottomView.mBottomOptions.mOptionView;
                        break;

                }
            }
        };

        /** 设置备忘数据 **/
        public void setBackupEntity(EditorTextBackups.TextBackupEntity entity){
            mStrokeSizeSeekBar.setProgress(entity== null?0f:entity.bgAlpha);
            mAlpha = entity.bgAlpha;
            mColor = entity.bgColor;
            mColorView.resetToEnd();
            int retColor = ColorUtils.alphaEvaluator(mAlpha, mColor);
            ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).onSelectedColorChanged(1, retColor);
        }

        /** 重置状态 **/
        public void reset(){
            mStrokeSizeSeekBar.setProgress(0.5f);
        }
    }

    /** 间距 **/
    class EditorBottomSpacingOption {
        private View mSpacingView;
        private ImageView mBackButton;
        private TuSeekBar mRowSeekBar;
        private TuSeekBar mWordSeekBar;

        public EditorBottomSpacingOption() {
            mSpacingView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_spacing, null);
            initView();
        }

        private void initView() {
            mBackButton = findViewById(R.id.lsq_editor_component_text_spacing_back);
            mRowSeekBar = findViewById(R.id.lsq_editor_text_row_seek);
            mWordSeekBar = findViewById(R.id.lsq_editor_text_word_seek);

            mBackButton.setOnClickListener(mOnClickListener);
            mRowSeekBar.setDelegate(mRowSeekBarDelegate);
            mWordSeekBar.setDelegate(mWordSeekBarDelegate);
        }

        private <T extends View> T findViewById(@IdRes int id) {
            return mSpacingView.findViewById(id);
        }

        /** 行间距改变 **/
        private TuSeekBar.TuSeekBarDelegate mRowSeekBarDelegate = new TuSeekBar.TuSeekBarDelegate() {
            @Override
            public void onTuSeekBarChanged(TuSeekBar seekBar, float progress) {
                if(mStickerView.getCurrentItemViewSelected() == null)return;
                ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).setLineSpacing(0, 0.5f + progress);
                if(mBackupEntity != null)mBackupEntity.rowWidth = progress;
            }
        };

        /** 字间距改变 **/
        private TuSeekBar.TuSeekBarDelegate mWordSeekBarDelegate = new TuSeekBar.TuSeekBarDelegate() {
            @Override
            public void onTuSeekBarChanged(TuSeekBar seekBar, float progress) {
                if(mStickerView.getCurrentItemViewSelected() == null)return;
                ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).setLetterSpacing(0.07f * progress);
                if(mBackupEntity != null)mBackupEntity.wordWidth = progress;
            }
        };


        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_editor_component_text_spacing_back:
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mBottomOptions.isCanBackToOptionView = true;
                        mBottomView.mControlContent.addView(mBottomView.mBottomOptions.mOptionView);
                        mBottomView.setTitleText(R.string.lsq_edit_text_title);
                        mDisplayView = mBottomView.mBottomOptions.mOptionView;
                        break;

                }
            }
        };

        /** 设置备忘数据 **/
        public void setBackupEntity(EditorTextBackups.TextBackupEntity entity){
            mRowSeekBar.setProgress(entity== null?0f:entity.rowWidth);
            mWordSeekBar.setProgress(entity== null?0f:entity.wordWidth);
        }

        /** 重置状态 **/
        public void reset(){
            mRowSeekBar.setProgress(0f);
            mWordSeekBar.setProgress(0f);
        }
    }

    /** 文字对齐 **/
    class EditorBottomAlignOption {
        private View mAlignView;
        private ImageView mBackButton;

        private LinearLayout mLeftAlignButton;
        private LinearLayout mCenterAlignButton;
        private LinearLayout mRightAlignButton;

        public EditorBottomAlignOption() {
            mAlignView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_align, null);
            initView();
        }

        private void initView() {
            mBackButton = findViewById(R.id.lsq_editor_component_text_align_back);
            mLeftAlignButton = findViewById(R.id.lsq_editor_text_leftalignment);
            mCenterAlignButton = findViewById(R.id.lsq_editor_text_centeralignment);
            mRightAlignButton = findViewById(R.id.lsq_editor_text_rightalignment);

            mBackButton.setOnClickListener(mOnClickListener);
            mLeftAlignButton.setOnClickListener(mOnClickListener);
            mCenterAlignButton.setOnClickListener(mOnClickListener);
            mRightAlignButton.setOnClickListener(mOnClickListener);
        }

        private <T extends View> T findViewById(@IdRes int id) {
            return mAlignView.findViewById(id);
        }

        /** 点击回调 **/
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_editor_component_text_align_back:
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mBottomOptions.isCanBackToOptionView = true;
                        mBottomView.mControlContent.addView(mBottomView.mBottomOptions.mOptionView);
                        mBottomView.setTitleText(R.string.lsq_edit_text_title);
                        mDisplayView = mBottomView.mBottomOptions.mOptionView;
                        break;
                    case R.id.lsq_editor_text_leftalignment:
                        if(mStickerView.getCurrentItemViewSelected() == null)return;
                        mStickerView.changeTextAlignment(Gravity.LEFT);
                        break;
                    case R.id.lsq_editor_text_centeralignment:
                        if(mStickerView.getCurrentItemViewSelected() == null)return;
                        mStickerView.changeTextAlignment(Gravity.CENTER);
                        break;
                    case R.id.lsq_editor_text_rightalignment:
                        if(mStickerView.getCurrentItemViewSelected() == null)return;
                        mStickerView.changeTextAlignment(Gravity.RIGHT);
                        break;
                }
            }
        };

        public void setBackupEntity(EditorTextBackups.TextBackupEntity entity) {

        }

        /** 重置状态 **/
        public void reset(){

        }
    }

    /** 文字排列 **/
    class EditorBottomArrayOption {
        private View mArrayView;
        private ImageView mBackButton;
        private LinearLayout mLeftToRightButton;
        private LinearLayout mRightToLeftButton;
        private boolean mNeedReverse = true;
        private TuSdkTextButton mL2R;
        private TuSdkTextButton mR2L;

        public EditorBottomArrayOption() {
            mArrayView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_array, null);
            initView();
        }

        private void initView() {
            mBackButton = findViewById(R.id.lsq_editor_component_text_array_back);
            mLeftToRightButton = findViewById(R.id.lsq_editor_text_lefttoright);
            mRightToLeftButton = findViewById(R.id.lsq_editor_text_righttoleft);
            mL2R = findViewById(R.id.lsq_btn_left_to_right);
            mL2R.setDefaultColor(mL2R.getResources().getColor(R.color.lsq_color_white));
            mL2R.setSelectedColor(mL2R.getResources().getColor(R.color.lsq_filter_title_color));


            mR2L = findViewById(R.id.lsq_btn_right_to_left);
            mR2L.setDefaultColor(mR2L.getResources().getColor(R.color.lsq_color_white));
            mR2L.setSelectedColor(mR2L.getResources().getColor(R.color.lsq_filter_title_color));
            mL2R.setSelected(true);

            mBackButton.setOnClickListener(mOnClickListener);
            mLeftToRightButton.setOnClickListener(mOnClickListener);
            mRightToLeftButton.setOnClickListener(mOnClickListener);

        }

        private <T extends View> T findViewById(@IdRes int id) {
            return mArrayView.findViewById(id);
        }

        /** 点击回调 **/
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lsq_editor_component_text_array_back:
                        mBottomView.mControlContent.removeAllViews();
                        mBottomView.mBottomOptions.isCanBackToOptionView = true;
                        mBottomView.mControlContent.addView(mBottomView.mBottomOptions.mOptionView);
                        mBottomView.setTitleText(R.string.lsq_edit_text_title);
                        mDisplayView = mBottomView.mBottomOptions.mOptionView;
                        break;
                    case R.id.lsq_editor_text_lefttoright:
                        if(mStickerView.getCurrentItemViewSelected() == null)return;
                        if (mNeedReverse) return;
                        if (mBackupEntity != null) mBackupEntity.needReverse = true;
                        mNeedReverse = true;
                        mL2R.setSelected(mNeedReverse);
                        mR2L.setSelected(!mNeedReverse);
                        mStickerView.toggleTextReverse();
                        break;
                    case R.id.lsq_editor_text_righttoleft:
                        if(mStickerView.getCurrentItemViewSelected() == null)return;
                        if (!mNeedReverse) return;
                        if (mBackupEntity != null) mBackupEntity.needReverse = false;
                        mNeedReverse = false;
                        mL2R.setSelected(mNeedReverse);
                        mR2L.setSelected(!mNeedReverse);
                        mStickerView.toggleTextReverse();
                        break;
                }
            }
        };

        public void setBackupEntity(EditorTextBackups.TextBackupEntity entity) {
            mL2R.setSelected(entity.needReverse);
            mR2L.setSelected(!entity.needReverse);
            mNeedReverse = entity.needReverse;
        }

        /** 重置状态 **/
        public void reset(){
            mNeedReverse = true;
        }
    }

    /** 风格选项 */
    class EditorBottomStyleOption {
        private static final int BOLD = 0;
        private static final int UNDERLINE = 1;
        private static final int ITALIC = 2;

        private View mStyleView;
        private ImageView mBackButton;

        private int[] selRes = {R.drawable.t_ic_blod_sel,R.drawable.t_ic_underline_sel,R.drawable.t_ic_ltalic_sel};
        private int[] norRes = {R.drawable.t_ic_blod_nor,R.drawable.t_ic_underline_nor,R.drawable.t_ic_ltalic_nor};

        private LinearLayout mNormalButton;
        private LinearLayout mUnderlineButton;
        private LinearLayout mBoldButton;
        private LinearLayout mItalicsButton;

        private boolean isUnderline = false;
        private boolean isBold = false;
        private boolean isItalics = false;

        public EditorBottomStyleOption() {
            mStyleView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_style, null);
            initView();
        }

        private void initView() {
            mBackButton = findViewById(R.id.lsq_editor_component_text_font_back);

            mUnderlineButton = findViewById(R.id.lsq_editor_text_underline);
            mNormalButton = findViewById(R.id.lsq_editor_text_normal);
            mBoldButton = findViewById(R.id.lsq_editor_text_bold);
            mItalicsButton = findViewById(R.id.lsq_editor_text_italics);

            mBackButton.setOnClickListener(mOnClickListener);
            mUnderlineButton.setOnClickListener(mOnClickListener);
            mNormalButton.setOnClickListener(mOnClickListener);
            mBoldButton.setOnClickListener(mOnClickListener);
            mItalicsButton.setOnClickListener(mOnClickListener);
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
                        mBottomView.setTitleText(R.string.lsq_edit_text_title);
                        mDisplayView = mBottomView.mBottomOptions.mOptionView;
                        break;
                    case R.id.lsq_editor_text_normal:
                        if(mStickerView.getCurrentItemViewSelected() == null)return;
                        if(isUnderline) {
                            mStickerView.toggleTextUnderlineStyle();
                            isUnderline = false;
                        }
                        isItalics = false;
                        isBold = false;
                        ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).getTextView().setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                        setIsCheck(isUnderline,UNDERLINE,mUnderlineButton);
                        setIsCheck(isItalics,ITALIC,mItalicsButton);
                        setIsCheck(isBold,BOLD,mBoldButton);
                        if(mBackupEntity != null){
                            mBackupEntity.isBold = false;
                            mBackupEntity.isUnderline = false;
                            mBackupEntity.isItalics = false;
                        }
                        break;
                    case R.id.lsq_editor_text_bold:
                        if(mStickerView.getCurrentItemViewSelected() == null)return;
                        isBold = !isBold;
                        if(mBackupEntity != null)mBackupEntity.isBold = isBold;
                        int style = isBold?Typeface.BOLD:Typeface.NORMAL;
                        if(isBold && isItalics) style = Typeface.BOLD_ITALIC;
                        else if(!isBold && isItalics) style = Typeface.ITALIC;
                        Typeface typeface = Typeface.create(((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).getTextView().getTypeface(),style);
                        ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).getTextView().setTypeface(typeface);
                        setIsCheck(isBold,BOLD,mBoldButton);
                        break;
                    case R.id.lsq_editor_text_underline:
                        if(mStickerView.getCurrentItemViewSelected() == null)return;
                        isUnderline = !isUnderline;
                        if(mBackupEntity != null)mBackupEntity.isUnderline = isUnderline;
                        ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).setUnderline(isUnderline);
                        setIsCheck(isUnderline,UNDERLINE,mUnderlineButton);
                        break;
                    case R.id.lsq_editor_text_italics:
                        if(mStickerView.getCurrentItemViewSelected() == null)return;
                        isItalics = !isItalics;
                        if(mBackupEntity != null)mBackupEntity.isItalics = isItalics;
                        int italicStyle = isItalics?Typeface.ITALIC:Typeface.NORMAL;
                        if(isItalics && isBold) italicStyle = Typeface.BOLD_ITALIC;
                        else if (!isItalics && isBold) italicStyle = Typeface.BOLD;
                        Typeface italicTypeface = Typeface.create(((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).getTextView().getTypeface(),italicStyle);
                        ((StickerTextItemView) mStickerView.getCurrentItemViewSelected()).getTextView().setTypeface(italicTypeface);
                        setIsCheck(isItalics,ITALIC,mItalicsButton);
                        break;
                    default:
                        break;

                }
            }
        };

        /**
         * 是否选中状态
         * @param isCheck
         * @param viewGroup
         */
        private void setIsCheck(boolean isCheck,int position,LinearLayout viewGroup){
            int resId = isCheck?selRes[position]:norRes[position];
            ((ImageView)viewGroup.getChildAt(0)).setImageResource(resId);
            int color = TuSdkContext.getColor(isCheck?R.color.lsq_capture_selected:R.color.lsq_capture_unselected);
            ((TextView)viewGroup.getChildAt(1)).setTextColor(color);
        }

        /** 设置备忘数据 **/
        public void setBackupEntity(EditorTextBackups.TextBackupEntity entity){
            if(entity == null){
                isBold = false;
                isUnderline = false;
                isItalics = false;

                setIsCheck(isUnderline,UNDERLINE,mUnderlineButton);
                setIsCheck(isItalics,ITALIC,mItalicsButton);
                setIsCheck(isBold,BOLD,mBoldButton);
                return;
            }
            isBold = entity.isBold;
            isItalics = entity.isItalics;
            isUnderline = entity.isUnderline;
            setIsCheck(isBold,BOLD,mBoldButton);
            setIsCheck(isUnderline, UNDERLINE, mUnderlineButton);
            setIsCheck(isItalics,ITALIC,mItalicsButton);
        }

        /** 重置状态 **/
        public void reset(){
            isItalics = false;
            isBold = false;
            isUnderline = false;
            setIsCheck(isItalics,ITALIC,mItalicsButton);
            setIsCheck(isUnderline, UNDERLINE, mUnderlineButton);
            setIsCheck(isBold,BOLD,mBoldButton);
        }
    }

}
