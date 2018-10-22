/**
 * TuSDKVideoDemo
 * MovieRecordView.java
 *
 * @author Bonan
 * @Date: 2017-5-8 上午10:42:48
 * @Copyright: (c) 2017 tusdk.com. All rights reserved.
 */
package com.upyun.shortvideo.views.record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.upyun.shortvideo.R;
import com.upyun.shortvideo.album.MovieAlbumActivity;
import com.upyun.shortvideo.suite.MoviePreviewAndCutActivity;
import com.upyun.shortvideo.utils.Constants;
import com.upyun.shortvideo.views.CompoundDrawableTextView;
import com.upyun.shortvideo.views.ConfigViewSeekBar;
import com.upyun.shortvideo.views.FilterCellView;
import com.upyun.shortvideo.views.FilterConfigSeekbar;
import com.upyun.shortvideo.views.FilterConfigView;
import com.upyun.shortvideo.views.FilterConfigView.FilterConfigViewSeekBarDelegate;
import com.upyun.shortvideo.views.FilterListView;
import com.upyun.shortvideo.views.StickerCellView;
import com.upyun.shortvideo.views.StickerListView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.secret.StatisticsManger;
import org.lasque.tusdk.core.seles.SelesParameters;
import org.lasque.tusdk.core.seles.SelesParameters.FilterArg;
import org.lasque.tusdk.core.seles.sources.SelesVideoCameraInterface;
import org.lasque.tusdk.core.seles.tusdk.FilterWrap;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.ContextUtils;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.hardware.CameraConfigs.CameraFlash;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordError;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordMode;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera.RecordState;
import org.lasque.tusdk.core.utils.hardware.TuSDKVideoCamera.TuSDKVideoCameraDelegate;
import org.lasque.tusdk.core.utils.hardware.TuSdkStillCameraAdapter.CameraState;
import org.lasque.tusdk.core.utils.image.RatioType;
import org.lasque.tusdk.core.utils.json.JsonHelper;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView;
import org.lasque.tusdk.core.view.recyclerview.TuSdkTableView.TuSdkTableViewItemClickDelegate;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;
import org.lasque.tusdk.impl.view.widget.TuSeekBar;
import org.lasque.tusdk.modules.components.ComponentActType;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 断点续拍 + 正常模式录制相机界面视图
 */
public class MovieRecordView extends RelativeLayout {
    /**
     * 关闭按钮
     */
    protected TuSdkTextButton mCloseView;

    /**
     * 比例调节按钮
     */
    protected ImageView mRatioButton;

    /**
     * 闪光灯按钮
     */
    protected ImageView mFlashButton;

    /**
     * 切换摄像头
     */
    protected ImageView mToggleButton;

    /**
     * 开始录制按钮
     */
    protected ImageButton mRecordButton;

    /**
     * 动态贴纸按钮
     */
    protected TuSdkTextButton mStickerButton;

    /**
     * 智能美化按钮
     */
    protected TuSdkTextButton mFilterButton;

    /**
     * 相机底部按钮栏
     */
    private RelativeLayout mBottomBar;

    // 闪光灯状态
    private boolean mFlashEnabled = false;

    // 确认完成按钮
    protected TuSdkTextButton mConfirmButton;

    // 回退按钮
    protected TuSdkTextButton mRollBackButton;

    private int lastProgress;

    // 录制的视频之间的断点
    private RelativeLayout interuptLayout;

    private ArrayList<Integer> progressList = new ArrayList<Integer>();

    /**
     * 上方的触摸视图
     */
    private FrameLayout mTouchView;

    /**
     * 进度条
     */
    private ProgressBar mProgressBar;

    /**
     * 参数调节视图
     */
    protected FilterConfigView mConfigView;

    /**
     * 滤镜栏视图
     */
    protected FilterListView mFilterListView;

    /**
     * 贴纸栏视图
     */
    protected StickerListView mStickerListView;

    /**
     * 滤镜底部栏
     */
    private RelativeLayout mFilterBottomView;

    /**
     * 贴纸底部栏
     */
    private RelativeLayout mStickerBottomView;

    // 用于记录焦点位置
    private int mFocusPostion = 1;

    // 记录当前滤镜
    private FilterWrap mSelesOutInput;

    // 滤镜Tab
    private TuSdkTextButton mFilterTab;
    // 美颜布局
    private RelativeLayout mFilterLayout;
    // 美颜Tab
    private TuSdkTextButton mBeautyTab;
    // 美颜布局
    private LinearLayout mBeautyLayout;
    // 磨皮调节栏
    private ConfigViewSeekBar mSmoothingBarLayout;
    // 大眼调节栏
    private ConfigViewSeekBar mEyeSizeBarLayout;
    // 瘦脸调节栏
    private ConfigViewSeekBar mChinSizeBarLayout;
    // 用于记录当前调节栏效果系数
    private float mMixiedProgress = -1.0f;
    // 用于记录当前调节栏磨皮系数
    private float mSmoothingProgress = -1.0f;
    // 用于记录当前调节栏大眼系数
    private float mEyeSizeProgress = -1.0f;
    // 用于记录当前调节栏瘦脸系数
    private float mChinSizeProgress = -1.0f;
    // 用于记录当前白皙的系数
    private float mWhiteningProgress = -1.0f;

    private Context mContext;

    protected TuSDKRecordVideoCamera mCamera;

    // 录制视频动作委托
    private TuSDKMovieRecordDelegate mDelegate;

    // 跳转视频编辑按钮
    private CompoundDrawableTextView mMovieImportButton;

    // 顶部栏
    private RelativeLayout mTopBarLayout;
    private RelativeLayout mProcessContainerLayout;

    // 记录页面状态
    protected boolean mActived = false;
    // 记录是否是首次进入录制页面
    private boolean mIsFirstEntry = true;

    // 记录是否使用方形贴纸
    private boolean isSquareSticker = true;

    /**
     * 当前屏幕比例
     */
    private int mScreenRatioType;
    /**
     * 视频视图显示比例类型 (默认:RatioType.ratio_all, 如果设置CameraViewRatio > 0,
     * 将忽略RatioType)
     */
    private int mRatioType = RatioType.ratio_orgin | RatioType.ratio_1_1 | RatioType.ratio_3_4;

    /**
     * 当前比例类型
     */
    private int mCurrentRatioType;

    /**
     * 录制视频动作委托
     */
    public interface TuSDKMovieRecordDelegate {
        /**
         * 开始录制视频
         */
        void startRecording();

        /**
         * 是否正在录制
         *
         * @return
         */
        boolean isRecording();

        /**
         * 暂停录制视频
         */
        void pauseRecording();

        /**
         * 停止录制视频
         */
        void stopRecording();

        /**
         * 关闭录制界面
         */
        void finishRecordActivity();
    }

    public MovieRecordView(Context context) {
        super(context);

    }

    public MovieRecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setDelegate(TuSDKMovieRecordDelegate delegate) {
        mDelegate = delegate;
    }

    public TuSDKMovieRecordDelegate getDelegate() {
        return mDelegate;
    }

    protected int getLayoutId() {
        return R.layout.movie_record_view;
    }

    protected void init(Context context) {
        mIsFirstEntry = true;

        LayoutInflater.from(context).inflate(getLayoutId(), this,
                true);
        mMovieImportButton = (CompoundDrawableTextView) findViewById(R.id.lsq_movieEditorButton);
        if (mMovieImportButton != null)
            mMovieImportButton.setOnClickListener(mButtonClickListener);

        mCloseView = (TuSdkTextButton) findViewById(R.id.lsq_closeButton);
        mCloseView.setOnClickListener(mButtonClickListener);

        mFlashButton = (ImageView) findViewById(R.id.lsq_flashButton);
        mFlashButton.setOnClickListener(mButtonClickListener);

        mRatioButton = (ImageView) findViewById(R.id.lsq_ratioButton);
        mRatioButton.setOnClickListener(mButtonClickListener);
        mRatioButton.setVisibility(GONE);

        mToggleButton = (ImageView) findViewById(R.id.lsq_toggleButton);
        mToggleButton.setOnClickListener(mButtonClickListener);

        mRecordButton = (ImageButton) findViewById(R.id.lsq_recordButton);
        LayoutParams btnParams = (LayoutParams) mRecordButton.getLayoutParams();
        btnParams.width = Math.min(btnParams.width, btnParams.height);
        btnParams.height = Math.min(btnParams.width, btnParams.height);
        mRecordButton.setLayoutParams(btnParams);


        mFilterButton = (TuSdkTextButton) findViewById(R.id.lsq_tab_filter_btn);
        mFilterButton.setOnClickListener(mButtonClickListener);

        mStickerButton = (TuSdkTextButton) findViewById(R.id.lsq_stickerWrap);
        mStickerButton.setOnClickListener(mButtonClickListener);

        //确认按钮
        mConfirmButton = (TuSdkTextButton) findViewById(R.id.lsq_confirmWrap);
        mConfirmButton.setOnClickListener(mButtonClickListener);
        mConfirmButton.setClickable(false);

        //回退按钮
        mRollBackButton = (TuSdkTextButton) findViewById(R.id.lsq_backWrap);
        mRollBackButton.setOnClickListener(mButtonClickListener);
        mRollBackButton.setClickable(false);

        mBottomBar = (RelativeLayout) findViewById(R.id.lsq_bottomBar);
        android.view.ViewGroup.LayoutParams params = mBottomBar.getLayoutParams();

        int remainHeight = TuSdkContext.getScreenSize().height - TuSdkContext.getScreenSize().width - ContextUtils.dip2px(context, 90);
        params.height = remainHeight;
        mBottomBar.setLayoutParams(params);

        mTouchView = (FrameLayout) findViewById(R.id.lsq_touch_view);
        mTouchView.setOnClickListener(mButtonClickListener);
        mTouchView.setVisibility(View.INVISIBLE);

        mProcessContainerLayout = (RelativeLayout) findViewById(R.id.lsq_process_container);
        mProgressBar = (ProgressBar) findViewById(R.id.lsq_record_progressbar);
        mProgressBar.setProgress(0);

        Button minTimeButton = (Button) findViewById(R.id.lsq_minTimeBtn);
        LayoutParams minTimeLayoutParams = (LayoutParams) minTimeButton.getLayoutParams();
        minTimeLayoutParams.leftMargin = (int) (((float) Constants.MIN_RECORDING_TIME * TuSdkContext.getScreenSize().width) / Constants.MAX_RECORDING_TIME)
                - TuSdkContext.dip2px(minTimeButton.getWidth());

        //一进入录制界面就显示最小时长标记
        interuptLayout = (RelativeLayout) findViewById(R.id.interuptLayout);

        mBeautyTab = (TuSdkTextButton) findViewById(R.id.lsq_beauty_btn);
        mBeautyTab.setOnClickListener(mButtonClickListener);
        mBeautyLayout = (LinearLayout) findViewById(R.id.lsq_beauty_content);

        mFilterTab = (TuSdkTextButton) findViewById(R.id.lsq_filter_btn);
        mFilterTab.setOnClickListener(mButtonClickListener);
        mFilterLayout = (RelativeLayout) findViewById(R.id.lsq_filter_content);

        mSmoothingBarLayout = (ConfigViewSeekBar) mBeautyLayout.findViewById(R.id.lsq_dermabrasion_bar);
        mSmoothingBarLayout.getTitleView().setText(R.string.lsq_dermabrasion);
        mSmoothingBarLayout.getSeekbar().setDelegate(mTuSeekBarDelegate);

        mEyeSizeBarLayout = (ConfigViewSeekBar) mBeautyLayout.findViewById(R.id.lsq_big_eyes_bar);
        mEyeSizeBarLayout.getTitleView().setText(R.string.lsq_big_eyes);
        mEyeSizeBarLayout.getSeekbar().setDelegate(mTuSeekBarDelegate);

        mChinSizeBarLayout = (ConfigViewSeekBar) mBeautyLayout.findViewById(R.id.lsq_thin_face_bar);
        mChinSizeBarLayout.getTitleView().setText(R.string.lsq_thin_face);
        mChinSizeBarLayout.getSeekbar().setDelegate(mTuSeekBarDelegate);

        mFilterBottomView = (RelativeLayout) findViewById(R.id.lsq_filter_group_bottom_view);
        mFilterBottomView.setVisibility(View.INVISIBLE);

        mStickerBottomView = (RelativeLayout) findViewById(R.id.lsq_sticker_group_bottom_view);
        mStickerBottomView.setVisibility(View.INVISIBLE);

        mTopBarLayout = (RelativeLayout) findViewById(R.id.lsq_topBar);

        android.view.ViewGroup.LayoutParams param = mStickerBottomView.getLayoutParams();
        param.height = remainHeight;
        mStickerBottomView.setLayoutParams(param);


        initFilterListView();
        initStickerListView();

        updateFlashButtonStatus();
        updateShowStatus(false);

        updateViewStatus(false);
        updateTopBarStatus(false);

        //为进度添加初始值
        progressList.add(0);

        initDefaultRatio(mRatioButton);
    }

    public void setActived(boolean mActived) {
        this.mActived = mActived;
    }

    public RelativeLayout getFilterBottomView() {
        return mFilterBottomView;
    }

    public CompoundDrawableTextView getMovieImportButton() {
        return mMovieImportButton;
    }

    public RelativeLayout getStickerBottomView() {
        return mStickerBottomView;
    }

    public RelativeLayout getTopBarLayout() {
        return mTopBarLayout;
    }

    public RelativeLayout getBottomBarLayout() {
        return mBottomBar;
    }

    /**
     * 计算  顶部工具栏 + 进度栏 高度
     *
     * @return 顶部总高度
     */
    public int calculateHeaderLayoutHeight() {
        return mTopBarLayout.getHeight() + mProcessContainerLayout.getHeight();
    }

    /**
     * 录制按钮触摸事件处理
     */
    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (getDelegate() == null || mCamera.getRecordMode() != RecordMode.Keep) return false;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    onPressRecordButton();

                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:

                    onReleaseRecordButton();

                    break;
            }
            return false;
        }
    };

    /**
     * 按钮点击事件处理
     */
    private OnClickListener mButtonClickListener = new OnClickListener() {
        public void onClick(View v) {
            dispatchClickEvent(v);
        }
    };

    /**
     * 按下录制按钮
     */
    protected void onPressRecordButton() {
        if (!getDelegate().isRecording()) {
            // 启动录制隐藏比例调节按钮
            if (mRatioButton != null)
                mRatioButton.setVisibility(GONE);
            getDelegate().startRecording();
        }
    }

    /**
     * 释放录制按钮
     */
    protected void onReleaseRecordButton() {

        updateTopBarStatus(false);
        if (getDelegate() != null && getDelegate().isRecording()) {
            getDelegate().pauseRecording();
        }
    }


    private void updateSmartBeautyTab(TuSdkTextButton button, boolean clickable) {
        int imgId = 0, colorId = 0;

        switch (button.getId()) {
            case R.id.lsq_filter_btn:
                imgId = clickable ? R.drawable.lsq_style_default_btn_filter_selected
                        : R.drawable.lsq_style_default_btn_filter_unselected;
                colorId = clickable ? R.color.lsq_filter_title_color : R.color.lsq_filter_title_default_color;
                break;
            case R.id.lsq_beauty_btn:
                imgId = clickable ? R.drawable.lsq_style_default_btn_beauty_selected
                        : R.drawable.lsq_style_default_btn_beauty_unselected;
                colorId = clickable ? R.color.lsq_filter_title_color : R.color.lsq_filter_title_default_color;
                break;
        }

        button.setCompoundDrawables(null, TuSdkContext.getDrawable(imgId), null, null);
        button.setTextColor(TuSdkContext.getColor(colorId));
    }

    private void setEnableAllSeekBar(boolean enable) {
        setEnableSeekBar(mSmoothingBarLayout, enable, 0, R.drawable.tusdk_view_widget_seekbar_none_drag);
        setEnableSeekBar(mEyeSizeBarLayout, enable, 0, R.drawable.tusdk_view_widget_seekbar_none_drag);
        setEnableSeekBar(mChinSizeBarLayout, enable, 0, R.drawable.tusdk_view_widget_seekbar_none_drag);
    }

    /**
     * 设置调节栏是否有效
     */
    private void setEnableSeekBar(ConfigViewSeekBar viewSeekBar, boolean enable, float progress, int id) {
        if (viewSeekBar == null) return;

        viewSeekBar.setProgress(progress);
        viewSeekBar.getSeekbar().setEnabled(enable);
        viewSeekBar.getSeekbar().getDragView().setBackgroundResource(id);
    }

    /**
     * 显示美颜调节栏
     */
    private void showBeautySeekBar() {
        if (mIsFirstEntry) {
            changeVideoFilterCode(Arrays.asList(Constants.VIDEOFILTERS).get(mFocusPostion));
        }

        if (mBeautyLayout == null || mFilterLayout == null)
            return;

        mBeautyLayout.setVisibility(View.VISIBLE);
        mFilterLayout.setVisibility(View.GONE);
        updateSmartBeautyTab(mBeautyTab, true);
        updateSmartBeautyTab(mFilterTab, false);

        if (mSelesOutInput == null) {
            setEnableAllSeekBar(false);
            return;
        }

        // 滤镜参数
        SelesParameters params = mSelesOutInput.getFilterParameter();
        if (params == null) {
            setEnableAllSeekBar(false);
            return;
        }

        List<FilterArg> list = params.getArgs();
        if (list == null || list.size() == 0) {
            setEnableAllSeekBar(false);
            return;
        }

        for (FilterArg arg : list) {
            if (arg.equalsKey("smoothing")) {
                setEnableSeekBar(mSmoothingBarLayout, true, arg.getPrecentValue(),
                        R.drawable.tusdk_view_widget_seekbar_drag);
            } else if (arg.equalsKey("eyeSize")) {
                setEnableSeekBar(mEyeSizeBarLayout, true, arg.getPrecentValue(),
                        R.drawable.tusdk_view_widget_seekbar_drag);
            } else if (arg.equalsKey("chinSize")) {
                setEnableSeekBar(mChinSizeBarLayout, true, arg.getPrecentValue(),
                        R.drawable.tusdk_view_widget_seekbar_drag);
            }
        }
    }

    /**
     * 显示滤镜列表
     */
    private void showFilterLayout() {
        if (mBeautyLayout == null || mFilterLayout == null)
            return;

        mFilterLayout.setVisibility(View.VISIBLE);
        mBeautyLayout.setVisibility(View.GONE);
        updateSmartBeautyTab(mBeautyTab, false);
        updateSmartBeautyTab(mFilterTab, true);

        if (mFocusPostion > 0 && getFilterConfigView() != null && mSelesOutInput != null) {
            getFilterConfigView().post(new Runnable() {

                @Override
                public void run() {
                    getFilterConfigView().setSelesFilter(mSelesOutInput.getFilter());
                    getFilterConfigView().setVisibility(View.VISIBLE);
                }
            });

            getFilterConfigView().setSeekBarDelegate(mConfigSeekBarDelegate);
            getFilterConfigView().invalidate();
        }
    }

    /****************************** CameraRatio ***********************************/

    /**
     * 初始化默认相机显示比例
     */
    private void initDefaultRatio(ImageView btn) {
        if (btn == null) return;

        // 设置了固定比例，或者仅有一种比例可选时，不显示比例开关
        if (RatioType.ratioCount(this.getRatioType()) == 1) btn.setVisibility(GONE);

        this.setCurrentRatioType(RatioType.firstRatioType(this.getRatioType()));
    }

    public void isShowRatioButton(boolean isShow) {
        mRatioButton.setVisibility(isShow ? VISIBLE : GONE);
    }

    /**
     * 视频视图显示比例类型 (默认:RatioType.ratio_all, 如果设置CameraViewRatio > 0,
     * 将忽略RatioType)
     */
    public final int getRatioType() {
        return mRatioType;
    }

    /**
     * 视频视图显示比例类型 (默认:RatioType.ratio_all, 如果设置CameraViewRatio > 0,
     * 将忽略RatioType)
     */
    public final void setRatioType(int mRatioType) {
        this.mRatioType = mRatioType;

        if (mScreenRatioType == 0) {
            mScreenRatioType = RatioType.radioType(TuSdkContext.getScreenSize().minMaxRatio());
        }

        // 将和屏幕比例相同的比例替换为全屏选项
        if (mScreenRatioType != 1 && (mScreenRatioType == (mScreenRatioType & mRatioType))) {
            this.mRatioType = ((mRatioType | RatioType.ratio_orgin) ^ mScreenRatioType);
        }
    }

    /**
     * 获取当前显示比例
     */
    public float getCurrentRatio() {
        // 设置了固定比例
        if (mCurrentRatioType > 0) return RatioType.ratio(mCurrentRatioType);
        return 0f;
    }

    /**
     * 获取显示比例类型
     *
     * @return
     */
    public int getCurrentRatioType() {
        return mCurrentRatioType;
    }

    /**
     * 设置当前比例类型
     */
    protected void setCurrentRatioType(int ratioType) {
        long actType;
        int ratioIcon;
        switch (ratioType) {
            case RatioType.ratio_3_4:
                ratioIcon = TuSdkContext.getDrawableResId("lsq_style_default_camera_ratio_3_4");
                actType = ComponentActType.camera_action_ratio_3_4;
                break;
            case RatioType.ratio_1_1:
                ratioIcon = TuSdkContext.getDrawableResId("lsq_style_default_camera_ratio_1_1");
                actType = ComponentActType.camera_action_ratio_1_1;
                break;
            default:
                ratioIcon = TuSdkContext.getDrawableResId("lsq_style_default_camera_ratio_orgin");
                actType = ComponentActType.camera_action_ratio_orgin;
                break;

        }
        mCurrentRatioType = ratioType;

        if (mRatioButton != null)
            mRatioButton.setImageResource(ratioIcon);

        // sdk统计
        StatisticsManger.appendComponent(actType);

    }

    /**
     * 获取当前 Ratio 预览画面顶部偏移百分比（默认：-1 居中显示 取值范围：0-1）
     *
     * @param ratioType
     * @return
     */
    protected float getPreviewOffsetTopPercent(int ratioType) {
        if (ratioType == RatioType.ratio_1_1) return 0.1f;

        // 置顶
        return 0.f;
    }

    protected void dispatchClickEvent(View v) {
        if (v == mCloseView) {
            if (getDelegate() != null) getDelegate().finishRecordActivity();
        } else if (v == mRatioButton) {
            handleCameraRatio();
        } else if (v == mFlashButton) {
            mFlashEnabled = !mFlashEnabled;

            mCamera.setFlashMode(mFlashEnabled ? CameraFlash.Torch
                    : CameraFlash.Off);

            // 闪光灯
            updateFlashButtonStatus();
        } else if (v == mToggleButton) {
            mCamera.rotateCamera();
        } else if (v == mStickerButton) {
            handleStickerButton();
        } else if (v == mFilterButton) {
            handleFilterButton();
        } else if (v == mTouchView) {
            hideStickerStaff();
            hideFilterStaff();
        } else if (v == mBeautyTab) {
            showBeautySeekBar();
        } else if (v == mFilterTab) {
            showFilterLayout();
        } else if (v == mConfirmButton) {
            // 启动录制隐藏比例调节按钮
            if (mRatioButton != null)
                mRatioButton.setVisibility(VISIBLE);
            updateButtonStatus(mConfirmButton, false);
            updateButtonStatus(mRollBackButton, false);
            initProgressList();
            mCamera.finishRecording();
        } else if (v == mRollBackButton) {
            // 点击后退按钮删除上一条视频
            if (progressList.size() > 1) {
                mCamera.popVideoFragment();

                // 上一条视频所在的 position
                int positon = progressList.size() - 2;
                int progress = progressList.get(positon);

                // 删除最后一条视频
                progressList.remove(positon + 1);

                // 设置进度条显示上一条视频的进度
                mProgressBar.setProgress(progress);
                lastProgress = mProgressBar.getProgress();

                if (mProgressBar.getProgress() == 0) {
                    updateButtonStatus(mRollBackButton, false);
                    updateButtonStatus(mConfirmButton, false);
                }

                if (interuptLayout.getChildCount() != 0) {
                    interuptLayout.removeViewAt(interuptLayout.getChildCount() - 1);
                }

                updateViewStatus(false);
                updateButtonStatus(mConfirmButton, mCamera.getMovieDuration() >= mCamera.getMinRecordingTime());
            }
        } else if (v == mRecordButton) {
            if (getDelegate() == null) return;

            if (!getDelegate().isRecording()) {
                getDelegate().startRecording();
                updateShowStatus(true);
            } else {
                if (mCamera.getMovieDuration() >= Constants.MIN_RECORDING_TIME) {
                    getDelegate().stopRecording();
                    updateShowStatus(false);
                } else {
                    String msg = getStringFromResource("min_recordTime") + Constants.MIN_RECORDING_TIME + "s";
                    if (mActived)
                        TuSdk.messageHub().showToast(mContext, msg);
                }
            }
        } else if (v == mMovieImportButton) {
            handleImportButton();
        }
    }

    /**
     * 初始化存储进度集合
     */
    public void initProgressList() {
        lastProgress = 0;
        //清除所有存储进度并设置初始值
        progressList.clear();
        progressList.add(0);
        // 录制完成进度清零(断点续拍模式)
        mProgressBar.setProgress(0);
        interuptLayout.removeAllViews();
    }

    private void handleImportButton() {
        Intent intent = new Intent(mContext, MovieAlbumActivity.class);
        intent.putExtra("cutClassName", MoviePreviewAndCutActivity.class.getName());
        intent.putExtra("selectMax", 1);
        mContext.startActivity(intent);
        getDelegate().finishRecordActivity();
    }

    /**
     * 更新录制按钮显示状态
     *
     * @param isRunning 是否录制中
     */
    protected void updateShowStatus(boolean isRunning) {
        int imgID = isRunning ? getRecordSelectedDrawable() : getRecordUnselectedDrawable();

        if (mRecordButton != null)
            mRecordButton.setBackgroundResource(imgID);
    }

    /**
     * 更新闪光灯按钮显示状态
     */
    protected void updateFlashButtonStatus() {
        if (mFlashButton != null) {
            int imgID = mFlashEnabled ? getFlashSelectedDrawable() : getFlashUnselectedDrawable();

            mFlashButton.setImageResource(imgID);
        }
    }

    /**
     * 开始录制，更新视图显示状态
     */
    private void updateViewStatus(boolean isRecording) {
        updateButtonStatus(mStickerButton, !isRecording);
        updateButtonStatus(mFilterButton, !isRecording);
    }

    /**
     * 更新顶部栏视图显示状态
     *
     * @param isRecording
     */
    private void updateTopBarStatus(boolean isRecording) {
        mCloseView.setVisibility(isRecording ? View.GONE : View.VISIBLE);
        mFlashButton.setVisibility(isRecording ? View.GONE : View.VISIBLE);
        mToggleButton.setVisibility(isRecording ? View.GONE : View.VISIBLE);
    }

    /**
     * 更新按钮显示状态
     *
     * @param button
     * @param clickable
     */
    protected void updateButtonStatus(TuSdkTextButton button, boolean clickable) {
        int imgId = 0, colorId = 0;

        switch (button.getId()) {
            case R.id.lsq_confirmWrap:
                imgId = clickable ? getConfirmSelectedDrawable()
                        : getConfirmUnselectedDrawable();
                colorId = clickable ? R.color.lsq_filter_title_color : R.color.lsq_filter_title_unselected_color;
                button.setClickable(clickable);
                break;

            case R.id.lsq_backWrap:
                imgId = clickable ? getCancelSelectedDrawable()
                        : getCancelUnselectedDrawable();
                colorId = clickable ? R.color.lsq_filter_title_color : R.color.lsq_filter_title_unselected_color;
                button.setClickable(clickable);
                break;

            case R.id.lsq_stickerWrap:
                imgId = clickable ? getStickerSelectedDrawable()
                        : getStickerUnselectedDrawable();
                colorId = clickable ? R.color.lsq_filter_title_color : R.color.lsq_filter_title_unselected_color;
                button.setClickable(clickable);
                break;

            case R.id.lsq_tab_filter_btn:
                imgId = clickable ? getFilterSelectedDrawable()
                        : getFilterUnselectedDrawable();
                colorId = clickable ? R.color.lsq_filter_title_color : R.color.lsq_filter_title_unselected_color;
                button.setClickable(clickable);
                break;

            default:
                break;
        }

        updateButtonStyle(button, imgId, colorId, clickable);
    }

    /**
     * 更新按钮的样式：背景图 文字颜色
     *
     * @param button
     * @param imgId
     * @param colorId
     */
    protected void updateButtonStyle(TuSdkTextButton button, int imgId, int colorId, boolean clickable) {
        Drawable drawable = TuSdkContext.getDrawable(imgId);
        button.setCompoundDrawables(null, drawable, null, null);
        button.setTextColor(TuSdkContext.getColor(colorId));
    }

    /**
     * 滤镜按钮选中时的图标
     *
     * @return
     */
    protected int getFilterSelectedDrawable() {
        return R.drawable.auto_selected;
    }

    /**
     * 滤镜按钮未选中时的图标
     *
     * @return
     */
    protected int getFilterUnselectedDrawable() {
        return R.drawable.auto_default;
    }

    /**
     * 贴纸按钮选中时的图标
     *
     * @return
     */
    protected int getStickerSelectedDrawable() {
        return R.drawable.lsq_style_default_btn_sticker;
    }

    /**
     * 贴纸按钮未选中时的图标
     *
     * @return
     */
    protected int getStickerUnselectedDrawable() {
        return R.drawable.lsq_style_default_btn_sticker_unselected;
    }

    /**
     * 撤销按钮选中时的图标
     *
     * @return
     */
    protected int getCancelSelectedDrawable() {
        return R.drawable.lsq_style_default_btn_back_selected;
    }

    /**
     * 撤销按钮未选中时的图标
     *
     * @return
     */
    protected int getCancelUnselectedDrawable() {
        return R.drawable.lsq_style_default_btn_back_unselected;
    }

    /**
     * 确认按钮选中时的图标
     *
     * @return
     */
    protected int getConfirmSelectedDrawable() {
        return R.drawable.lsq_style_default_btn_finish_selected;
    }

    /**
     * 确认按钮未选中时的图标
     *
     * @return
     */
    protected int getConfirmUnselectedDrawable() {
        return R.drawable.lsq_style_default_btn_finish_unselected;
    }

    /**
     * 录制按钮选中时的图标
     *
     * @return
     */
    protected int getRecordSelectedDrawable() {
        return R.drawable.lsq_style_default_record_btn_record_selected;
    }

    /**
     * 录制按钮未选中时的图标
     *
     * @return
     */
    protected int getRecordUnselectedDrawable() {
        return R.drawable.lsq_style_default_record_btn_record_unselected;
    }

    /**
     * 闪光灯按钮选中时的图标
     *
     * @return
     */
    protected int getFlashSelectedDrawable() {
        return R.drawable.lsq_style_default_btn_flash_on;
    }

    /**
     * 闪光灯按钮未选中时的图标
     *
     * @return
     */
    protected int getFlashUnselectedDrawable() {
        return R.drawable.lsq_style_default_btn_flash_off;
    }

    // 添加视频断点标记
    private void addInteruptPoint(float margingLeft) {
        // 添加断点标记
        Button interuptBtn = new Button(mContext);
        LayoutParams lp = new LayoutParams(2,
                LayoutParams.MATCH_PARENT);

        interuptBtn.setBackgroundColor(TuSdkContext.getColor("lsq_progress_interupt_color"));
        lp.setMargins((int) Math.ceil(margingLeft), 0, 0, 0);
        interuptBtn.setLayoutParams(lp);
        interuptLayout.addView(interuptBtn);
    }

    /**
     * 设置当前录制的时间及进度条进度
     */
    private void setTimeProgress(float currentDuration, float progress) {
        mProgressBar.setProgress((int) Math.ceil(progress * 100));
    }

    /**
     * 开始录制时更新视图显示
     *
     * @param isRecording
     */
    public void updateViewOnStartRecording(boolean isRecording) {
        updateViewStatus(isRecording);
        updateTopBarStatus(isRecording);
        updateButtonStatus(mConfirmButton, false);
    }

    /**
     * 暂停录制时更新视图显示
     *
     * @param isClickable
     */
    public void updateViewOnPauseRecording(boolean isClickable) {
        updateShowStatus(false);
        updateButtonStatus(mStickerButton, true);
        updateButtonStatus(mFilterButton, true);
        //录制时长大于最小限制时长且非录制状态下确认按钮才可以点击
        updateButtonStatus(mConfirmButton, isClickable);
    }

    /**
     * 停止录制时更新视图显示
     *
     * @param isRecording
     */
    public void updateViewOnStopRecording(boolean isRecording) {
        updateTopBarStatus(isRecording);
    }

    /**
     * 录制完成时更新视图显示
     *
     * @param isRecording
     */
    public void updateViewOnMovieRecordComplete(boolean isRecording) {
        TuSdk.messageHub().dismissRightNow();
        String msg = getStringFromResource("new_movie_saved");
        if (mActived)
            TuSdk.messageHub().showSuccess(mContext, msg);

        updateShowStatus(isRecording);
        updateViewStatus(isRecording);
        updateTopBarStatus(isRecording);

        // 录制完进度清零(正常录制模式)
        mProgressBar.setProgress(0);
    }

    /**
     * 录制错误时更新视图显示
     *
     * @param error
     * @param isRecording
     */
    public void updateViewOnMovieRecordFailed(RecordError error, boolean isRecording) {
        if (error == RecordError.MoreMaxDuration) // 超过最大时间 （超过最大时间是再次调用startRecording时会调用）
        {
            String msg = getStringFromResource("over_max_recordTime");
            if (mActived)
                TuSdk.messageHub().showError(mContext, msg);
            updateButtonStatus(mConfirmButton, true);

        } else if (error == RecordError.SaveFailed) // 视频保存失败
        {
            String msg = getStringFromResource("new_movie_error_saving");
            if (mActived)
                TuSdk.messageHub().showError(mContext, msg);
            updateButtonStatus(mConfirmButton, true);
        } else if (error == RecordError.InvalidRecordingTime) {
            if (mActived)
                TuSdk.messageHub().showError(mContext, R.string.lsq_record_time_invalid);
        }


        updateShowStatus(isRecording);
        updateViewStatus(isRecording);
    }

    /**
     * 录制状态发生改变时更新视图显示
     *
     * @param state
     * @param isRecording
     */
    public void updateViewOnMovieRecordStateChanged(RecordState state, boolean isRecording) {
        if (state == RecordState.Recording) // 开始录制
        {
            updateShowStatus(isRecording);
            updateButtonStatus(mRollBackButton, false);

        } else if (state == RecordState.Paused) // 已暂停录制
        {
            if (mProgressBar.getProgress() != 0) {
                addInteruptPoint(TuSdkContext.getDisplaySize().width * mProgressBar.getProgress() / 100);
            }

            // 存储每段视频结束时进度条的进度
            lastProgress = mProgressBar.getProgress();
            progressList.add(lastProgress);
            updateButtonStatus(mRollBackButton, true);
            updateShowStatus(false);
        } else if (state == RecordState.RecordCompleted) //录制完成弹出提示（续拍模式下录过程中超过最大时间时调用）
        {
            String msg = getStringFromResource("max_recordTime") + Constants.MAX_RECORDING_TIME + "s";
            if (mActived)
                TuSdk.messageHub().showToast(mContext, msg);

            updateShowStatus(false);
            updateButtonStatus(mConfirmButton, true);

        } else if (state == RecordState.Saving) // 正在保存视频
        {
            String msg = getStringFromResource("new_movie_saving");
            if (mActived)
                TuSdk.messageHub().setStatus(mContext, msg);
        }
    }

    /**
     * 录制进度改变时更新视图显示
     *
     * @param progress
     * @param durationTime
     */
    public void updateViewOnMovieRecordProgressChanged(float progress, float durationTime) {
        setTimeProgress(durationTime, progress);
    }

    /**
     * 显示贴纸视图
     */
    protected void handleStickerButton() {
        showStickerLayout();
    }

    /**
     * 改变屏幕比例 录制状态不可改变
     */
    private void handleCameraRatio() {
        if (mCamera == null || !mCamera.canChangeRatio()) return;

        int type = RatioType.nextRatioType(this.getRatioType(), mCurrentRatioType);
        this.setCurrentRatioType(type);

        // 设置预览区域顶部偏移量 必须在 changeRegionRatio 之前设置
        mCamera.getRegionHandler().setOffsetTopPercent(getPreviewOffsetTopPercent(type));
        mCamera.changeRegionRatio(RatioType.ratio(type));

        // 计算保存比例
        mCamera.getVideoEncoderSetting().videoSize = TuSdkSize.create((int) (mCamera.getCameraPreviewSize().width * RatioType.ratio(type)), mCamera.getCameraPreviewSize().width);
    }

    /**
     * 更新底部栏的显示状态
     */
    private void updateBottomBar(boolean isHidden) {
        mBottomBar.setVisibility(isHidden ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * 显示滤镜视图
     */
    protected void handleFilterButton() {
        showSmartBeautyLayout();
    }

    /**
     * 显示贴纸底部栏
     */
    public void showStickerLayout() {
        updateStickerViewStaff(true);

        // 滤镜栏向上动画并显示
        ViewCompat.setTranslationY(mStickerBottomView,
                mStickerBottomView.getHeight());
        ViewCompat.animate(mStickerBottomView).translationY(0).setDuration(200).setListener(mViewPropertyAnimatorListener);
    }

    /**
     * 属性动画监听事件
     */
    private ViewPropertyAnimatorListener mViewPropertyAnimatorListener = new ViewPropertyAnimatorListener() {

        @Override
        public void onAnimationCancel(View view) {
        }

        @Override
        public void onAnimationEnd(View view) {
            updateBottomBar(true);
            ViewCompat.animate(mStickerBottomView).setListener(null);
            ViewCompat.animate(mFilterBottomView).setListener(null);
        }

        @Override
        public void onAnimationStart(View view) {
        }
    };

    /**
     * 更新贴纸栏相关视图的显示状态
     *
     * @param isShow
     */
    private void updateStickerViewStaff(boolean isShow) {
        mStickerBottomView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        mTouchView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 点击贴纸栏上方的空白区域隐藏贴纸栏
     */
    public void hideStickerStaff() {
        if (mStickerBottomView.getVisibility() == View.INVISIBLE) return;

        updateStickerViewStaff(false);

        // 滤镜栏向下动画并隐藏
        ViewCompat.animate(mStickerBottomView)
                .translationY(mStickerBottomView.getHeight()).setDuration(200);

        updateBottomBar(false);
    }

    /**
     * 显示智能美颜底部栏
     */
    public void showSmartBeautyLayout() {
        updateFilterViewStaff(true);

        // 滤镜栏向上动画并显示
        ViewCompat.setTranslationY(mFilterBottomView,
                mFilterBottomView.getHeight());
        ViewCompat.animate(mFilterBottomView).translationY(0).setDuration(200).setListener(mViewPropertyAnimatorListener);
        showBeautySeekBar();

    }

    /**
     * 点击滤镜栏上方的空白区域隐藏滤镜栏
     */
    public void hideFilterStaff() {
        if (mFilterBottomView.getVisibility() == View.INVISIBLE) return;

        updateFilterViewStaff(false);

        // 滤镜栏向下动画并隐藏
        ViewCompat.animate(mFilterBottomView)
                .translationY(mFilterBottomView.getHeight()).setDuration(200);
        updateBottomBar(false);
    }

    /**
     * 更新滤镜栏相关视图的显示状态
     *
     * @param isShow
     */
    private void updateFilterViewStaff(boolean isShow) {
        mRecordButton.setVisibility(isShow ? View.INVISIBLE : View.VISIBLE);
        mFilterButton.setVisibility(isShow ? View.INVISIBLE : View.VISIBLE);
        mStickerButton.setVisibility(isShow ? View.INVISIBLE : View.VISIBLE);

        mFilterBottomView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        mTouchView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 初始化滤镜栏视图
     */
    protected void initFilterListView() {
        getFilterListView();

        this.mFilterListView.setModeList(Arrays.asList(Constants.VIDEOFILTERS));
        ThreadHelper.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!mIsFirstEntry) return;

                mIsFirstEntry = false;
                changeVideoFilterCode(Arrays.asList(Constants.VIDEOFILTERS).get(mFocusPostion));
            }

        }, 1000);
    }

    /**
     * 滤镜栏视图
     *
     * @return
     */
    public FilterListView getFilterListView() {
        if (mFilterListView == null) {
            mFilterListView = (FilterListView) findViewById(R.id.lsq_filter_list_view);
            mFilterListView.loadView();
            mFilterListView.setCellLayoutId(R.layout.filter_list_cell_view);
            mFilterListView.setCellWidth(TuSdkContext.dip2px(62));
            mFilterListView.setItemClickDelegate(mFilterTableItemClickDelegate);
            mFilterListView.reloadData();
            mFilterListView.selectPosition(mFocusPostion);
        }
        return mFilterListView;
    }

    /**
     * 初始化贴纸组视图
     */
    protected void initStickerListView() {
        getStickerListView();

        refetchStickerList();
    }

    /**
     * 刷新本地贴纸列表
     */
    public void refetchStickerList() {
        if (mStickerListView == null) return;

        List<StickerGroup> groups = new ArrayList<StickerGroup>();
        groups.addAll(getRawStickGroupList());
        groups.add(0, new StickerGroup());

        mStickerListView.setModeList(groups);
        mStickerListView.reloadData();
    }

    /**
     * 获取本地贴纸列表
     *
     * @return
     */
    public List<StickerGroup> getRawStickGroupList() {
        List<StickerGroup> list = new ArrayList<StickerGroup>();
        try {
            InputStream stream = getResources().openRawResource(R.raw.square_sticker);
            if (!isSquareSticker)
                stream = getResources().openRawResource(R.raw.full_screen_sticker);

            if (stream == null) return null;

            byte buffer[] = new byte[stream.available()];
            stream.read(buffer);
            String json = new String(buffer, "UTF-8");

            JSONObject jsonObject = JsonHelper.json(json);
            JSONArray jsonArray = jsonObject.getJSONArray("stickerGroups");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                StickerGroup group = new StickerGroup();
                group.groupId = item.optLong("id");
                group.previewName = item.optString("previewImage");
                group.name = item.optString("name");
                list.add(group);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 贴纸组视图
     */
    public StickerListView getStickerListView() {
        if (mStickerListView == null) {
            mStickerListView = (StickerListView) findViewById(R.id.lsq_sticker_list_view);
            mStickerListView.loadView();
            mStickerListView.setCellLayoutId(R.layout.sticker_list_cell_view);
            GridLayoutManager grid = new GridLayoutManager(mContext, 5);
            mStickerListView.setLayoutManager(grid);


            mStickerListView.setCellWidth(TuSdkContext.dip2px(80));
            mStickerListView.setItemClickDelegate(mStickerTableItemClickDelegate);
            mStickerListView.reloadData();
        }
        return mStickerListView;
    }

    /**
     * 滤镜组列表点击事件
     */
    private TuSdkTableViewItemClickDelegate<String, FilterCellView> mFilterTableItemClickDelegate = new TuSdkTableViewItemClickDelegate<String, FilterCellView>() {
        @Override
        public void onTableViewItemClick(String itemData,
                                         FilterCellView itemView, int position) {
            onFilterGroupSelected(itemData, itemView, position);
        }
    };

    /**
     * 贴纸组列表点击事件
     */
    private TuSdkTableView.TuSdkTableViewItemClickDelegate<StickerGroup, StickerCellView> mStickerTableItemClickDelegate = new TuSdkTableView.TuSdkTableViewItemClickDelegate<StickerGroup, StickerCellView>() {
        @Override
        public void onTableViewItemClick(StickerGroup itemData,
                                         StickerCellView itemView, int position) {
            onStickerGroupSelected(itemData, itemView, position);
        }
    };

    /**
     * 滤镜配置视图
     *
     * @return
     */
    private FilterConfigView getFilterConfigView() {
        if (mConfigView == null) {
            mConfigView = (FilterConfigView) findViewById(R.id.lsq_filter_config_view);
        }

        return mConfigView;
    }

    /**
     * 滤镜组选择事件
     *
     * @param itemData
     * @param itemView
     * @param position
     */
    protected void onFilterGroupSelected(String itemData,
                                         FilterCellView itemView, int position) {
        FilterCellView prevCellView = (FilterCellView) mFilterListView.findViewWithTag(mFocusPostion);
        mFocusPostion = position;
        changeVideoFilterCode(itemData);
        mFilterListView.selectPosition(mFocusPostion);
        deSelectLastFilter(prevCellView);
        selectFilter(itemView, position);
        getFilterConfigView().setVisibility((position == 0) ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * 滤镜选中状态
     *
     * @param itemView
     * @param position
     */
    private void selectFilter(FilterCellView itemView, int position) {
        updateFilterBorderView(itemView, false);
        itemView.setFlag(position);
        TextView titleView = itemView.getTitleView();
        titleView.setBackground(TuSdkContext.getDrawable(R.drawable.tusdk_view_filter_selected_text_roundcorner));
    }

    /**
     * 取消上一个滤镜的选中状态
     *
     * @param lastFilter
     */
    private void deSelectLastFilter(FilterCellView lastFilter) {
        if (lastFilter == null) return;

        updateFilterBorderView(lastFilter, true);
        lastFilter.getTitleView().setBackground(TuSdkContext.getDrawable(R.drawable.tusdk_view_filter_unselected_text_roundcorner));
        lastFilter.getImageView().invalidate();
    }

    /**
     * 设置滤镜单元边框是否可见
     *
     * @param lastFilter
     * @param isHidden
     */
    private void updateFilterBorderView(FilterCellView lastFilter, boolean isHidden) {
        View filterBorderView = lastFilter.getBorderView();
        filterBorderView.setVisibility(isHidden ? View.GONE : View.VISIBLE);
    }

    /**
     * 获取字符串资源
     *
     * @param fieldName
     * @return
     */
    protected String getStringFromResource(String fieldName) {
        int stringID = this.getResources().getIdentifier(fieldName, "string",
                this.mContext.getPackageName());

        return getResources().getString(stringID);
    }

    /**
     * 贴纸组选择事件
     *
     * @param itemData
     * @param itemView
     * @param position
     */
    protected void onStickerGroupSelected(StickerGroup itemData,
                                          StickerCellView itemView, int position) {
        // 设置点击贴纸时呈现或是隐藏贴纸
        if (position == 0) {
            mCamera.removeAllLiveSticker();
            mStickerListView.setSelectedPosition(position);
            return;
        }

        // 如果贴纸已被下载到本地
        if (mStickerListView.isDownloaded(itemData)) {
            mStickerListView.setSelectedPosition(position);
            // 必须重新获取StickerGroup,否则itemData.stickers为null
            itemData = StickerLocalPackage.shared().getStickerGroup(itemData.groupId);
            mCamera.showGroupSticker(itemData);
        } else {
            mStickerListView.downloadStickerGroup(itemData);
        }
    }

    /**
     * 传递录制相机对象
     *
     * @param camera
     */
    public void setUpCamera(Context context, TuSDKRecordVideoCamera camera) {
        this.mContext = context;
        this.mCamera = camera;

        mCamera.setDelegate(mVideoCameraDelegate);

        // 根据录制模式不同，给开始录制按钮加上不同的监听事件
        if (mCamera.getRecordMode() == RecordMode.Normal) {
            mRecordButton.setOnClickListener(mButtonClickListener);

        } else if (mCamera.getRecordMode() == RecordMode.Keep) {

            mRecordButton.setOnTouchListener(mOnTouchListener);
        }
    }

    /**
     * 切换滤镜
     *
     * @param code
     */
    protected void changeVideoFilterCode(final String code) {
        if (mCamera != null && mCamera.getState() != CameraState.StateUnknow
                && mCamera.getState() != CameraState.StateStarting) {
            ThreadHelper.runThread(new Runnable() {

                @Override
                public void run() {
                    mCamera.switchFilter(code);
                }
            });
        } else {
            ThreadHelper.postDelayed(new Runnable() {

                @Override
                public void run() {

                    ThreadHelper.runThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mCamera != null
                                    && mCamera.getState() != CameraState.StateUnknow
                                    && mCamera.getState() != CameraState.StateStarting)
                                mCamera.switchFilter(code);
                        }
                    });
                }

            }, 800);
        }
    }

    /**
     * 滤镜效果改变监听事件
     */
    protected TuSDKVideoCameraDelegate mVideoCameraDelegate = new TuSDKVideoCameraDelegate() {
        @Override
        public void onFilterChanged(FilterWrap selesOutInput) {
            if (selesOutInput == null) return;

            // 默认滤镜参数调节
            SelesParameters params = selesOutInput.getFilterParameter();
            List<FilterArg> list = params.getArgs();
            for (FilterArg arg : list) {
                if (arg.equalsKey("smoothing") && mSmoothingProgress != -1.0f)
                    arg.setPrecentValue(mSmoothingProgress * 0.7f);
                else if (arg.equalsKey("smoothing") && mSmoothingProgress == -1.0f)
                    mSmoothingProgress = arg.getPrecentValue();
                else if (arg.equalsKey("mixied") && mMixiedProgress != -1.0f)
                    arg.setPrecentValue(mMixiedProgress * 0.7f);
                else if (arg.equalsKey("mixied") && mMixiedProgress == -1.0f)
                    mMixiedProgress = arg.getPrecentValue();
                else if (arg.equalsKey("eyeSize") && mEyeSizeProgress != -1.0f)
                    arg.setPrecentValue(mEyeSizeProgress * 0.7f);
                else if (arg.equalsKey("chinSize") && mChinSizeProgress != -1.0f)
                    arg.setPrecentValue(mChinSizeProgress*0.4f);
                else if (arg.equalsKey("eyeSize") && mEyeSizeProgress == -1.0f)
                    mEyeSizeProgress = arg.getPrecentValue();
                else if (arg.equalsKey("chinSize") && mChinSizeProgress == -1.0f)
                    mChinSizeProgress = arg.getPrecentValue();
                else if (arg.equalsKey("whitening") && mWhiteningProgress != -1.0f)
                    arg.setPrecentValue(mWhiteningProgress*0.6f);
            }
            selesOutInput.setFilterParameter(params);

            mSelesOutInput = selesOutInput;

            if (getFilterConfigView() != null)
                getFilterConfigView().setSelesFilter(mSelesOutInput.getFilter());

            if (mIsFirstEntry || (mBeautyLayout != null && mBeautyLayout.getVisibility() == View.VISIBLE)) {
                mIsFirstEntry = false;
                showBeautySeekBar();
            }
        }

        @Override
        public void onVideoCameraStateChanged(SelesVideoCameraInterface camera, CameraState newState) {
        }

        @Override
        public void onVideoCameraScreenShot(SelesVideoCameraInterface camera, Bitmap bitmap) {

        }
    };

    /**
     * 滤镜拖动条监听事件
     */
    private FilterConfigViewSeekBarDelegate mConfigSeekBarDelegate = new FilterConfigViewSeekBarDelegate() {

        @Override
        public void onSeekbarDataChanged(FilterConfigSeekbar seekbar, FilterArg arg) {
            if (arg == null) return;

            if (arg.equalsKey("smoothing"))
                mSmoothingProgress = arg.getPrecentValue();
            else if (arg.equalsKey("eyeSize"))
                mEyeSizeProgress = arg.getPrecentValue();
            else if (arg.equalsKey("chinSize"))
                mChinSizeProgress = arg.getPrecentValue();
            else if (arg.equalsKey("mixied"))
                mMixiedProgress = arg.getPrecentValue();
            else if(arg.equalsKey("whitening"))
                mWhiteningProgress = arg.getPrecentValue();
        }

    };

    /**
     * 拖动条监听事件
     */
    private TuSeekBar.TuSeekBarDelegate mTuSeekBarDelegate = new TuSeekBar.TuSeekBarDelegate() {
        @Override
        public void onTuSeekBarChanged(TuSeekBar seekBar, float progress) {
            if (seekBar == mSmoothingBarLayout.getSeekbar()) {
                mSmoothingProgress = progress;
                applyFilter(mSmoothingBarLayout, "smoothing", progress);
            } else if (seekBar == mEyeSizeBarLayout.getSeekbar()) {
                mEyeSizeProgress = progress;
                applyFilter(mEyeSizeBarLayout, "eyeSize", progress);
            } else if (seekBar == mChinSizeBarLayout.getSeekbar()) {
                mChinSizeProgress = progress;
                applyFilter(mChinSizeBarLayout, "chinSize", progress);
            }
        }
    };

    private void applyFilter(ConfigViewSeekBar viewSeekBar, String key, float progress) {
        if (viewSeekBar == null || mSelesOutInput == null) return;

        viewSeekBar.getConfigValueView().setText((int) (progress * 100) + "%");
        SelesParameters params = mSelesOutInput.getFilterParameter();
        params.setFilterArg(key, progress);
        mSelesOutInput.submitFilterParameter();
    }

    public void setSquareSticker(boolean isSquareSticker) {
        this.isSquareSticker = isSquareSticker;
        refetchStickerList();
    }

}